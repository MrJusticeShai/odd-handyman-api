package com.handyman.oddhandyman.bid.service;

import com.handyman.oddhandyman.bid.dto.BidRequest;
import com.handyman.oddhandyman.bid.entity.Bid;
import com.handyman.oddhandyman.bid.entity.enums.BidStatus;

import java.util.List;

/**
 * Service interface for managing bids in the system.
 * <p>
 * Provides operations to place bids, list bids for a task, and update bid status.
 */
public interface BidService {

    /**
     * Places a bid for a given task by a handyman.
     * <p>
     * Steps performed may include: <br>
     * 1. Validating the task exists. <br>
     * 2. Ensuring the handyman is eligible to bid. <br>
     * 3. Creating a new {@link Bid} entity with status {@link BidStatus#PENDING}.
     *
     * @param req the {@link BidRequest} containing task ID and bid amount
     * @param handymanEmail the email of the handyman placing the bid
     * @return the created {@link Bid} entity
     */
    Bid placeBid(BidRequest req, String handymanEmail);

    /**
     * Retrieves all bids placed for a specific task.
     *
     * @param taskId the ID of the task
     * @return a list of {@link Bid} entities associated with the task
     */
    List<Bid> listBidsForTask(Long taskId);

    /**
     * Accepts a bid, marking its status as {@link com.handyman.oddhandyman.bid.entity.enums.BidStatus#ACCEPTED}.
     * <p>
     * Typically also rejects other pending bids for the same task.
     *
     * @param bidId the ID of the bid to accept
     * @return the updated {@link Bid} entity with status ACCEPTED
     */
    Bid acceptBid(Long bidId);

    /**
     * Rejects a bid, marking its status as {@link com.handyman.oddhandyman.bid.entity.enums.BidStatus#REJECTED}.
     *
     * @param bidId the ID of the bid to reject
     * @return the updated {@link Bid} entity with status REJECTED
     */
    Bid rejectBid(Long bidId);
}
