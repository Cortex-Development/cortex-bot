package dev.cortex.cortexbot.model;

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
    private boolean finished;
    private String staffId;

    private long whenLastActive;

    private long whenLastPrompted;
    private boolean promptResult;

}
