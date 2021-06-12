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

    @Autowired
    DiscordBotService discordBotService;

    public void log(String message){
        TextChannel channel = discordBotService.getGuild().getTextChannelById(discordConfiguration.getLoggingChannel());
        channel.sendMessage(message + " [" + new Date() + "]").queue();
    }

    public void logPointsGiven(String username, String points, String givenBy){
        this.log(points + " have been given to " + username + " by " + givenBy + ".");
    }

}
