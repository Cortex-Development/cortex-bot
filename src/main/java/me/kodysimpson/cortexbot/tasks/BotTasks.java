package me.kodysimpson.cortexbot.tasks;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static me.kodysimpson.cortexbot.services.DiscordBot.getGuild;

@Service
public class BotTasks {

    //TODO - do this another way bruh
    @Scheduled(fixedRate = 864000000, initialDelay = 60000)
    public void announceStart(){
        getGuild().getTextChannelById("786974733123846214").sendMessage("Cortex bot redeployed. Version: 1.0").queue();
    }

}