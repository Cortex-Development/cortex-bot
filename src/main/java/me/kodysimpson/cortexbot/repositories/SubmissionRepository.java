package me.kodysimpson.cortexbot.repositories;

import me.kodysimpson.cortexbot.model.challenges.Submission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends MongoRepository<Submission, String> {

    boolean existsSubmissionByUseridEqualsAndChallengeIdEquals(String userid, String challengeId);

    Submission findSubmissionByUseridEqualsAndChallengeIdEquals(String userid, String challengeId);

    List<Submission> findAllByChallengeIdEquals(String challengeId);

}
