package ru.vladtop46.dsa.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.vladtop46.dsa.util.LogUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiscordBot extends ListenerAdapter {
    private static JDA jda;
    private static String guildId;
    private static String adminChannelId;
    private static List<String> allowedRoles;

    private static Plugin plugin;
    private static LogUtil logger;

    public static boolean initialize(JDA jda, String guildId, String adminChannelId, List<String> allowedRoles, Plugin plugin, LogUtil logger) {
        try {
            DiscordBot.guildId = guildId;
            DiscordBot.adminChannelId = adminChannelId;
            DiscordBot.allowedRoles = allowedRoles;
            DiscordBot.plugin = plugin;
            DiscordBot.logger = logger;

            jda.awaitReady();

            Guild guild = jda.getGuildById(guildId);

            guild.updateCommands().addCommands(
                    Commands.slash("execute", "Выполняет команду на сервере.")
                            .addOption(OptionType.STRING, "command", "Команда для выполнения:", true),
                    Commands.slash("list", "получает список игроков")
            ).queue();

            return true;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void shutdown() {
        if (jda != null) {
            jda.shutdown();
        }
    }

    private void executeSlashCommand(SlashCommandInteractionEvent event) {
        String command = event.getOption("command").getAsString();
        CommandSender sender = plugin.getServer().getConsoleSender();

        plugin.getServer().dispatchCommand(sender, command);
        logger.logInfo("Executed command: " + command);

        event.reply("Команда \"" + command + "\" выполнена успешно!").queue();
    }

    private void listSlashCommand(SlashCommandInteractionEvent event) {
        List<String> playerList = new ArrayList<>();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            playerList.add(player.getName());
        }
        Collections.sort(playerList);
        int onlinePlayers = plugin.getServer().getOnlinePlayers().size();
        int maxPlayers = plugin.getServer().getMaxPlayers();
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Список игроков на сервере")
                .setDescription(String.format("Онлайн игроков: **%s/%s**", onlinePlayers, maxPlayers))
                .setColor(Color.GREEN);

        StringBuilder playersInfo = new StringBuilder();
        for (int i = 0; i < playerList.size(); i++) {
            playersInfo.append((i + 1) + ". " + playerList.get(i) + "\n");
        }

        builder.addField("Игроки:", (playerList.isEmpty()) ? "Нет игроков" : playersInfo.toString(), false);
        event.replyEmbeds(builder.build()).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getChannel().getId().equals(adminChannelId)) {
            EmbedBuilder error = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle("Ошибка доступа")
                    .setDescription("Вы не имеете доступа к выполнению этой команды в данном канале.");
            event.replyEmbeds(error.build()).setEphemeral(true).queue();
            return;
        }

        boolean hasRole = event.getMember().getRoles().stream()
                .anyMatch(role -> allowedRoles.contains(role.getId()));

        if (!hasRole) {
            EmbedBuilder error = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle("Ошибка доступа")
                    .setDescription("Вы не имеете необходимой роли для выполнения этой команды.");
            event.replyEmbeds(error.build()).setEphemeral(true).queue();
            return;
        }

        String eventName = event.getName();
        switch (eventName) {
            case "execute":
                executeSlashCommand(event);
                break;
            case "list":
                listSlashCommand(event);
                break;
            default:
                EmbedBuilder error = new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle("Ошибка")
                        .setDescription("Неизвестная команда.");
                event.replyEmbeds(error.build()).setEphemeral(true).queue();
                break;
        }
    }
}
