package me.kodysimpson.cortexbot.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * The Member class represents each user in the discord server
 * , used to track score and such
 */
@Entity
@Data
public class Member {

    /**
     * Unique ID identifier for each Discord Member
     */
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

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




}

