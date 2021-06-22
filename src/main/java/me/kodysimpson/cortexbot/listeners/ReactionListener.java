package me.kodysimpson.cortexbot.listeners;

import me.kodysimpson.cortexbot.model.Bounty;
import me.kodysimpson.cortexbot.repositories.BountyRepository;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class ReactionListener extends ListenerAdapter {

    BountyRepository bountyRepository;

    public ReactionListener(BountyRepository bountyRepository){
        this.bountyRepository = bountyRepository;
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event){

        if (bountyRepository.existsBountyByChannelIdEquals(event.getChannel().getId())){

            Bounty bounty = bountyRepository.findBountyByChannelIdEquals(event.getChannel().getId());

            if (bounty.getUserId().equalsIgnoreCase(event.getUserId())){
                bounty.setFinished(true);

                //send it for staff approval and grading
                event.getTextChannel().getManager().setParent(event.getGuild().getCategoryById("786974851818192916")).complete();

                Role role = event.getGuild().getRoleById("786974475354505248");
                event.getTextChannel().createPermissionOverride(event.getGuild().getPublicRole()).setDeny(Permission.VIEW_CHANNEL).queue();
                event.getTextChannel().putPermissionOverride(role).setAllow(Permission.VIEW_CHANNEL).queue();

                MessageBuilder builder = new MessageBuilder();
                builder.allowMentions(Message.MentionType.ROLE)
                        .append("""
                        <@&786974475354505248> Check this conversation to see if anyone should be given points for helping the creator of this bounty. Do $done when done.
                        """);

                event.getTextChannel().sendMessage(builder.build()).queue();

            }
        }

    }

}
