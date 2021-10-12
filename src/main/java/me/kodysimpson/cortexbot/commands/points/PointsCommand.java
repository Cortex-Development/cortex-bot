package me.kodysimpson.cortexbot.commands.points;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import me.kodysimpson.cortexbot.services.MemberUserService;
import me.kodysimpson.cortexbot.services.PointsService;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PointsCommand extends SlashCommand {

    private final PointsService pointsService;
    private final MemberUserService memberUserService;

    public PointsCommand(PointsService pointsService, MemberUserService memberUserService){
        this.pointsService = pointsService;
        this.memberUserService = memberUserService;
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
                event.getHook().sendMessage("You don't exist!").queue();
            }

        }else{

            //determine who was provided as an argument to this command
            User user = event.getOption("user").getAsUser();

            long points = pointsService.getPoints(user.getId());

            if (points != -1){
                event.getHook().sendMessage(user.getName() + " has " + points + " point(s).").queue();
            }else{
                event.getHook().sendMessage("The user provided does not exist.").queue();
            }

        }

    }

}