package me.kodysimpson.cortexbot.model;

import lombok.Data;

import java.util.Date;

@Data
public class Message {

    /**
     * The text contents of the response
     */
    private String message;

    /**
     * If posted by a discord user, their unique ID
     */
    private String discordUserID;
    /**
     * If posted by a discord user, the message ID
     */
    private long discordMessageID;
    /**
     * If posted through the website, the User database ID
     */
    private String userID;

    /**
     * Upvotes for the message
     */
    private int upVotes = 0;
    /**
     * Downvotes for the message
     */
    private int downVotes = 0;

    /**
     * The date of when the response was posted
     */
    private Date datePosted = new Date();

    /**
     * @return True if the response was posted on discord, false if through website
     */
    public boolean isDiscordMessage(){
        return this.userID == null;
    }

}

