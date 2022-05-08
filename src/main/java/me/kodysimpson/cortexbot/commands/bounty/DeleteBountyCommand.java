package me.kodysimpson.cortexbot.commands.bounty;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import me.kodysimpson.cortexbot.services.BountyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteBountyCommand extends SlashCommand {

    private final BountyService bountyService;

    @Autowired
    public DeleteBountyCommand(BountyService bountyService){
        this.bountyService = bountyService;
        this.name = "delete-bounty";
        this.help = "Delete your current bounty if you have one";
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.deferReply().queue();
        bountyService.deleteBounty(event.getInteraction());
    }

}
