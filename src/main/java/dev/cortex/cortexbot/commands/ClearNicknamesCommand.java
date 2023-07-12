package dev.cortex.cortexbot.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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

        int successCount = 0;
        int failCount = 0;
        for (Member member : members) {
            if (member.getNickname() != null) {
                try {
                    member.modifyNickname(null).queue();
                    successCount++;
                } catch (HierarchyException e) {
                    failCount++;
                    // This just means the bot doesn't have permission to change the nickname of the user.
                }
            }
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Clear Nicknames");
        embed.addField("Nicknames Cleared Successfully", String.valueOf(successCount), false);
        embed.addField("Nicknames Failed to Clear", String.valueOf(failCount), false);
        embed.setDescription("✅ Operation completed successfully!");
        embed.setFooter("Cortex Bot", event.getJDA().getSelfUser().getAvatarUrl());

        event.replyEmbeds(embed.build()).queue();
    }
}
