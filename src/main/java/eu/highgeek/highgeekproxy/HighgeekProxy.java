package eu.highgeek.highgeekproxy;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import eu.highgeek.highgeekproxy.listeners.PlayerJoinListener;
import eu.highgeek.highgeekproxy.listeners.PlayerLeaveListener;
import eu.highgeek.highgeekproxy.redis.HighgeekConfig;
import eu.highgeek.highgeekproxy.redis.RedisManager;
import lombok.Getter;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(
        id = "highgeekproxy",
        name = "HighgeekProxy",
        version = "0.0.1-SNAPSHOT",
        url = "highgeek.eu",
        authors = "CursedHawk"
)
public class HighgeekProxy {

    public ProxyServer server;
    public HighgeekConfig config;
    public final Logger logger;
    public final Path dataDirectory;

    @Getter
    private static HighgeekProxy instance;

    @Getter
    private static RedisManager redisManager;

    private void init(ProxyInitializeEvent event) throws IOException {
        //Config
        Files.createDirectories(dataDirectory);
        Path configPath = dataDirectory.resolve("HighgeekProxy.toml");
        config = HighgeekConfig.read(configPath);

        //Init services
        HighgeekProxy.redisManager = new RedisManager();

        //Register events
        server.getEventManager().register(this, new PlayerJoinListener());
        server.getEventManager().register(this, new PlayerLeaveListener());
    }

    @Inject
    public HighgeekProxy(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory){
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.logger.info("HighgeekProxy loading!");
        HighgeekProxy.instance = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        try {
            init(event);
        }
        catch (Exception e) {
            logger.error("An error prevented HighgeekProxy to load correctly: "+ e.toString());
        }
    }
}
