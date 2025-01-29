package eu.highgeek.highgeekproxy.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.scheduler.Scheduler;
import eu.highgeek.highgeekproxy.HighgeekProxy;
import eu.highgeek.highgeekproxy.objects.Message;
import lombok.Getter;
import redis.clients.jedis.*;

import java.util.concurrent.TimeUnit;

@Getter
public class RedisManager {

    private volatile boolean running = true;
    private final static String host = HighgeekProxy.getInstance().config.getRedisIp();
    private final static int port = HighgeekProxy.getInstance().config.getRedisPort();

    private final static HostAndPort node = HostAndPort.from(host + ":" + port);

    private final static JedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
            .resp3() // RESP3 protocol
            .build();

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final JedisPooled jedisPooled;
    private final UnifiedJedis unifiedJedis;

    private final RedisSubscriber subscriber;

    public RedisManager(){
        this.unifiedJedis  = new UnifiedJedis(node, clientConfig);
        this.jedisPooled  = new JedisPooled(host,port);

        this.subscriber = new RedisSubscriber();

        HighgeekProxy.getInstance().server.getScheduler().buildTask(HighgeekProxy.getInstance(), () -> {
            startSubscriber();
        }).delay(1L, TimeUnit.SECONDS).schedule();
    }

    public void addChatEntry(Message message){
        jedisPooled.set(message.getUuid(), gson.toJson(message));
    }

    private void startSubscriber(){
        while (running) {  // Infinite loop for reconnection handling
            try (Jedis jedis = new Jedis(host, port)) {
                HighgeekProxy.getInstance().logger.info("Connected to Redis, waiting for messages...");
                jedis.psubscribe(subscriber, "*");
            } catch (Exception e) {
                if (!running) break; // Exit loop if shutting down
                HighgeekProxy.getInstance().logger.info("Connection lost, retrying in 100 ms...");
                try {
                    Thread.sleep(100); // Wait before reconnecting
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
    public void stopSubscriber() {
        running = false; // Set flag to stop reconnection attempts
        if (subscriber != null) {
            subscriber.punsubscribe(); // Stop Jedis subscription
        }
        HighgeekProxy.getInstance().logger.info("Subscriber has been shut down.");
    }
}
