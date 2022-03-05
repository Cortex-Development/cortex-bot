package me.kodysimpson.cortexbot.model.challenges;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class Submission {

    @Id
    private String id;

    private String challengeId;

    private String userid;
    private String channel;
    private boolean won; //if they won the challenge
    private long date; //when they submitted

}
