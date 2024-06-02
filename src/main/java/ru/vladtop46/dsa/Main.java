package ru.vladtop46.dsa;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladtop46.dsa.discord.DiscordBot;
import ru.vladtop46.dsa.handler.Events;
import ru.vladtop46.dsa.util.BotUtil;
import ru.vladtop46.dsa.util.LogUtil;

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
        botUtil.initUtil(plugin, jda);
        eventHandler.init(botUtil);
        Bukkit.getPluginManager().registerEvents(eventHandler, plugin);
    }

    @Override
    public void onDisable() {
        getLogger().info("DSA Disabled!");
        DiscordBot.shutdown();
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

        boolean initialized = createBot(token) && DiscordBot.initialize(jda, guildId, adminChannelId, plugin, logger);
        if (!initialized) {
            logger.logError("Failed to initialize Discord bot. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }
}