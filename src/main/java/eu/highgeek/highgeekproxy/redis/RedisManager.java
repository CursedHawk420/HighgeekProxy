package eu.highgeek.highgeekproxy.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.highgeek.highgeekproxy.HighgeekProxy;
import eu.highgeek.highgeekproxy.objects.Message;
import lombok.Getter;
import redis.clients.jedis.*;

@Getter
public class RedisManager {

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

    public RedisManager(){
        this.unifiedJedis  = new UnifiedJedis(node, clientConfig);
        this.jedisPooled  = new JedisPooled(host,port);
    }

    public void addChatEntry(Message message){
        jedisPooled.set(message.getUuid(), gson.toJson(message));
    }
}
