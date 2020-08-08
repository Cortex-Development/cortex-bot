package me.kodysimpson.cortexbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class LeaderboardCommand extends Command {

    MemberRepository memberRepository;

    public LeaderboardCommand(MemberRepository memberRepository){
        this.name = "leaderboard";
        this.help = "Get the top ten leaderboard rankings";
        this.guildOnly = false;
        this.memberRepository = memberRepository;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {

        ArrayList<User> topTen = (ArrayList<User>) memberRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Member::getPoints).reversed())
                .limit(10)
                .map(member -> commandEvent.getJDA().retrieveUserById(member.getUserID()).complete())
                .collect(Collectors.toList());

        MessageBuilder message = new MessageBuilder();

        message.append("--------------------------------------------------------------------------------------------------", MessageBuilder.Formatting.STRIKETHROUGH).append("\n");
        message.append("Top Ten Leaderboard Rankings", MessageBuilder.Formatting.BOLD).append("\n\n");

        for (int i = 0; i < topTen.size(); i++){
            message.append("(" + (i + 1) + ") - ", MessageBuilder.Formatting.BOLD).append(topTen.get(i).getAsTag() + "\n");
        }

        message.append("\nYou can view the full leaderboard here: https://cortexdev.herokuapp.com/leaderboard").append("\n");
        message.append("--------------------------------------------------------------------------------------------------", MessageBuilder.Formatting.STRIKETHROUGH);

        commandEvent.getChannel().sendMessage(message.build()).queue();
    }
}
