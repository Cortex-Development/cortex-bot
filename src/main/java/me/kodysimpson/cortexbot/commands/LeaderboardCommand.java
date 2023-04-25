package me.kodysimpson.cortexbot.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import me.kodysimpson.cortexbot.model.CortexMember;
import me.kodysimpson.cortexbot.repositories.CortexMemberRepository;
import me.kodysimpson.cortexbot.services.DiscordBot;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;

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
                .toList();

        MessageCreateBuilder message = new MessageCreateBuilder();

        message.addContent("---------------------------------------------------------------------------------------------").addContent("\n");
        message.addContent("Top 15 Leaderboard Rankings").addContent("\n\n");

        for (int i = 0; i < top.size(); i++){
            message.addContent("(" + (i + 1) + ") - ").addContent(DiscordBot.getUsernameFromUserID(top.get(i).getUserID()) + " *-* " + top.get(i).getPoints() + " pts").addContent("\n");
        }

        //Might have a web interface in the future, no touch
//        message.addContent("\nYou can view the full leaderboard here: COMING SOON").addContent("\n");
        message.addContent("---------------------------------------------------------------------------------------------");

        event.getHook().sendMessage(message.build()).queue();

    }

}
