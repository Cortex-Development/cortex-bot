package dev.cortex.cortexbot.model.challenges;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Submission {

    @Id
    private String id;

    private String challengeId;

    private String userid;
    private String channel;
    private ChallengeGrade status;
    private long date; //when they submitted

}
