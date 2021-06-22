package me.kodysimpson.cortexbot.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Bounty {

    @Id
    private String id;

    private String userId;
    private String channelId;

    private boolean isFinished;

    private int pointsEarned;
    private String staffId;

}
