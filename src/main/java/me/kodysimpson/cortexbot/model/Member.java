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

    /**
     * Amount of points from talking, winning challenges, and such
     */
    private long points;
    /**
     * The amount of messages sent by this user on discord
     */
    private long messagesSent;

    private List<Infraction> muteInfractions = new ArrayList<>();
    private List<Infraction> banInfractions = new ArrayList<>();

    public void addMute(Infraction infraction){
        this.muteInfractions.add(infraction);
    }

    public void addBan(Infraction infraction){
        this.banInfractions.add(infraction);
    }

    public void setPoints(long points) {
        if (points <= 0){
            this.points = 0;
        }else{
            this.points = points;
        }
    }

    public boolean isCurrentlyMuted(){

        for (Infraction infraction : getMuteInfractions()) {
            if (infraction.getExpireDate().isAfter(LocalDateTime.now())) {
                return true;
            }
        }
        return false;
    }

}

