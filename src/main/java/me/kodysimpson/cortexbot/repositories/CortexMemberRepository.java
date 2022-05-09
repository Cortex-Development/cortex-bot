package me.kodysimpson.cortexbot.repositories;

import me.kodysimpson.cortexbot.model.CortexMember;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CortexMemberRepository extends MongoRepository<CortexMember, String> {

    @Override
    @NotNull
    List<CortexMember> findAll();

    boolean existsByUserID(String userID);

    CortexMember findByUserIDIs(String userID);

}

