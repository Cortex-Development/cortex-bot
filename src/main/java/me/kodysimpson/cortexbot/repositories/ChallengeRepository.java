package me.kodysimpson.cortexbot.repositories;

import me.kodysimpson.cortexbot.model.challenges.Challenge;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChallengeRepository extends MongoRepository<Challenge, String> {

    //see if there is an active challenge currently
    boolean existsChallengeByEndDateGreaterThan(long l);

    //get the active challenge
    Challenge findChallengeByEndDateGreaterThan(long l);

}
