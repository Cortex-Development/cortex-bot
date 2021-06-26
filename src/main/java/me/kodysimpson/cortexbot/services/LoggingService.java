package me.kodysimpson.cortexbot.services;

import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LoggingService {

    @Autowired
    DiscordConfiguration discordConfiguration;

    public void log(String message){
        TextChannel channel = DiscordBotService.getApi().getGuildById("503656531665879063").getTextChannelById(discordConfiguration.getLoggingChannel());
        channel.sendMessage(message + " [" + new Date() + "]").queue();
    }

    public void logPointsGiven(String username, int points, String givenBy){
        this.log(points + " point(s) have been given to " + username + " by " + givenBy + ".");
    }

    public void logPointsTaken(String username, int points, String givenBy){
        this.log(points + " point(s) have been taken from " + username + " by " + givenBy + ".");
    }

    public void logPointsSet(String username, int points, String givenBy){
        this.log(points + " point(s) have been set for " + username + " by " + givenBy + ".");
    }

    public void logPointsPayed(String username, int points, String givenBy) {
        this.log(points + " point(s) have been payed to " + username + " by " + givenBy + ".");
    }
}
