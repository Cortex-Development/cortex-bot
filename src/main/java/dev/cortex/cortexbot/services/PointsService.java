package dev.cortex.cortexbot.services;

import dev.cortex.cortexbot.repositories.CortexMemberRepository;
import dev.cortex.cortexbot.model.CortexMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointsService {

    @Autowired
    CortexMemberRepository cortexMemberRepository;

    public long getPoints(String userID){

        CortexMember cortexMember = cortexMemberRepository.findByUserIDIs(userID);

        if (cortexMember == null){
            return -1;
        }else{
            return cortexMember.getPoints();
        }

    }

}
