package dev.cortex.cortexbot.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import dev.cortex.cortexbot.repositories.CortexMemberRepository;
import dev.cortex.cortexbot.model.CortexMember;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
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

        ArrayList<CortexMember> top = (ArrayList<CortexMember>) cortexMemberRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(CortexMember::getPoints).reversed())
                .limit(15)
                .collect(Collectors.toList());

        MessageCreateBuilder message = new MessageCreateBuilder();

        message.addContent("---------------------------------------------------------------------------------------------").addContent("\n");
        message.addContent("\uD83D\uDE80 **Top 15 Leaderboard Rankings** \uD83D\uDE80").addContent("\n\n");

        for (int i = 0; i < top.size(); i++){
            message.addContent("(" + (i + 1) + ") - ").addContent("<@" + top.get(i).getUserID() + "> *-* " + top.get(i).getPoints() + " pts");
            if (i == 0) message.addContent(":first_place:");
            if (i == 1) message.addContent(":second_place:");
            if (i == 2) message.addContent(":third_place:");
            message.addContent("\n");
        }

        //Might have a web interface in the future, no touch
//        message.addContent("\nYou can view the full leaderboard here: COMING SOON").addContent("\n");
        message.addContent("\nYou can get points by being active in the server and helping others.").addContent("\n");
        message.addContent("---------------------------------------------------------------------------------------------");

        event.reply(message.build()).setEphemeral(true).queue();

    }

}
