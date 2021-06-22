package me.kodysimpson.cortexbot.repositories;

import me.kodysimpson.cortexbot.model.Bounty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BountyRepository extends MongoRepository<Bounty, String> {

    Bounty deleteBountyByUserIdEquals(String userId);

    Bounty deleteBountyByChannelIdEquals(String channelId);

    boolean existsBountyByUserIdEquals(String userId);

    boolean existsBountyByChannelIdEquals(String channelId);

    Bounty findBountyByChannelIdEquals(String channelId);

}
