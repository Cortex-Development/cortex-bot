package dev.cortex.cortexbot.listeners;

import dev.cortex.cortexbot.repositories.BountyRepository;
import dev.cortex.cortexbot.repositories.CortexMemberRepository;
import jakarta.annotation.Nonnull;
import dev.cortex.cortexbot.config.DiscordConfiguration;
import dev.cortex.cortexbot.model.Bounty;
import dev.cortex.cortexbot.model.CortexMember;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class MessageListeners extends ListenerAdapter{
    private final Random random;
    private final CortexMemberRepository cortexMemberRepository;
    private final DiscordConfiguration discordConfiguration;
    private final BountyRepository bountyRepository;

    @Autowired
    public MessageListeners(CortexMemberRepository cortexMemberRepository, DiscordConfiguration discordConfiguration, BountyRepository bountyRepository){
        this.random = new Random();
        this.cortexMemberRepository = cortexMemberRepository;
        this.discordConfiguration = discordConfiguration;
        this.bountyRepository = bountyRepository;
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {

        if (event.getChannel().getId().equalsIgnoreCase("855669438170267698")){
            event.getGuild().getTextChannelById(event.getChannel().getId()).deleteMessageById(event.getMessageId()).completeAfter(5, TimeUnit.SECONDS);
        }

        if (!event.getAuthor().isBot()) {

            //see if the message was sent in an active bounty channel
            List<Bounty> bounties = bountyRepository.findAllByFinishedEquals(false);
            for (Bounty bounty : bounties){
                if (bounty.getChannelId().equalsIgnoreCase(event.getChannel().getId())){
                    bounty.setWhenLastActive(System.currentTimeMillis());
                    bountyRepository.save(bounty);
                    break;
                }
            }

            if (event.getChannel().getId().equalsIgnoreCase("856772595294142475")){
                event.getGuild().getTextChannelById(event.getChannel().getId()).deleteMessageById(event.getMessageId()).completeAfter(5, TimeUnit.SECONDS);
            }

//            if (!event.getMessage().getMentionedMembers().isEmpty() && event.getMessage().getMentionedMembers().get(0).getId().equalsIgnoreCase("250856681724968960")){
//                System.out.println(event.getMessage().getMentionedMembers());
//                event.getGuild().getTextChannelById(event.getChannel().getId()).deleteMessageById(event.getMessageId()).completeAfter(10, TimeUnit.SECONDS);
//                return;
//            }

            if (cortexMemberRepository.existsByUserID(event.getAuthor().getId())) {

                CortexMember cortexMember = cortexMemberRepository.findByUserIDIs(event.getAuthor().getId());

                cortexMember.setMessagesSent(cortexMember.getMessagesSent() + 1);
                cortexMember.setName(event.getAuthor().getAsTag());

                if (random.nextInt(5) == 3)
                    cortexMember.setPoints(cortexMember.getPoints() + random.nextInt(7));

                cortexMemberRepository.save(cortexMember);

            } else {

                CortexMember cortexMember = new CortexMember();

                cortexMember.setUserID(event.getAuthor().getId());
                cortexMember.setName(event.getAuthor().getAsTag());

                cortexMember.setMessagesSent(1);
                cortexMember.setPoints(1);

                cortexMemberRepository.save(cortexMember);

            }


            if (event.getChannel().getIdLong() == (discordConfiguration.getSuggestionsChannelId())){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl())
                        .setDescription(event.getMessage().getContentRaw());
                eb.setColor(event.getMember().getColorRaw());
                event.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                    m.addReaction(Objects.requireNonNull(event.getGuild().getEmojiById(discordConfiguration.getGreenTickId()))).queue();
                    m.addReaction(Objects.requireNonNull(event.getGuild().getEmojiById(discordConfiguration.getNeutralTickId()))).queue();
                    m.addReaction(Objects.requireNonNull(event.getGuild().getEmojiById(discordConfiguration.getRedTickId()))).queue();
                });
                event.getMessage().delete().queue();
            }


        }
    }

}
