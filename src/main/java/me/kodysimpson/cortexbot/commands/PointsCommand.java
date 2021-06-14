package me.kodysimpson.cortexbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.kodysimpson.cortexbot.services.MemberUserService;
import me.kodysimpson.cortexbot.services.PointsService;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Component;

@Component
public class PointsCommand extends Command {

    private final PointsService pointsService;
    private final MemberUserService memberUserService;

    public PointsCommand(PointsService pointsService, MemberUserService memberUserService){
        this.pointsService = pointsService;
        this.memberUserService = memberUserService;
        this.name = "points";
        this.arguments = "<user id | username | tag>";
    }

    @Override
    protected void execute(CommandEvent event) {

        String args = event.getArgs();

        if (args.isEmpty()){

            //Since no arguments were provided, show the user their own points amount
            long points = pointsService.getPoints(event.getAuthor().getId());

            if (points != -1){
                event.reply("You have " + points + " point(s).");
            }else{
                event.reply("You don't exist!");
            }

        }else{

            //determine who was provided as an argument to this command
            User user = memberUserService.findUser(args);

            if (user == null){
                event.reply("The user provided does not exist.");
            }else{

                long points = pointsService.getPoints(user.getId());

                if (points != -1){
                    event.reply(user.getName() + " has " + points + " point(s).");
                }else{
                    event.reply("The user provided does not exist.");
                }

            }

        }


    }
}
