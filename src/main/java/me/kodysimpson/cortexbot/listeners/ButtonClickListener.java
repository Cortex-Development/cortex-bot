package me.kodysimpson.cortexbot.listeners;

import me.kodysimpson.cortexbot.services.ChallengeService;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ButtonClickListener extends ListenerAdapter {
    private final ChallengeService challengeService;

    @Autowired
    public ButtonClickListener(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

        if(event.getButton().getId().equalsIgnoreCase("i-got-helped")){

            TextInput email = TextInput.create("helper", "Who helped you?", TextInputStyle.SHORT)
                    .setPlaceholder("Put their name here")
                    .setRequired(true)
                    .build();

            TextInput body = TextInput.create("proof", "Body", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Put a link to the message, screenshot, etc.")
                    .setRequired(true)
                    .build();

            Modal modal = Modal.create("got-helped", "I Got Helped")
                    .addActionRows(ActionRow.of(email), ActionRow.of(body))
                    .build();

            event.replyModal(modal).queue();

            return;
        }

        event.deferReply().setEphemeral(true).queue();

        if(event.getButton().getId().equalsIgnoreCase("submit-challenge")){
            challengeService.createSubmissionChannel(event.getInteraction());
        }else if(event.getButton().getId().equalsIgnoreCase("challenge-close-submission")){
            challengeService.closeSubmissionChannel(event.getInteraction());
        }else if(event.getButton().getId().equalsIgnoreCase("get-challenge-role")){

            //get the challengers role
            Role role = Objects.requireNonNull(event.getInteraction().getGuild()).getRoleById("770425465063604244");
            Member member = event.getInteraction().getMember();

            if (role == null) {
                event.getHook().sendMessage("Error, could not find the Challengers role").queue();
                return;
            }

            if (member == null){
                event.getHook().sendMessage("Error, you do not exist").queue();
                return;
            }

            //See if the user already has the role
            if(event.getInteraction().getMember().getRoles().contains(role)){
                event.getHook().sendMessage("You already have the <@&770425465063604244> role.").setEphemeral(true).queue();
                return;
            }

            //add the role to the user
            event.getInteraction().getGuild().addRoleToMember(member, role).queue(then -> {
                //send a message in #challenge-chat that this user has joined the role
                TextChannel challengeChat = event.getGuild().getTextChannelById("803795646565843003");
                if (challengeChat != null) {
                    challengeChat.sendMessage(member.getAsMention() + " has joined the Challengers role.").queue();
                }
            });

            //reply
            event.getHook().sendMessage("You have been given the <@&770425465063604244> role! If you want it removed, ask a Community Manager.").setEphemeral(true).queue();

        }else if(event.getButton().getId().equalsIgnoreCase("challenge-grade-pass")){

            challengeService.gradeSubmission(event.getInteraction(), true);

        }else if(event.getButton().getId().equalsIgnoreCase("challenge-grade-fail")){

            challengeService.gradeSubmission(event.getInteraction(), false);

        }else if(event.getButton().getId().equalsIgnoreCase("points-given")){

            //Make sure the user is in the correct role
            Role communityManager = event.getInteraction().getGuild().getRoleById("786974475354505248");

            if(!event.getMember().getRoles().contains(communityManager)){
                event.getHook().sendMessage("Nice try.").setEphemeral(true).queue();
                return;
            }

            // Add [POINTS GIVEN] to the message
            event.getInteraction().getMessage().editMessage(event.getInteraction().getMessage().getContentRaw() + " [POINTS GIVEN]").queue();

            //remove the reactions from the message

            event.getHook().sendMessage("Roger.").setEphemeral(true).queue();
        }

    }

}
