package me.kodysimpson.cortexbot.model;

import lombok.Data;
import me.kodysimpson.cortexbot.model.infractions.Infraction;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The Member class represents each user in the discord server
 * , used to track score and such
 */
@Document
@Data
public class Member {

    /**
     * Unique ID identifier for each Discord Member
     */
    @Id
    private String id;

    /**
     * Their discord unique ID
     */
    private String userID;

    private String name;

    /**
     * Amount of points from talking, winning challenges, and such
     */
    private long points;
    /**
     * The amount of messages sent by this user on discord
     */
    private long messagesSent;

    //total infractions(expired)
    private List<Infraction> expiredInfractions = new ArrayList<>();

    //Represents if they are currently muted or banned
    private Infraction mute = null;
    private Infraction ban = null;

    public void addMute(Infraction infraction){

        if (this.isCurrentlyMuted()){
            this.expiredInfractions.add(this.mute);
            this.mute = null;
        }
        this.mute = infraction;

    }

//    public void addBan(Infraction infraction){
//        this.banInfractions.add(infraction);
//    }

    public void setPoints(long points) {
        if (points <= 0){
            this.points = 0;
        }else{
            this.points = points;
        }
    }

    public boolean isCurrentlyMuted(){

        if (this.mute != null){
            if (this.mute.getExpireDate().isAfter(LocalDateTime.now())) {
                return true;
            }else{
                this.expiredInfractions.add(this.mute);
                this.mute = null;
                return false;
            }
        }

        return false;
    }

    /**
     * @return Returns true if unmuted, false if they were never muted
     */
    public boolean unmute(){

        if (this.mute != null){
            this.expiredInfractions.add(this.mute);
            this.mute = null;
            return true;
        }
        return false;
    }

}

