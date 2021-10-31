package me.kodysimpson.cortexbot.listeners;

import me.kodysimpson.cortexbot.services.BountyService;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ButtonClickListener extends ListenerAdapter {

    private final BountyService bountyService;

    @Autowired
    public ButtonClickListener(BountyService bountyService) {
        this.bountyService = bountyService;
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
        }

    }

}
