package me.kodysimpson.cortexbot.services;

import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.Instant;
import java.util.Date;

@Service
public class LoggingService {

    private final DiscordConfiguration discordConfiguration;

    @Autowired
    public LoggingService(DiscordConfiguration discordConfiguration) {
        this.discordConfiguration = discordConfiguration;
    }

    public void log(String message){
        TextChannel channel = DiscordBot.getApi().getGuildById("503656531665879063").getTextChannelById(discordConfiguration.getLoggingChannel());
        channel.sendMessage(message + " [" + new Date() + "]").queue();
    }

    public void log(MessageEmbed embed){
        TextChannel channel = DiscordBot.getApi().getGuildById(discordConfiguration.getGuildId())
                .getTextChannelById(discordConfiguration.getLoggingChannel());
        channel.sendMessageEmbeds(embed).queue();
    }

    public void logPointsGiven(String username, int points, String givenBy, @Nullable String reason){
        log(points + " point(s) have been given to " + username + " by " + givenBy + (reason == null ? "." : " for \"" + reason + "\"."));
    }

    public void logPointsTaken(String username, int points, String takenBy){
        log(points + " point(s) have been taken from " + username + " by " + takenBy + ".");
    }

    public void logPointsSet(String username, int points, String setBey){
        log(points + " point(s) have been set for " + username + " by " + setBey + ".");
    }

    public void logPointsPayed(String username, int points, String paidBy) {
        log(points + " point(s) have been payed to " + username + " by " + paidBy + ".");
    }

    public void logPointsThanked(String username, int points, String thankedBy) {
        log(points + " point(s) have been thanked to " + username + " by " + thankedBy + ".");
    }

    public void logPointsGiven(User user, long points, User givenBy, @Nullable String reason) {
        log(pointActionEmbed(user, points, givenBy, reason, "Give Points", Color.CYAN));
    }

    public void logPointsTaken(User user, long points, User takenBy, @Nullable String reason) {
        log(pointActionEmbed(user, points, takenBy, reason, "Take Points", Color.RED));
    }

    public void logPointsSet(User user, long points, User takenBy, @Nullable String reason) {
        log(pointActionEmbed(user, points, takenBy, reason, "Set Points", Color.BLUE));
    }

    public void logPointsPayed(User user, long points, User paidBy, @Nullable String reason) {
        log(pointActionEmbed(user, points, paidBy, reason, "Pay Points", Color.GREEN));
    }

    public void logPointsThanked(User user, long points, User thankedBy, @Nullable String reason) {
        log(pointActionEmbed(user, points, thankedBy, reason, "Thank Points", Color.PINK));
    }

    private MessageEmbed pointActionEmbed(User payee, long points, User payer, @Nullable String reason, String title, Color color) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(title)
                .setTimestamp(Instant.now())
                .setAuthor(
                        payer.getName(),
                        "https://discord.com/channels/@me/" + payer.getId(),
                        payer.getAvatarUrl()
                )
                .setColor(color)
                .addField("Receiver", payee.getAsMention(), true)
                .addField("Amount", String.valueOf(points), true);

        if (reason != null) builder.addField("Reason", reason, false);

        return builder.build();
    }
}
