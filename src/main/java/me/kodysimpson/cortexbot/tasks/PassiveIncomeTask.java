package me.kodysimpson.cortexbot.tasks;

import me.kodysimpson.cortexbot.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PassiveIncomeTask {

    @Autowired
    private MemberRepository memberRepository;

    @Scheduled(fixedRate = 3600000)
    public void payMembers(){

        System.out.println("Running Passive Income Task");

        memberRepository.findAll().stream()
                .forEach(member -> {
                    member.setPoints(member.getPoints() + 1);
                });

    }

}
