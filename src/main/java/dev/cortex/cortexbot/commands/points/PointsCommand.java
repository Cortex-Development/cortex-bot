package dev.cortex.cortexbot.commands.points;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import dev.cortex.cortexbot.services.PointsService;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PointsCommand extends SlashCommand {

    private final PointsService pointsService;

    public PointsCommand(PointsService pointsService){
        this.pointsService = pointsService;
        this.name = "points";
        this.help = "See how many points you or someone else has";

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "user", "The person involved"));
        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event) {

        event.deferReply().queue();

        if (event.getOptions().isEmpty()){

            //Since no arguments were provided, show the user their own points amount
            long points = pointsService.getPoints(event.getUser().getId());

            if (points != -1){
                event.getHook().sendMessage("You have " + points + " point(s).").setEphemeral(true).queue();
            }else{
                event.getHook().sendMessage("You don't exist!").setEphemeral(true);
            }

        }else{

            //determine who was provided as an argument to this command
            User user = event.getOption("user").getAsUser();

            long points = pointsService.getPoints(user.getId());

            if (points != -1){
                event.getHook().sendMessage(user.getName() + " has " + points + " point(s).").queue();
            }else{
                event.getHook().sendMessage("The user provided does not exist.").setEphemeral(true).queue();
            }

        }

    }

}
