package me.kodysimpson.cortexbot.tasks;

import me.kodysimpson.cortexbot.model.Bounty;
import me.kodysimpson.cortexbot.repositories.BountyRepository;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import me.kodysimpson.cortexbot.services.DiscordBotService;
import net.dv8tion.jda.api.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static me.kodysimpson.cortexbot.services.DiscordBotService.getGuild;

@Service
public class PassiveIncomeTask {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    BountyRepository bountyRepository;

    @Scheduled(initialDelay = 60000)
    public void announceStart(){
        getGuild().getTextChannelById("786974733123846214").sendMessage("Cortex bot redeployed.").queue();
    }

    @Scheduled(fixedRate = 3600000)
    public void payMembers(){

        System.out.println("Running Passive Income Task");

        memberRepository.findAll().stream()
                .forEach(member -> {
                    member.setPoints(member.getPoints() + 1);
                    memberRepository.save(member);
                });

    }

    @Scheduled(fixedRate = 120000, initialDelay = 120000)
    public void updateBountiesList(){

        System.out.println("Bounties lb");

        List<Bounty> bounties = bountyRepository.findAllByFinishedEquals(false);

        MessageBuilder message = new MessageBuilder();

        if (!bounties.isEmpty()){
            message.append("---------------------------------------------------------------------------------------------", MessageBuilder.Formatting.STRIKETHROUGH).append("\n");
            message.append("Active Bounties:", MessageBuilder.Formatting.BOLD).append("\n\n");

            for (int i = 0; i < bounties.size(); i++){
                message.append("[ #" + (i + 1) + " ] - ", MessageBuilder.Formatting.BOLD).append(DiscordBotService.getUsernameFromUserID(bounties.get(i).getUserId()) + " *-* " + "<#" + bounties.get(i).getChannelId() + ">").append("\n");
            }

            message.append("\n");
            message.append("---------------------------------------------------------------------------------------------", MessageBuilder.Formatting.STRIKETHROUGH);
            message.append("\n\n*updated every 2 mins*");
        }else{
            message.append("---------------------------------------------------------------------------------------------", MessageBuilder.Formatting.STRIKETHROUGH).append("\n");
            message.append("Active Bounties:", MessageBuilder.Formatting.BOLD).append("\n\n");

            message.append("No active bounties currently!");

            message.append("\n");
            message.append("---------------------------------------------------------------------------------------------", MessageBuilder.Formatting.STRIKETHROUGH);
            message.append("\n\n*updated every 2 mins*");
        }

//        String last = DiscordBotService.getGuild().getTextChannelById("856780175449784347").getLatestMessageId();
//        DiscordBotService.getGuild().getTextChannelById("856780175449784347").deleteMessageById(last).queue();
        getGuild().getTextChannelById("856780175449784347").editMessageById("856784347231158272", message.build()).queue();

    }

}
