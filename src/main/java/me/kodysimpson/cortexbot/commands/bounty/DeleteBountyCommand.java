package me.kodysimpson.cortexbot.commands.bounty;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import me.kodysimpson.cortexbot.model.Bounty;
import me.kodysimpson.cortexbot.repositories.BountyRepository;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteBountyCommand extends SlashCommand {

    public BountyRepository bountyRepository;

    @Autowired
    public DeleteBountyCommand(BountyRepository bountyRepository){
        this.name = "delete-bounty";
        this.help = "Delete your current bounty";
        this.bountyRepository = bountyRepository;
    }

    @Override
    protected void execute(SlashCommandEvent event) {

        if(bountyRepository.existsBountyByUserIdEquals(event.getMember().getId())){

            Bounty bounty = bountyRepository.deleteBountyByUserIdEquals(event.getMember().getId());
            event.getGuild().getTextChannelById(bounty.getChannelId()).delete().complete();

            event.reply("Bounty help channel deleted. You can now open a new bounty.").queue();
        }else{
            event.reply("You don't have a bounty currently open.").queue();
        }

    }

}
