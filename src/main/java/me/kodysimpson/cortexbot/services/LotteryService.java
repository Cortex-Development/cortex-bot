package me.kodysimpson.cortexbot.services;

import me.kodysimpson.cortexbot.model.CortexMember;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//Responsible for giving out a random amount of points for those who register for the lottery
@Service
public class LotteryService {

    private long jackpot;
    private List<CortexMember> entered;

    public LotteryService() {
        this.jackpot = 0;
        this.entered = new ArrayList<>();
    }

    public void initiateJackpot(){

        System.out.println("Initiating jackpot...");

        //Go through each of the entered members and give them a random amount of points.
        // Loop again until there are no more points to give out.

        while(jackpot > 0){
            for(CortexMember cortexMember : entered){

                //Choose a random amount of points to give, max being 100 or the jackpot amount if it is less
                long pointsToGive = (long) (Math.random() * Math.min(100, jackpot));

                //Give the member the points
                System.out.println("Giving " + pointsToGive + " points to " + cortexMember.getName());

                //Subtract the points from the jackpot
                jackpot -= pointsToGive;
            }
        }

        System.out.println("Jackpot has been cleared!");

    }

    public void setJackpot(long jackpot) {
        this.jackpot = jackpot;
    }

    public long getJackpot() {
        return jackpot;
    }

    public void addEntered(CortexMember cortexMember) {
        entered.add(cortexMember);
    }

    public List<CortexMember> getEntered() {
        return entered;
    }

    public void clearEntered() {
        entered.clear();
    }

}
