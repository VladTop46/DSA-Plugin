package ru.vladtop46.dsa.handler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.vladtop46.dsa.util.BotUtil;

public class Events implements Listener {

    private static BotUtil botUtil;

    public void init(BotUtil botUtil) {
        Events.botUtil = botUtil;
        botUtil.updateBotActivity();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        botUtil.updateBotActivity();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        botUtil.updateBotActivity();
    }
}
