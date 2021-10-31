package me.kodysimpson.cortexbot.repositories;

import me.kodysimpson.cortexbot.model.Member;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MemberRepository extends MongoRepository<Member, String> {

    @Override
    @NotNull
    List<Member> findAll();

    boolean existsByUserID(String userID);

    Member findByUserIDIs(String userID);

    List<Member> findMembersByVeteranEquals(boolean isVeteran);

}

