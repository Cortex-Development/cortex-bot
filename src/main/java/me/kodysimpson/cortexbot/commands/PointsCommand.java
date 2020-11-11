package me.kodysimpson.cortexbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import net.dv8tion.jda.api.entities.User;

import java.util.List;
import java.util.stream.IntStream;

public class PointsCommand extends Command {

    MemberRepository memberRepository;

    public PointsCommand(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
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
                event.reply("You don't exist.");
            }

        }else{

            //determine who was provided as an argument to this command
            User user;
            if (args.startsWith("<@!")){
                user = event.getJDA().getUserById(args.substring(3, args.length() - 1));
            }else if (IntStream.range(0, args.length()).boxed().map(args::charAt).allMatch(Character::isDigit)){
                user = event.getJDA().getUserById(args);
            }else{
                List<User> users = event.getJDA().getUsersByName(args, true);
                if (!users.isEmpty()){
                    user = users.get(0);
                }else{
                    user = null;
                }
            }

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
