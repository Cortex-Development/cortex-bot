package me.kodysimpson.cortexbot.listeners;

import me.kodysimpson.cortexbot.services.BountyService;
import me.kodysimpson.cortexbot.services.ChallengeService;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ButtonClickListener extends ListenerAdapter {

    private final BountyService bountyService;
    private final ChallengeService challengeService;

    @Autowired
    public ButtonClickListener(BountyService bountyService, ChallengeService challengeService) {
        this.bountyService = bountyService;
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

        if (event.getButton().getId().equalsIgnoreCase("new-bounty")) {
            bountyService.createNewBounty(event.getInteraction());
        }else if (event.getButton().getId().equalsIgnoreCase("delete-bounty")){
            bountyService.deleteBounty(event.getInteraction());
        }else if (event.getButton().getId().equalsIgnoreCase("done-bounty")){
            bountyService.closeBounty(event.getInteraction());
        }else if (event.getButton().getId().equalsIgnoreCase("grade-bounty")){
            bountyService.finishGrading(event.getInteraction());
        }else if(event.getButton().getId().equalsIgnoreCase("submit-challenge")){
            challengeService.createSubmissionChannel(event.getInteraction());
        }else if(event.getButton().getId().equalsIgnoreCase("challenge-close-submission")){
            challengeService.closeSubmissionChannel(event.getInteraction());
        }else if(event.getButton().getId().equalsIgnoreCase("get-challenge-role")){

            //get the challengeping role
            Role role = event.getInteraction().getGuild().getRoleById("770425465063604244");

            //add the role to the user
            event.getInteraction().getGuild().addRoleToMember(event.getMember(), role).queue();

            //reply
            event.reply("You have been given the @ChallengePing role!").setEphemeral(true).queue();

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
