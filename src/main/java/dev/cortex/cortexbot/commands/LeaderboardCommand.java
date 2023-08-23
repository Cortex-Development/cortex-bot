package dev.cortex.cortexbot.commands;

import com.jagrosh.jdautilities.command.*;
import dev.cortex.cortexbot.repositories.CortexMemberRepository;
import dev.cortex.cortexbot.model.CortexMember;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.stereotype.Component;

import java.awt.*;
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

        ArrayList<CortexMember> top = (ArrayList<CortexMember>) cortexMemberRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(CortexMember::getPoints).reversed())
                .limit(15)
                .toList();

        EmbedBuilder builder = new EmbedBuilder();

        builder
                .setColor(Color.CYAN)
                .setTitle("**Top 15 Leaderboard Rankings**")
                .addBlankField(false);

        for (int i = 0; i < top.size(); i++){

            StringBuilder nameString = new StringBuilder("(" + (i + 1) + ")  <@" + top.get(i).getUserID() + ">");
            if (i == 0) nameString.append(":first_place:");
            if (i == 1) nameString.append(":second_place:");
            if (i == 2) nameString.append(":third_place:");

            builder.addField(nameString.toString(), top.get(i) + "pts", false);
        }

        //Might have a web interface in the future, no touch
//        message.addContent("\nYou can view the full leaderboard here: COMING SOON").addContent("\n");
        builder.appendDescription("You can get points by being active in the server and helping others.");

        event.replyEmbeds(builder.build()).setEphemeral(true).queue();

    }

}
