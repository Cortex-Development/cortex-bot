package dev.cortex.cortexbot.repositories;

import dev.cortex.cortexbot.model.challenges.Challenge;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChallengeRepository extends MongoRepository<Challenge, String> {

    //see if there is an active challenge currently
    boolean existsChallengeByEndDateGreaterThan(long l);

    //get the active challenge
    Challenge findChallengeByEndDateGreaterThan(long l);

}
