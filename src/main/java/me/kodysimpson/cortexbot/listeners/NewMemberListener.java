package me.kodysimpson.cortexbot.listeners;

import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import me.kodysimpson.cortexbot.services.DiscordBotService;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class NewMemberListener extends ListenerAdapter {

    private final DiscordBotService discordBotService;
    private final DiscordConfiguration discordConfiguration;

    public NewMemberListener(DiscordBotService discordBotService, DiscordConfiguration discordConfiguration) {
        this.discordBotService = discordBotService;
        this.discordConfiguration = discordConfiguration;
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        discordBotService.addRoleToMember(event.getMember(), discordConfiguration.getMemberRoleId(), new Consumer<Void>() {
            @Override
            public void accept(Void unused) {
                event.getMember().getUser().openPrivateChannel().queue(privateChannel -> {
                    MessageBuilder messageBuilder = new MessageBuilder();
                    messageBuilder.append("**Welcome to Cortex Development!!!**").append("\n\n")
                            .append("""
                                My name is Kody and I would like to welcome you to our community.
                                This community is centered around people who are enthusiastic about coding or
                                want to learn new things. It was created as a community for my 
                                Youtube channel(https://youtube.com/c/KodySimpson), but has grown significantly in other aspects. 
                                Here you can find new friends(if you don't have any), ask for help/advice with something 
                                you are stuck on, and just talk computer science. Please check out the #server-info channel 
                                for a full catalog of information to help you understand what this is all about. 
                                
                                If you already know a thing or two, I encourage you to get involved in the community by helping others
                                in the help channels. By doing such things, you can earn community points. Community points are a way for you
                                to flex on the haters and show how much of a pro you are. You can get points from helping people,
                                talking, contributing to our open source projects, etc. :)
                                
                                If you have any suggestions on how you think the community can be improved, plop a 
                                message in the *#suggestions* channel. Run **$help** to see the bot commands. 
                                
                                *Nice to meet ya!*
                                """);
                    privateChannel.sendMessage(messageBuilder.build()).queue();
                });
            }
        });
    }

}
