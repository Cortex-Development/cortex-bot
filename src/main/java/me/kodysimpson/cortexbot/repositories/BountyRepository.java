package me.kodysimpson.cortexbot.repositories;

import me.kodysimpson.cortexbot.model.Bounty;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BountyRepository extends MongoRepository<Bounty, String> {

    boolean existsBountyByBountyMessageID(String messageID);

    Bounty findBountyByBountyMessageID(String id);

    boolean existsByChannelID(String channelID);

    Bounty findBountyByChannelID(String id);

}

