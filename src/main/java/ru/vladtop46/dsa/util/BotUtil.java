package ru.vladtop46.dsa.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.plugin.Plugin;

public class BotUtil {

    private static Plugin plugin;
    private static JDA jda;

    public void initUtil(Plugin plugin, JDA jda) {
        BotUtil.plugin = plugin;
        BotUtil.jda = jda;
    }

    public void updateBotActivity() {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000);

                int onlinePlayers = plugin.getServer().getOnlinePlayers().size();
                int maxPlayers = plugin.getServer().getMaxPlayers();
                String activityDescription = "Онлайн: " + onlinePlayers + "/" + maxPlayers;
                jda.getPresence().setActivity(Activity.playing(String.format("Minecraft на сервере - %s", activityDescription)));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
    }
}
