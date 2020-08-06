package me.kodysimpson.cortexbot.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a "ticket" to request help from other users
 */
@Document
@Data
public class Bounty {

    /**
     * Unique identifier of the Bounty within the Database
     */
    @Id
    private String id;

    /**
     * The User account that owns this Bounty
     */
    @DBRef
    private User owner;

    /**
     * Basically the headline of the bounty -- the subject
     */
    @NotBlank(message = "Title is empty")
    @Length(min = 5, max = 25, message = "The title must be between 5 and 25 characters")
    private String title;
    /**
     * The issue explained in as much detail as possible
     */
    @NotBlank(message = "Description is empty")
    @Length(min = 500, message = "The description must be at least 500 characters")
    private String description;
    /**
     * Link to their code, if provided
     */
    private String linkToCode;

    /**
     * The date when this bounty was created
     */
    private Date date = new Date();

    /**
     * The tag that best describes this issue
     * Example: Java, C++, Minecraft
     */
    @NotBlank(message = "Choose a tag that describes this bounty")
    private String tags;

    /**
     * The ID of the bounty message posted in #wanted
     */
    private long bountyMessageID;
    /**
     * The ID of the bounty channel which is created when the bounty is
     */
    private long channelID;

    /**
     * Responses by users on the website and discord
     */
    private List<Message> responses = new ArrayList<>();

    /**
     * @return Returns whether this bounty has a link to the users code
     */
    public boolean hasCodeLink(){
        return linkToCode != null;
    }

}
