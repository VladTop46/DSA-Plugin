package ru.vladtop46.dsa.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

public class LogUtil {
    private static LogUtil instance;
    private Plugin plugin;
    private final Logger logger;
    private ConsoleCommandSender console;

    private static final Level defaultLogLevel = Level.INFO;

    private LogUtil(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.console = Bukkit.getServer().getConsoleSender();
    }

    public static LogUtil getInstance(Plugin plugin) {
        if (instance == null) {
            instance = new LogUtil(plugin);
        }
        return instance;
    }

    // Логгирование с уровнем по умолчанию (INFO) с цветом, в качестве цветовых кодов указан символ &
    public void logColored(String message) {
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    // Логгирование с уровнем по умолчанию (INFO)
    public void log(String message) {
        log(defaultLogLevel, message);
    }

    // Логгирование с заданным уровнем
    public void log(Level level, String message) {
        logger.log(level, "[" + plugin.getName() + "] " + message);
    }

    // Логгирование ошибки
    public void logError(String message) {
        log(Level.SEVERE, message);
    }

    // Логгирование предупреждения
    public void logWarning(String message) {
        log(Level.WARNING, message);
    }

    // Логгирование информации
    public void logInfo(String message) {
        log(Level.INFO, message);
    }

    // Логгирование отладочной информации
    public void logDebug(String message) {
        log(Level.FINE, message);
    }

}

