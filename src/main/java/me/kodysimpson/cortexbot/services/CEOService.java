package me.kodysimpson.cortexbot.services;

import me.kodysimpson.cortexbot.model.CEOBid;
import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.repositories.CEOBidRepository;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CEOService {

    private final CEOBidRepository ceoBidRepository;
    private final MemberRepository memberRepository;

    public CEOService(CEOBidRepository ceoBidRepository, MemberRepository memberRepository) {
        this.ceoBidRepository = ceoBidRepository;
        this.memberRepository = memberRepository;
    }

    public void newBid(Member member, int points){

        String currentCEO = this.getCurrentCEO();

        CEOBid bid = null;
        if (ceoBidRepository.existsCEOBidByUserId(member.getUserID())){
            bid = ceoBidRepository.findCEOBidByUserId(member.getUserID());
            bid.setPoints(bid.getPoints() + points);
            ceoBidRepository.save(bid);
        }else{
            bid = new CEOBid();
            bid.setPoints(points);
            bid.setUserId(member.getUserID());
            ceoBidRepository.insert(bid);
        }

        member.setPoints(member.getPoints() - points);
        memberRepository.save(member);

        String updatedCEO = this.getCurrentCEO();
        if (!currentCEO.equalsIgnoreCase(updatedCEO)){
            //take it from the previous ceo
            net.dv8tion.jda.api.entities.Member oldCEO = DiscordBotService.getApi().getGuildById("503656531665879063").getMemberByTag(currentCEO);
            net.dv8tion.jda.api.entities.Member newCEO = DiscordBotService.getApi().getGuildById("503656531665879063").getMemberByTag(updatedCEO);

            DiscordBotService.getApi().getGuildById("503656531665879063").removeRoleFromMember(oldCEO, DiscordBotService.getApi().getGuildById("503656531665879063").getRoleById("771971339526602762")).queue();
            DiscordBotService.getApi().getGuildById("503656531665879063").addRoleToMember(newCEO, DiscordBotService.getApi().getGuildById("503656531665879063").getRoleById("771971339526602762")).queue();

            DiscordBotService.getApi().getTextChannelById("503656532144291862").sendMessage(newCEO.getEffectiveName() + " is the new **CEO** of *Cortex Development*! You can become CEO by trying $ceo-bid.").queue();

            newCEO.getUser().openPrivateChannel().flatMap(channel -> {
                return channel.sendMessage("You are now the CEO of Cortex Development. There are no perks right now other than unlimited swag.");
            }).queue();
        }

    }

    public List<CEOBid> getCurrentBids(){

        return ceoBidRepository.findAll()
                .stream()
                .sorted(Comparator.comparingInt(CEOBid::getPoints).reversed())
                .collect(Collectors.toList());

    }

    public String getCurrentCEO(){

        List<CEOBid> bids = ceoBidRepository.findAll();

        //find the largest
        //default CEO: Kody Simpson
        String ceo = "250856681724968960";
        int points = 0;

        for (CEOBid bid : bids) {
            String key = bid.getUserId();
            int value = bid.getPoints();
            if (value > points) {
                ceo = key;
                points = value;
            }
        }

        return DiscordBotService.getUsernameFromUserID(ceo);
    }

}
