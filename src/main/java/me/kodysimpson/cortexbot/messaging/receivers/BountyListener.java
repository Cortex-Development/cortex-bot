package me.kodysimpson.cortexbot.messaging.receivers;

import me.kodysimpson.cortexbot.bot.DiscordBotService;
import me.kodysimpson.cortexbot.model.Bounty;
import me.kodysimpson.cortexbot.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class BountyListener {

    @Autowired
    DiscordBotService discordBotService;

    @JmsListener(destination = "bounty-thing")
    public void receiveBounty(@Payload Bounty bounty){

        try {
            discordBotService.postBounty(bounty);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @JmsListener(destination = "response-queue")
    public void receiveNewResponse(@Payload Bounty bounty){

        try {
            discordBotService.postBountyMessage(bounty, bounty.getResponses().get(bounty.getResponses().size() - 1));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
