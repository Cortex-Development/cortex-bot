package me.kodysimpson.cortexbot.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import me.kodysimpson.cortexbot.services.DiscordBotService;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

@Component
public class LeaderboardCommand extends SlashCommand {

    private final MemberRepository memberRepository;

    public LeaderboardCommand(MemberRepository memberRepository){
        this.name = "leaderboard";
        this.help = "Get the top ten leaderboard rankings";
        this.guildOnly = false;
        this.memberRepository = memberRepository;
    }

    @Override
    protected void execute(SlashCommandEvent event) {

        event.deferReply().queue();

        ArrayList<Member> top = (ArrayList<Member>) memberRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Member::getPoints).reversed())
                .limit(15)
                .collect(Collectors.toList());

        MessageBuilder message = new MessageBuilder();

        message.append("---------------------------------------------------------------------------------------------", MessageBuilder.Formatting.STRIKETHROUGH).append("\n");
        message.append("Top 15 Leaderboard Rankings", MessageBuilder.Formatting.BOLD).append("\n\n");

        for (int i = 0; i < top.size(); i++){
            message.append("(" + (i + 1) + ") - ", MessageBuilder.Formatting.BOLD).append(DiscordBotService.getUsernameFromUserID(top.get(i).getUserID()) + " *-* " + top.get(i).getPoints() + " pts").append("\n");
        }

        message.append("\nYou can view the full leaderboard here: COMING SOON").append("\n");
        message.append("---------------------------------------------------------------------------------------------", MessageBuilder.Formatting.STRIKETHROUGH);

        event.getHook().sendMessage(message.build()).queue();

    }

}
