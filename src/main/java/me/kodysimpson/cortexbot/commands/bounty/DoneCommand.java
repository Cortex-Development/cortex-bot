package me.kodysimpson.cortexbot.commands.bounty;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import me.kodysimpson.cortexbot.model.Bounty;
import me.kodysimpson.cortexbot.repositories.BountyRepository;
import me.kodysimpson.cortexbot.services.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DoneCommand extends Command {

    public BountyRepository bountyRepository;
    private DiscordConfiguration discordConfiguration;
    private LoggingService loggingService;

    @Autowired
    public DoneCommand(BountyRepository bountyRepository, DiscordConfiguration discordConfiguration, LoggingService loggingService){
        this.discordConfiguration = discordConfiguration;
        this.loggingService = loggingService;
        this.name = "done";
        this.help = "Finish grading finished bounty channel";
        this.bountyRepository = bountyRepository;
    }

    @Override
    protected void execute(CommandEvent event) {

        if (event.getMember().isOwner() || event.getMember().getRoles().contains(event.getJDA().getRoleById(discordConfiguration.getStaffRole()))){
            if(bountyRepository.existsBountyByChannelIdEquals(event.getChannel().getId())){

                Bounty bounty = bountyRepository.deleteBountyByChannelIdEquals(event.getChannel().getId());

                event.getGuild().getTextChannelById(bounty.getChannelId()).delete().complete();

                loggingService.log("Bounty help channel deleted by " + event.getMember().getEffectiveName() + ". Bounty: " + bounty);
            }else{

                event.reply("This isn't a bounty channel.");

            }
        }else{
            event.reply("You cannot run this command.");
        }
    }

}
