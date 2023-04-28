package dev.cortex.cortexbot.repositories;

import dev.cortex.cortexbot.model.CEOBid;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CEOBidRepository extends MongoRepository<CEOBid, String> {

    List<CEOBid> findAllByUserIdEquals(String userId);

    boolean existsCEOBidByUserId(String userId);

    CEOBid findCEOBidByUserId(String userId);

}
