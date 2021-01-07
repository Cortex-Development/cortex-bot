package me.kodysimpson.cortexbot.listeners;

import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Random;

public class MessageListeners extends ListenerAdapter{

    private final Random random;
    private final MemberRepository memberRepository;
    private final DiscordConfiguration discordConfiguration;

    public MessageListeners(MemberRepository memberRepository, DiscordConfiguration discordConfiguration){
        this.random = new Random();
        this.memberRepository = memberRepository;
        this.discordConfiguration = discordConfiguration;
    }


    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {

        if (!event.getAuthor().isBot()) {
            if (memberRepository.existsByUserID(event.getAuthor().getId())) {

                Member member = memberRepository.findByUserIDIs(event.getAuthor().getId());

                //unmute the
                if (!member.isCurrentlyMuted()){
                    Role mutedRole = event.getGuild().getRoleById(discordConfiguration.getMuteRole());

                    net.dv8tion.jda.api.entities.Member discordMember = event.getGuild().getMemberById(member.getUserID());

                    if (discordMember != null){
                        if (discordMember.getRoles().contains(mutedRole)){
                            event.getGuild().removeRoleFromMember(member.getUserID(), mutedRole).queue();
                        }
                    }


                }

                member.setMessagesSent(member.getMessagesSent() + 1);

                if (random.nextInt(5) == 3)
                    member.setPoints(member.getPoints() + random.nextInt(7));

                memberRepository.save(member);

            } else {

                Member member = new Member();

                member.setUserID(event.getAuthor().getId());

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
