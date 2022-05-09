package me.kodysimpson.cortexbot.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The Member class represents each user in the discord server
 * , used to track score and such
 */
@Document(collection = "member")
@Data
public class CortexMember {

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

    private int level;

    public void setPoints(long points) {
        if (points <= 0){
            this.points = 0;
        }else{
            this.points = points;
        }
    }

    public void takePoints(long amount) {
        this.points -= amount;
        if (points <= 0) {
            this.points = 0;
        }
    }

    public void addPoints(long amount) {
        this.points += amount;
    }

}

