package me.kodysimpson.cortexbot.repositories;

import me.kodysimpson.cortexbot.model.Bounty;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BountyRepository extends MongoRepository<Bounty, String> {

    boolean existsBountyByBountyMessageID(long bountyMessageID);

    Bounty findBountyByBountyMessageID(long bountyMessageID);

    boolean existsByChannelID(long channelID);

    Bounty findBountyByChannelID(long id);

}

