package me.kodysimpson.cortexbot.commands.ceo;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import me.kodysimpson.cortexbot.services.CEOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CEOBidCommand extends Command {

    private final MemberRepository memberRepository;

    private CEOService ceoService;

    public CEOBidCommand(MemberRepository memberRepository, CEOService ceoService){
        this.memberRepository = memberRepository;
        this.ceoService = ceoService;
        this.name = "ceo-bid";
        this.arguments = "<points>";
    }

    @Autowired
    public void setCeoService(CEOService ceoService) {
        this.ceoService = ceoService;
    }

    @Override
    protected void execute(CommandEvent event) {

        String args = event.getArgs();

        if (args.isEmpty()){
            event.reply("No points were provided to make the bid.");
            return;
        }

        //make sure the amount is a number
        try{
            int points = Integer.parseInt(args);
            if (points > 0){
                Member member = memberRepository.findByUserIDIs(event.getMember().getId());

                //see if they have the points to back it up
                if (member.getPoints() >= points){

                    ceoService.newBid(member, points);

                    event.reply("Bid created. Thank you.");

                    event.getJDA().getTextChannelById("562684703073632286").sendMessage("A new bid of " + points + " point(s) for CEO was placed by " + event.getMember().getEffectiveName()).queue();

                }else{
                    event.reply("You can't afford that! You only have " + member.getPoints() + " point(s).");
                }
            }else{
                event.reply("Bid an amount greater than zero.");
            }

        }catch (NumberFormatException ex){
            event.reply("That was not a number. Provide a number.");
        }


    }
}
