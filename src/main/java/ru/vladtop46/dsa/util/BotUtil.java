package ru.vladtop46.dsa.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.plugin.Plugin;

import java.awt.*;
import java.time.Instant;

public class BotUtil {

    private static Plugin plugin;
    private static JDA jda;
    private static String logChannelId;

    public void initUtil(Plugin plugin, JDA jda, String logChannelId) {
        BotUtil.plugin = plugin;
        BotUtil.jda = jda;
        BotUtil.logChannelId = logChannelId;
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

    public void logMessage(String message, Color color) {
        TextChannel channel = jda.getTextChannelById(logChannelId);
        if (channel != null) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setDescription(message)
                    .setColor(color).setTimestamp(Instant.now());
            channel.sendMessageEmbeds(embedBuilder.build()).queue();
        } else {
            System.out.println("Log channel not found!");
        }
    }
}
