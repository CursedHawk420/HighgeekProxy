package eu.highgeek.highgeekproxy.listeners;

import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import eu.highgeek.highgeekproxy.HighgeekProxy;
import eu.highgeek.highgeekproxy.objects.Message;

import java.util.UUID;
import java.time.Instant;

public class PlayerJoinListener {

    @Subscribe
    public EventTask onPlayerChat(LoginEvent event) {
        return EventTask.async(() -> processAsync(event));
    }

    public void processAsync(LoginEvent event){
        HighgeekProxy.getInstance().logger.info("Player logged to proxy!: " + event.getPlayer().getUsername());

        String time =  Instant.now().toString();
        String uuid = "chat:deaths:"+time.replaceAll(":", "-")+":server";

        HighgeekProxy.getRedisManager().addChatEntry(
                new Message(
                        uuid,
                        event.getPlayer().getUsername(),
                        event.getPlayer().getUsername(),
                        "se pripojil k serveru.",
                        "default",
                        time,
                        "global",
                        "&7Login",
                        "game",
                        "",
                        "&7",
                        "&f",
                        UUID.fromString("00000000-0000-0000-0000-000000000000"),
                        "&7Net"
                )
        );
    }
}
