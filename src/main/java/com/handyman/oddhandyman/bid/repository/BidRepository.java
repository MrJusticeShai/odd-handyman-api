package com.handyman.oddhandyman.bid.repository;

import com.handyman.oddhandyman.bid.entity.Bid;
import com.handyman.oddhandyman.bid.entity.enums.BidStatus;
import com.handyman.oddhandyman.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for performing CRUD operations on {@link Bid} entities.
 * <p>
 * Extends {@link JpaRepository} to provide standard JPA operations (save, findById, delete, etc.)
 * and declares custom query methods specific to bids.
 */
public interface BidRepository extends JpaRepository<Bid, Long> {

    /**
     * Retrieves all bids associated with a given task.
     *
     * @param task the task for which bids should be retrieved
     * @return a list of {@link Bid} entities associated with the task
     */
    List<Bid> findByTask(Task task);

    /**
     * Retrieves all bids associated with a given task that have a specific status.
     *
     * @param task the task for which bids should be retrieved
     * @param status the {@link BidStatus} to filter by
     * @return a list of {@link Bid} entities matching the task and status
     */
    List<Bid> findByTaskAndStatus(Task task, BidStatus status);
}
