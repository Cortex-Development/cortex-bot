package me.kodysimpson.cortexbot.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
public class Bounty {

    @Id
    private String id;

    private String userId;
    private String channelId;

    private boolean finished;

    private String staffId;

    private Date lastMessage;

}
