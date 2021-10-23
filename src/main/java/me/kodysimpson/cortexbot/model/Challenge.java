package me.kodysimpson.cortexbot.model;

import lombok.Data;
import net.dv8tion.jda.api.entities.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class Challenge {

    @Id
    private String id;

    private String challengeName;
    private String description;
    private String link;
    private long points;
    private String startedBy; //userid of who made the challenge on discord
    private long startDate;
    private long endDate;

    public boolean isActive(){
        return new Date().getTime() < endDate;
    }

}
