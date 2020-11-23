package me.kodysimpson.cortexbot.tasks;

import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import me.kodysimpson.cortexbot.services.DiscordBotService;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class MuteManagerTask {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    DiscordBotService discordBotService;

    @Autowired
    DiscordConfiguration discordConfiguration;

    @Scheduled(initialDelay = 30000L, fixedRate = 30000L)
    public void assignMuteRole(){

        Role mutedRole = discordBotService.getGuild().getRoleById(discordConfiguration.getMuteRole());

        if (mutedRole != null){
            memberRepository.findAll().stream()
                    .filter(member -> !member.isCurrentlyMuted())
                    .filter(member -> Objects.requireNonNull(discordBotService.getGuild().getMemberById(member.getUserID())).getRoles().contains(mutedRole))
                    .forEach(member -> discordBotService.getGuild().removeRoleFromMember(member.getUserID(), mutedRole).queue());
        }else{
            System.out.println("An invalid ID might be set for the mute role");
        }

    }

}
