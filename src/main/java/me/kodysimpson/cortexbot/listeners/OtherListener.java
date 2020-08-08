package me.kodysimpson.cortexbot.listeners;

import me.kodysimpson.cortexbot.services.DiscordBotService;
import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class OtherListener extends ListenerAdapter {

    //private final Logger logger = LoggerFactory.getLogger(OtherListener.class);
    DiscordBotService discordBotService;
    DiscordConfiguration discordConfiguration;

    public OtherListener(DiscordBotService discordBotService, DiscordConfiguration discordConfiguration) {
        this.discordBotService = discordBotService;
        this.discordConfiguration = discordConfiguration;
    }


    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        discordBotService.addRoleToMember(event.getMember(), discordConfiguration.getMemberRoleId());
    }




}
