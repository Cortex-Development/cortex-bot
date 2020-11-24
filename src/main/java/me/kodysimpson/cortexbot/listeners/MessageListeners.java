package me.kodysimpson.cortexbot.listeners;

import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import me.kodysimpson.cortexbot.services.DiscordBotService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.managers.EmoteManagerImpl;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Random;

public class MessageListeners extends ListenerAdapter{

    private final Random random;
    private final MemberRepository memberRepository;
    private final DiscordConfiguration discordConfiguration;
    private final DiscordBotService discordBotService;

    public MessageListeners(MemberRepository memberRepository, DiscordConfiguration discordConfiguration, DiscordBotService discordBotService){
        this.random = new Random();
        this.memberRepository = memberRepository;
        this.discordConfiguration = discordConfiguration;
        this.discordBotService = discordBotService;
    }


    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {

        if (!event.getAuthor().isBot()) {
            if (memberRepository.existsByUserID(event.getAuthor().getId())) {

                Member member = memberRepository.findByUserIDIs(event.getAuthor().getId());

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


            System.out.println(discordBotService.getApi().getEmotes());

            if (event.getChannel().getIdLong() == (discordConfiguration.getSuggestionsChannelId())){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl())
                        .setDescription(event.getMessage().getContentRaw());
                eb.setColor(event.getMember().getColorRaw());
                event.getChannel().sendMessage(eb.build()).queue(m -> {
                    m.addReaction("\uD83D\uDC4D\uD83C\uDFFD").queue();
                    m.addReaction("\uD83E\uDD37\uD83C\uDFFE").queue();
                    m.addReaction("\uD83D\uDC4E\uD83C\uDFFD").queue();
                });
                event.getMessage().delete().queue();
            }


        }
    }

}
