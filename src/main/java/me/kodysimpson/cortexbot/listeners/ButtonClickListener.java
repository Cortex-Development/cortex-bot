package me.kodysimpson.cortexbot.listeners;

import me.kodysimpson.cortexbot.services.BountyService;
import me.kodysimpson.cortexbot.services.ChallengeService;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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
    public void onButtonClick(ButtonClickEvent event){

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

        }

    }

}
