package eu.highgeek.highgeekproxy.redis;

import eu.highgeek.highgeekproxy.HighgeekProxy;
import org.slf4j.Logger;
import redis.clients.jedis.JedisPubSub;

public class RedisSubscriber extends JedisPubSub{

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        HighgeekProxy.getInstance().logger.info("Received message: " + message + " on channel: " + channel);
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        HighgeekProxy.getInstance().logger.info("Subscribed to: " + pattern);
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        HighgeekProxy.getInstance().logger.info("Unsubscribed from: " + pattern);
    }
}
