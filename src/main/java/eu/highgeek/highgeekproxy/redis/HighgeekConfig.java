package eu.highgeek.highgeekproxy.redis;

import com.google.gson.annotations.Expose;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import lombok.Getter;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

public class HighgeekConfig {

    @Getter
    @Expose
    private String redisIp = "redisIp";
    @Getter
    @Expose
    private int redisPort = 6379;

    private HighgeekConfig(String redisIp, int redisPort){
        this.redisIp = redisIp;
        this.redisPort = redisPort;
    }

    public static HighgeekConfig read(Path path) throws IOException {
        URL defaultConfigLocation = HighgeekConfig.class.getClassLoader()
                .getResource("default-config.toml");
        if (defaultConfigLocation == null) {
            throw new RuntimeException("Default configuration file does not exist.");
        }
        CommentedFileConfig config = CommentedFileConfig.builder(path)
                .defaultData(defaultConfigLocation)
                .autosave()
                .preserveInsertionOrder()
                .sync()
                .build();
        config.load();


        String redisIp = config.getOrElse("redis-ip", "0.0.0.0");
        int redisPort = Integer.parseInt(config.getOrElse("redis-port", "6379"));


        return new HighgeekConfig(redisIp, redisPort);
    }

}
