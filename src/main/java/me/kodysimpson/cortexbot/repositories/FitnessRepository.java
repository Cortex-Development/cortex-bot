package me.kodysimpson.cortexbot.repositories;

import me.kodysimpson.cortexbot.model.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FitnessRepository extends MongoRepository<Member, String> {

    boolean existsMemberByUserID(String userID);


}
