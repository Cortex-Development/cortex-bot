package me.kodysimpson.cortexbot.services;

import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointsService {

    @Autowired
    MemberRepository memberRepository;

    public long getPoints(String userID){

        Member member = memberRepository.findByUserIDIs(userID);

        if (member == null){
            return -1;
        }else{
            return member.getPoints();
        }

    }

}
