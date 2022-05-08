package me.kodysimpson.cortexbot.services;

import me.kodysimpson.cortexbot.model.Member;

// TODO - WIP
/*
    * This class is used to manage the leveling system for Cortex Development.
    *
    * Levels can be bought with points. Levels will give access to certain perks.
    *
    * Everyone starts at level 0. Someone's level is shown as a role.
    *
 */
public class LevelerService {

    /*
        * Level someone up if they have enough points.
     */
    public void levelUp(Member member){

        int currentLevel = member.getLevel();

        int price = getLevelPrice(currentLevel + 1);

    }

    /*
     * Level prices:
     * 1-10: 100 points
     * 11-20: 250 points
     * 21-30: 450 points
     * 31-40: 675 points
     * 41-45: 1000 points
     * 46: 2000 points
     * 47: 3000 points
     * 48: 4000 points
     * 49: 5000 points
     * 50(max): 10000 points
     */
    public int getLevelPrice(int level){

        if (level <= 10){
            return 100;
        }else if (level <= 20){
            return 250;
        }else if (level <= 30){
            return 450;
        }else if (level <= 40){
            return 675;
        }else if (level <= 45){
            return 1000;
        }else if (level <= 46){
            return 2000;
        }else if (level <= 47){
            return 3000;
        }else if (level <= 48){
            return 4000;
        }else if (level <= 49){
            return 5000;
        }else{
            return 10000;
        }

    }

}
