package dev.cortex.cortexbot.repositories;

import dev.cortex.cortexbot.model.Thanked;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThankedRepository extends MongoRepository<Thanked, String> {

    //get the thanks you have accrued
    List<Thanked> findAllByPersonThankedEquals(String personThanked);

    //get the amount of times you have thanked someone
    long countThankedByThankByEquals(String thankedBy);

    //get the thanks a user have given to another specific user
    List<Thanked> findAllByPersonThankedEqualsAndThankByEquals(String personThanked, String thankedBy);

}
