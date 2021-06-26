package me.kodysimpson.cortexbot.commands.fitness;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.repositories.FitnessRepository;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import org.springframework.stereotype.Component;

@Component
public class FitnessCommand extends Command {

    private final FitnessRepository fitnessRepository;
    private final MemberRepository memberRepository;

    public FitnessCommand(FitnessRepository fitnessRepository, MemberRepository memberRepository){
        this.fitnessRepository = fitnessRepository;
        this.memberRepository = memberRepository;
        this.name = "fitness";
        this.help = "enroll/quit the legendary Cortex Fitness Program";
    }

    @Override
    protected void execute(CommandEvent event) {

        //get the user involved in this event
        Member member = memberRepository.findByUserIDIs(event.getMember().getId());

        //see if they are enrolled
        if (fitnessRepository.existsMemberByUserID(member.getUserID())){
            fitnessRepository.delete(member);
            event.reply("You have left the legendary **Cortex Fitness Program**. Good. We don't want weaklings.");
        }else{
            fitnessRepository.insert(member);
            event.reply("You are now enrolled in the legendary **Cortex Fitness Program**. Instructions will be given shortly. Follow this exactly and you will be ripped in no-time.");
        }

    }
}
