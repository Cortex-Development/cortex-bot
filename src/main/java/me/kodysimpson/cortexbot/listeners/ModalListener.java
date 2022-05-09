package me.kodysimpson.cortexbot.listeners;

import me.kodysimpson.cortexbot.services.BountyService;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import static me.kodysimpson.cortexbot.services.DiscordBot.getGuild;

@Service
public class ModalListener extends ListenerAdapter {

    private final BountyService bountyService;

    public ModalListener(BountyService bountyService) {
        this.bountyService = bountyService;
    }


    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {

        if (event.getModalId().equals("got-helped")){

            String helperName = event.getValue("helper").getAsString();
            String proof = event.getValue("proof").getAsString();

            Button pointsGiven = Button.of(ButtonStyle.SUCCESS, "points-given", "Points Given");
            Button pointsNotGiven = Button.of(ButtonStyle.DANGER, "points-not-given", "Points not Given");

            Message message = new MessageBuilder()
                    .append("--->  ")
                    .append(helperName)
                    .append(" helped ")
                    .append(event.getMember().getEffectiveName())
                    .append(", ")
                    .append(proof)
//                    .setActionRows(ActionRow.of(pointsGiven, pointsNotGiven))
                    .build();

            getGuild().getTextChannelById("838841366498246757").sendMessage(message).queue();

            event.reply("Thanks! A Community Manager will take a look and give them points if they helped you.").setEphemeral(true).queue();

        }

    }
}
