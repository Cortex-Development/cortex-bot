package me.kodysimpson.cortexbot.tasks;

import me.kodysimpson.cortexbot.model.Bounty;
import me.kodysimpson.cortexbot.repositories.BountyRepository;
import me.kodysimpson.cortexbot.repositories.ChallengeRepository;
import me.kodysimpson.cortexbot.repositories.CortexMemberRepository;
import me.kodysimpson.cortexbot.services.DiscordBot;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static me.kodysimpson.cortexbot.services.DiscordBot.getGuild;

@Service
public class BotTasks {

    private final CortexMemberRepository cortexMemberRepository;
    private final BountyRepository bountyRepository;
    private final ChallengeRepository challengeRepository;

    @Autowired
    public BotTasks(CortexMemberRepository cortexMemberRepository, BountyRepository bountyRepository, ChallengeRepository challengeRepository) {
        this.cortexMemberRepository = cortexMemberRepository;
        this.bountyRepository = bountyRepository;
        this.challengeRepository = challengeRepository;
    }

    @Scheduled(fixedRate = 864000000, initialDelay = 60000)
    public void announceStart(){
        getGuild().getTextChannelById("786974733123846214").sendMessage("Cortex bot redeployed. Version: 1.3.2").queue();
    }

    @Scheduled(initialDelay = 120000L, fixedRate = 120000L)
    public void updateBountiesList(){

        System.out.println("...Bounties Leaderboard...");

        List<Bounty> unfinishedBounties = bountyRepository.findAllByFinishedEquals(false);

        MessageBuilder message = new MessageBuilder("""
                **~~---------------------------------------------------------------------------------------------~~**
                **Help Bounties**
                Help bounties are channels you can create just for your specific thing that you need help with. After the issue is solved, you can thank those who helped you and close it.\s
                                
                After creating the channel, describe what you need help with in as much detail as possible. Do not use the bounty channels as casual chatrooms. Speak about the issue at hand.

                """);

        if (!unfinishedBounties.isEmpty()){

            message.append("Active Bounties:", MessageBuilder.Formatting.BOLD).append("\n\n");

            //Prompt the bounty owner to tell us if they still need help or not
            // if enough time has passed with no messages.
            for(int i = 0; i < unfinishedBounties.size(); i++){

                Bounty bounty = unfinishedBounties.get(i);

                //If the channel associated with the bounty does not exist, remove the bounty
                TextChannel channel = getGuild().getTextChannelById(bounty.getChannelId());

                if(channel == null){
                    bountyRepository.delete(bounty);
                    continue;
                }

                //see if the bounty has not been active for more than 36 hours
//                if(System.currentTimeMillis() - bounty.getWhenLastActive() > 129600000){
//                    channel.sendMessage("The bounty has been inactive for more than 36 hours. Do you still need help?").queue();
//                }

                message.append("[ #" + (i + 1) + " ] - ", MessageBuilder.Formatting.BOLD)
                        .append(DiscordBot.getUsernameFromUserID(unfinishedBounties.get(i).getUserId())).append(" *-* ").append("<#").append(unfinishedBounties.get(i).getChannelId()).append(">")
                        .append("\n");
            }

            message.append("\n\n*updated every 2 mins* [<t:").append(String.valueOf(System.currentTimeMillis() / 1000)).append(":R>]");
        }else{
            message.append("Active Bounties:", MessageBuilder.Formatting.BOLD).append("\n\n");

            message.append("No active bounties currently!");

            message.append("\n");
            message.append("---------------------------------------------------------------------------------------------", MessageBuilder.Formatting.STRIKETHROUGH);
            message.append("\n\n*updated every 2 mins* [<t:").append(String.valueOf(System.currentTimeMillis() / 1000)).append(":R>]");
        }

        message.setActionRows(ActionRow.of(
                Button.primary("new-bounty", "New Bounty"),
                Button.danger("delete-bounty", "Delete Bounty")
        ));

        getGuild().getTextChannelById("856780175449784347").editMessageById("856784347231158272", message.build()).queue();

    }

    //TODO - Provide a mechanism to allow people to report when they got helped
    // and also show a list of the weekly helpers
    @Scheduled(initialDelay = 120000, fixedRate = 120000)
    public void updateHelpedChannel(){

        System.out.println("...I got helped channel...");

        MessageBuilder message = new MessageBuilder("""
                **~~---------------------------------------------------------------------------------------------~~**
                **Did someone help you?**
                If you got helped, hit the button below and provide proof of them helping you and they will be compensated. Or, leave a message below yourself.\s

                You can also directly thank someone with */thank* and provide a tip.

                """);

        message.setActionRows(ActionRow.of(
                Button.primary("i-got-helped", "I Got Helped!")
        ));

        getGuild().getTextChannelById("838841366498246757").editMessageById("972863421517299772", message.build()).queue();

    }

}