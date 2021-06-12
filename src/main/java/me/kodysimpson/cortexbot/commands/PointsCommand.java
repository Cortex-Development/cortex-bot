package me.kodysimpson.cortexbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import me.kodysimpson.cortexbot.services.DiscordBotService;
import net.dv8tion.jda.api.entities.User;

public class PointsCommand extends Command {

    private final MemberRepository memberRepository;
    private final DiscordBotService discordBotService;

    public PointsCommand(MemberRepository memberRepository, DiscordBotService discordBotService){
        this.memberRepository = memberRepository;
        this.discordBotService = discordBotService;
        this.name = "points";
        this.arguments = "<user id | username | tag>";
    }

    @Override
    protected void execute(CommandEvent event) {

        String args = event.getArgs();

        if (args.isEmpty()){

            //Since no arguments were provided, show the user their own points amount
            Member member = memberRepository.findByUserIDIs(event.getAuthor().getId());

            if (member != null){
                event.reply("You have " + member.getPoints() + " point(s).");
            }else{
                event.reply("You don't exist!");
            }

        }else{

            //determine who was provided as an argument to this command
            User user = discordBotService.findUser(args);

            if (user == null){
                event.reply("The user provided does not exist.");
            }else{

                Member member = memberRepository.findByUserIDIs(user.getId());

                if (member != null){
                    event.reply(user.getName() + " has " + member.getPoints() + " point(s).");
                }else{
                    event.reply("The user provided does not exist in our database.");
                }

            }

        }


    }
}
