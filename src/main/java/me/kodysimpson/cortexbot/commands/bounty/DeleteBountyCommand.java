package me.kodysimpson.cortexbot.commands.bounty;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.kodysimpson.cortexbot.model.Bounty;
import me.kodysimpson.cortexbot.repositories.BountyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteBountyCommand extends Command {

    public BountyRepository bountyRepository;

    @Autowired
    public DeleteBountyCommand(BountyRepository bountyRepository){
        this.name = "delete-bounty";
        this.help = "Delete your current bounty";
        this.bountyRepository = bountyRepository;
    }

    @Override
    protected void execute(CommandEvent event) {

        if(bountyRepository.existsBountyByUserIdEquals(event.getMember().getId())){

            Bounty bounty = bountyRepository.deleteBountyByUserIdEquals(event.getMember().getId());
            event.getGuild().getTextChannelById(bounty.getChannelId()).delete().complete();

            event.reply("Bounty help channel deleted. You can now open a new bounty.");
        }else{

            event.reply("You don't have a bounty currently open.");

        }

    }
}
