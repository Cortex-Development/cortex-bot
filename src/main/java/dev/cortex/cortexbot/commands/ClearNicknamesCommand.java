package dev.cortex.cortexbot.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;

public class ClearNicknamesCommand extends SlashCommand {

    public ClearNicknamesCommand(){
        this.name = "clearnicknames";
        this.category = new Category("Moderation");
        this.help = "Clear nicknames from every user in the server.";
        this.userPermissions = new Permission[]{Permission.MODERATE_MEMBERS};
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        List<Member> members = event.getGuild().getMembers();

        for (Member member : members) {
            if (member.getNickname() != null) {
                member.modifyNickname(null).queue();
            }
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("✅ Cleared nicknames for all users successfully!");

        event.replyEmbeds(embed.build()).queue();
    }
}
