package eu.highgeek.highgeekproxy.listeners;

import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import eu.highgeek.highgeekproxy.HighgeekProxy;
import eu.highgeek.highgeekproxy.objects.Message;

import java.time.Instant;
import java.util.UUID;

public class PlayerLeaveListener {

    @Subscribe
    public EventTask onPlayerChat(DisconnectEvent event) {
        return EventTask.async(() -> processAsync(event));
    }

    public void processAsync(DisconnectEvent event){
        HighgeekProxy.getInstance().logger.info("Player left proxy!: " + event.getPlayer().getUsername());

        String time =  Instant.now().toString();
        String uuid = "chat:logs:"+time.replaceAll(":", "-")+":server";

        HighgeekProxy.getRedisManager().addChatEntry(
                new Message(
                        uuid,
                        event.getPlayer().getUsername(),
                        event.getPlayer().getUsername(),
                        "se odpojil ze serveru.",
                        "default",
                        time,
                        "logs",
                        "&7Logout",
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
