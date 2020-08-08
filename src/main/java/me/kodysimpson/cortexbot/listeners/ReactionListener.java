package me.kodysimpson.cortexbot.listeners;

import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import me.kodysimpson.cortexbot.model.Bounty;
import me.kodysimpson.cortexbot.model.Message;
import me.kodysimpson.cortexbot.repositories.BountyRepository;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;

public class ReactionListener extends ListenerAdapter {

    BountyRepository bountyRepository;
    DiscordConfiguration discordConfiguration;


    public ReactionListener(BountyRepository bountyRepository, DiscordConfiguration discordConfiguration){
        this.bountyRepository = bountyRepository;
        this.discordConfiguration = discordConfiguration;
    }


    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {

        if (event.getGuild().getIdLong() == discordConfiguration.getGuildId()) return;
        if (event.getReactionEmote().getEmote().getIdLong() == discordConfiguration.getGreenTickId() && event.getReactionEmote().getEmote().getIdLong() == discordConfiguration.getRedTickId()) return;
        if (event.getUser().isBot()) return;

        if (event.getChannel().getIdLong() == discordConfiguration.getWantedChannelId()) {
            event.getReaction().removeReaction(event.getUser()).queue();

            if (bountyRepository.existsBountyByBountyMessageID(event.getMessageIdLong())) {
                Bounty foundBounty = bountyRepository.findBountyByBountyMessageID(event.getMessageIdLong());

                if (!isDiscordUserAlreadyInChannel(foundBounty, event.getUserId())) {

                    TextChannel channel = event.getGuild().getTextChannelById(foundBounty.getChannelID());
                    channel.createPermissionOverride(event.getMember())
                            .setPermissions(Collections.singleton(Permission.VIEW_CHANNEL), null)
                            .queue(permissionOverride -> {
                                channel.sendMessage(event.getMember().getAsMention() + " has joined the conversation.")
                                        .queue();
                            });

                }
            }
        } else {

            //see if the reaction was made in a bounty channel
            if (bountyRepository.existsByChannelID(event.getChannel().getIdLong())) {

                Bounty bounty = bountyRepository.findBountyByChannelID(event.getChannel().getIdLong());

                ArrayList<Message> messages = (ArrayList<Message>) bounty.getResponses();

                messages.stream()
                        .filter(Message::isDiscordMessage)
                        .forEach(message -> {
                            if (message.getDiscordMessageID() == event.getMessageIdLong()) {
                                if (event.getReactionEmote().getEmote().getIdLong() == discordConfiguration.getGreenTickId()) {
                                    message.setUpVotes(message.getUpVotes() + 1);
                                } else if (event.getReactionEmote().getEmote().getIdLong() == discordConfiguration.getRedTickId()) {
                                    message.setDownVotes(message.getDownVotes() + 1);
                                }
                            }
                        });

                bountyRepository.save(bounty);

            }

        }


    }

    @Override
    public void onGuildMessageReactionRemove(@Nonnull GuildMessageReactionRemoveEvent event) {

        if (event.getGuild().getIdLong() == discordConfiguration.getGuildId()) return;
        if (event.getReactionEmote().getEmote().getIdLong() == discordConfiguration.getGreenTickId() && event.getReactionEmote().getEmote().getIdLong() == discordConfiguration.getRedTickId()) return;
        if (event.getUser().isBot()) return;

        //see if the reaction was made in a bounty channel
        if (bountyRepository.existsByChannelID(event.getChannel().getIdLong())) {

            Bounty bounty = bountyRepository.findBountyByChannelID(event.getChannel().getIdLong());

            ArrayList<Message> messages = (ArrayList<Message>) bounty.getResponses();

            messages.stream()
                    .filter(Message::isDiscordMessage)
                    .forEach(message -> {
                        if (message.getDiscordMessageID() == event.getMessageIdLong()) {
                            if (event.getReactionEmote().getEmote().getIdLong() == discordConfiguration.getGreenTickId()) {
                                message.setUpVotes(message.getUpVotes() - 1);
                            } else if (event.getReactionEmote().getEmote().getIdLong() == discordConfiguration.getRedTickId()) {
                                message.setDownVotes(message.getDownVotes() - 1);
                            }
                        }
                    });

            bountyRepository.save(bounty);

        }


    }

    private boolean isDiscordUserAlreadyInChannel(Bounty bounty, String userID) {

        long messagesFromUser = bounty.getResponses().stream()
                .filter(Message::isDiscordMessage)
                .filter(message -> message.getDiscordUserID().equalsIgnoreCase(userID)).count();

        return messagesFromUser > 0;
    }

}
