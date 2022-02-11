package me.kodysimpson.cortexbot.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class Challenge {

    @Id
    private String id;

    private String name;
    private String description;
    private String link;
    private String startedBy; //userid of who made the challenge on discord
    private long startDate;
    private long endDate;

    public boolean isActive(){
        return new Date().getTime() < endDate;
    }

}
