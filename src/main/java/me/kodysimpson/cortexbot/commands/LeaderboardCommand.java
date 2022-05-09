package me.kodysimpson.cortexbot.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import me.kodysimpson.cortexbot.model.CortexMember;
import me.kodysimpson.cortexbot.repositories.CortexMemberRepository;
import me.kodysimpson.cortexbot.services.DiscordBot;
import net.dv8tion.jda.api.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

@Component
public class LeaderboardCommand extends SlashCommand {

    private final CortexMemberRepository cortexMemberRepository;

    public LeaderboardCommand(CortexMemberRepository cortexMemberRepository){
        this.name = "leaderboard";
        this.help = "Get the top ten leaderboard rankings";
        this.guildOnly = false;
        this.cortexMemberRepository = cortexMemberRepository;
    }

    @Override
    protected void execute(SlashCommandEvent event) {

        event.deferReply().queue();

        ArrayList<CortexMember> top = (ArrayList<CortexMember>) cortexMemberRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(CortexMember::getPoints).reversed())
                .limit(15)
                .collect(Collectors.toList());

        MessageBuilder message = new MessageBuilder();

        message.append("---------------------------------------------------------------------------------------------", MessageBuilder.Formatting.STRIKETHROUGH).append("\n");
        message.append("Top 15 Leaderboard Rankings", MessageBuilder.Formatting.BOLD).append("\n\n");

        for (int i = 0; i < top.size(); i++){
            message.append("(" + (i + 1) + ") - ", MessageBuilder.Formatting.BOLD).append(DiscordBot.getUsernameFromUserID(top.get(i).getUserID()) + " *-* " + top.get(i).getPoints() + " pts").append("\n");
        }

        message.append("\nYou can view the full leaderboard here: COMING SOON").append("\n");
        message.append("---------------------------------------------------------------------------------------------", MessageBuilder.Formatting.STRIKETHROUGH);

        event.getHook().sendMessage(message.build()).queue();

    }

}
