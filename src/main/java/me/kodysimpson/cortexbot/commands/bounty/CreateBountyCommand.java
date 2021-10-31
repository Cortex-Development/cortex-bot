package me.kodysimpson.cortexbot.commands.bounty;

import com.jagrosh.jdautilities.command.SlashCommand;
import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import me.kodysimpson.cortexbot.repositories.BountyRepository;
import me.kodysimpson.cortexbot.services.BountyService;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.springframework.stereotype.Component;

@Component
public class CreateBountyCommand extends SlashCommand {

    private final BountyService bountyService;

    public CreateBountyCommand(BountyService bountyService){
        this.bountyService = bountyService;
        this.name = "create-bounty";
        this.help = "Create a new help bounty.";
    }

    @Override
    protected void execute(SlashCommandEvent event) {

        event.deferReply().queue();

        bountyService.createNewBounty(event.getInteraction());

    }
}
