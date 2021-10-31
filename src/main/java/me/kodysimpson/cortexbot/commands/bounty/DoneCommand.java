package me.kodysimpson.cortexbot.commands.bounty;

import com.jagrosh.jdautilities.command.SlashCommand;
import me.kodysimpson.cortexbot.services.BountyService;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DoneCommand extends SlashCommand {

    private final BountyService bountyService;

    @Autowired
    public DoneCommand(BountyService bountyService){
        this.bountyService = bountyService;
        this.name = "done";
        this.help = "Finish grading finished bounty channel";
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.deferReply().queue();
        bountyService.finishGrading(event.getInteraction());
    }

}
