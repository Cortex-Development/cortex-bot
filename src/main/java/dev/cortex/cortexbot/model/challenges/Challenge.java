package dev.cortex.cortexbot.model.challenges;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document
public class Challenge {

    @Id
    private String id;

    private String name;
    private String description;
    private ChallengeStatus status;
    private String link;
    private String startedBy; //userid of who made the challenge on discord
    private long startDate;

    private long endDate; //epoch time of when the challenge ends
    private long reward;

    private List<String> submissions = new ArrayList<>(); //ids of submissions to this challenge

    public boolean isActive() {
        return status == ChallengeStatus.ACTIVE;
    }
}
