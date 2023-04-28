package dev.cortex.cortexbot.listeners;

import dev.cortex.cortexbot.repositories.ChallengeRepository;
import dev.cortex.cortexbot.model.challenges.Challenge;
import dev.cortex.cortexbot.model.challenges.ChallengeStatus;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ModalListener extends ListenerAdapter {

    private final ChallengeRepository challengeRepository;

    public ModalListener(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {

        if (event.getModalId().equals("got-helped")){

//            String helperName = event.getValue("helper").getAsString();
//            String proof = event.getValue("proof").getAsString();
//
//            Button pointsGiven = Button.of(ButtonStyle.SUCCESS, "points-given", "Points Given");
//            Button pointsNotGiven = Button.of(ButtonStyle.DANGER, "points-not-given", "Points not Given");
//
//            Message message = new MessageBuilder()
//                    .append("--->  ")
//                    .append(helperName)
//                    .append(" helped ")
//                    .append(event.getMember().getEffectiveName())
//                    .append(", ")
//                    .append(proof)
////                    .setActionRows(ActionRow.of(pointsGiven, pointsNotGiven))
//                    .build();
//
//            getGuild().getTextChannelById("838841366498246757").sendMessage(message).queue();
//
//            event.reply("Thanks! A Community Manager will take a look and give them points if they helped you.").setEphemeral(true).queue();

        }else if (event.getModalId().equals("new-challenge-modal")){

            //Construct a new Challenge object with the given information
            String name = event.getValue("challenge-name").getAsString();
            String description = event.getValue("challenge-desc").getAsString();
            String link = event.getValue("challenge-link").getAsString();
            long whenEnd, reward;

            try{
                whenEnd = Long.parseLong(event.getValue("challenge-end").getAsString());
                reward = Long.parseLong(event.getValue("challenge-reward").getAsString());
            }catch (NumberFormatException e){
                event.reply("Please enter a valid number for the end and reward fields.").queue();
                return;
            }

            Challenge challenge = new Challenge();
            challenge.setName(name);
            challenge.setDescription(description);
            challenge.setLink(link);
            challenge.setStartDate(new Date().getTime());
            challenge.setEndDate(whenEnd);
            challenge.setReward(reward);
            challenge.setStatus(ChallengeStatus.ACTIVE);

            challengeRepository.insert(challenge);

            //Make the announcement
            //803777799353270293 <- challenges channel
            String announcement = event.getGuild().getRoleById("770425465063604244").getAsMention() + "\n\n" +
                    "NEW CODING CHALLENGE!: **\"" + challenge.getName() + "\"**\n\n" +
                    "**Description**: " + challenge.getDescription() + "\n" +
                    "**Link**: " + challenge.getLink() + "\n" +
                    "**Ends on**: <t:" + whenEnd + ":D>\n\n" +
                    "**Reward**: " + challenge.getReward() + " points\n\n" +
                    "Join by clicking the button below!\n";

            MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
            messageBuilder.setContent(announcement);
            messageBuilder.setActionRow(Button.success("submit-challenge", "Submit Solution"), Button.primary("get-challenge-role", "Get Alerted for Future Challenges"));

            event.getGuild().getTextChannelById("803777799353270293").sendMessage(messageBuilder.build()).queue();

            event.reply("The challenge has been created and posted in the Challenges channel.").setEphemeral(true).queue();
        }

    }
}
