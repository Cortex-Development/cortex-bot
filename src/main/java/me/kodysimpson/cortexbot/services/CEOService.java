package me.kodysimpson.cortexbot.services;

import me.kodysimpson.cortexbot.model.CEOBid;
import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.repositories.CEOBidRepository;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.*;

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

        CEOBid bid = new CEOBid();
        bid.setPoints(points);
        bid.setUserId(member.getUserID());
        ceoBidRepository.insert(bid);

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

    public HashMap<String, Integer> getCurrentBids(){
        HashMap<String, Integer> count = new HashMap<>();

        ceoBidRepository.findAll()
                .forEach(bid -> {
                    if (count.containsKey(bid.getUserId())){
                        count.replace(bid.getUserId(), count.get(bid.getUserId()) + bid.getPoints());
                    }else{
                        count.put(bid.getUserId(), bid.getPoints());
                    }
                });

        Comparator<Map.Entry<String, Integer>> valueComparator = new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        };

        List<Map.Entry<String, Integer>> listOfEntries = new ArrayList<Map.Entry<String, Integer>>(count.entrySet());
        Collections.sort(listOfEntries, valueComparator);

        return count;
    }

    public String getCurrentCEO(){

        HashMap<String, Integer> count = new HashMap<>();

        ceoBidRepository.findAll()
                .forEach(bid -> {
                    if (count.containsKey(bid.getUserId())){
                        count.replace(bid.getUserId(), count.get(bid.getUserId()) + bid.getPoints());
                    }else{
                        count.put(bid.getUserId(), bid.getPoints());
                    }
                });

        //find the largest
        //default CEO: Kody Simpson
        String ceo = "Kody Simpson";
        int points = 0;

        for (Map.Entry<String, Integer> entry : count.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (value > points) {
                ceo = key;
                points = value;
            }
        }

        return DiscordBotService.getUsernameFromUserID(ceo);
    }

}
