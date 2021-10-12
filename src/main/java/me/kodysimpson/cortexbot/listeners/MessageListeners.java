package me.kodysimpson.cortexbot.listeners;

import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import me.kodysimpson.cortexbot.model.Bounty;
import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.repositories.BountyRepository;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class MessageListeners extends ListenerAdapter{

    private final Random random;
    private final MemberRepository memberRepository;
    private final DiscordConfiguration discordConfiguration;
    private final BountyRepository bountyRepository;

    @Autowired
    public MessageListeners(MemberRepository memberRepository, DiscordConfiguration discordConfiguration, BountyRepository bountyRepository){
        this.random = new Random();
        this.memberRepository = memberRepository;
        this.discordConfiguration = discordConfiguration;
        this.bountyRepository = bountyRepository;
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {

        if (event.getChannel().getId().equalsIgnoreCase("855669438170267698")){
            event.getGuild().getTextChannelById(event.getChannel().getId()).deleteMessageById(event.getMessageId()).completeAfter(5, TimeUnit.SECONDS);
        }

        if (!event.getAuthor().isBot()) {

            //see if the message was sent in an active bounty channel
            List<Bounty> bounties = bountyRepository.findAllByFinishedEquals(false);
            for (Bounty bounty : bounties){
                if (bounty.getChannelId().equalsIgnoreCase(event.getChannel().getId())){
                    bounty.setLastMessage(new Date());
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

            if (memberRepository.existsByUserID(event.getAuthor().getId())) {

                Member member = memberRepository.findByUserIDIs(event.getAuthor().getId());

                member.setMessagesSent(member.getMessagesSent() + 1);
                member.setName(event.getAuthor().getAsTag());

                if (random.nextInt(5) == 3)
                    member.setPoints(member.getPoints() + random.nextInt(7));

                memberRepository.save(member);

            } else {

                Member member = new Member();

                member.setUserID(event.getAuthor().getId());
                member.setName(event.getAuthor().getAsTag());

                member.setMessagesSent(1);
                member.setPoints(1);

                memberRepository.save(member);

            }


            if (event.getChannel().getIdLong() == (discordConfiguration.getSuggestionsChannelId())){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl())
                        .setDescription(event.getMessage().getContentRaw());
                eb.setColor(event.getMember().getColorRaw());
                event.getChannel().sendMessage(eb.build()).queue(m -> {
                    m.addReaction(Objects.requireNonNull(event.getGuild().getEmoteById(discordConfiguration.getGreenTickId()))).queue();
                    m.addReaction(Objects.requireNonNull(event.getGuild().getEmoteById(discordConfiguration.getNeutralTickId()))).queue();
                    m.addReaction(Objects.requireNonNull(event.getGuild().getEmoteById(discordConfiguration.getRedTickId()))).queue();
                });
                event.getMessage().delete().queue();
            }


        }
    }

}
