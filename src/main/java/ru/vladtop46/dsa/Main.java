package ru.vladtop46.dsa;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vladtop46.dsa.discord.DiscordBot;
import ru.vladtop46.dsa.handler.Events;
import ru.vladtop46.dsa.util.BotUtil;
import ru.vladtop46.dsa.util.LogUtil;

import java.awt.*;
import java.util.List;

import javax.security.auth.login.LoginException;

public class Main extends JavaPlugin {

    private Plugin plugin;
    private LogUtil logger;
    private static JDA jda;
    private static final BotUtil botUtil = new BotUtil();
    private static final Events eventHandler = new Events();

    @Override
    public void onEnable() {
        plugin = this;
        logger = LogUtil.getInstance(plugin);
        saveDefaultConfig();
        messageOnEnable();
        initializeDiscordBot();

        String logChannelId = getConfig().getString("discord.log_channel_id");

        botUtil.initUtil(plugin, jda, logChannelId);
        eventHandler.init(botUtil);
        Bukkit.getPluginManager().registerEvents(eventHandler, plugin);
        botUtil.logMessage(":green_circle: Сервер был включён!", Color.GREEN);
    }

    @Override
    public void onDisable() {
        getLogger().info("DSA Disabled!");
        DiscordBot.shutdown();

        botUtil.logMessage(":red_circle: Сервер был отключён!", Color.RED);

        jda = null;
        logger = null;
        plugin = null;
    }

    private void messageOnEnable() {
        PluginDescriptionFile pdfFile = getDescription();
        String pluginVersion = pdfFile.getVersion();

        String coreVersion = Bukkit.getVersion();

        logger.logColored("");
        logger.logColored("  &d  ___&5 __      __");
        logger.logColored("  &d | __&5 \\ \\    / /    &bDiscord Administration Plugin &av" + pluginVersion);
        logger.logColored("  &d | _|&5  \\ \\/\\/ /     &9Running on &b" + coreVersion);
        logger.logColored("  &d |_| &5   \\_/\\_/  ");
        logger.logColored("");
    }

    private boolean createBot(String token) {
        try {
            jda = JDABuilder.createDefault(token)
                    .setActivity(Activity.playing("Minecraft"))
                    .addEventListeners(new DiscordBot())
                    .build();
            return true;
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeDiscordBot() {
        String token = getConfig().getString("discord.token");
        String guildId = getConfig().getString("discord.guild_id");
        String adminChannelId = getConfig().getString("discord.admin_channel_id");
        List<String> allowedRoles = getConfig().getStringList("discord.allowed_roles");

        if (token == null || token.isEmpty()) {
            logger.logError("Discord bot token is missing in config.yml. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (guildId == null || guildId.isEmpty()) {
            logger.logError("Discord guild ID is missing in config.yml. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (adminChannelId == null || adminChannelId.isEmpty()) {
            logger.logError("Discord admin channel ID is missing in config.yml. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        boolean initialized = createBot(token) && DiscordBot.initialize(jda, guildId, adminChannelId, allowedRoles, plugin, logger);
        if (!initialized) {
            logger.logError("Failed to initialize Discord bot. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }
}