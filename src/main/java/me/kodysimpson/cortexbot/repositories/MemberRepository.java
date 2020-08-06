package me.kodysimpson.cortexbot.repositories;

import me.kodysimpson.cortexbot.model.Member;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MemberRepository extends CrudRepository<Member, Long> {

    @Override
    @NotNull
    List<Member> findAll();

    boolean existsByUserID(String userID);

    Member findByUserIDIs(String userID);

}

