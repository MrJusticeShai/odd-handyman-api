package com.handyman.oddhandyman.bid.service.impl;

import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.auth.repository.UserRepository;
import com.handyman.oddhandyman.bid.dto.BidRequest;
import com.handyman.oddhandyman.bid.entity.Bid;
import com.handyman.oddhandyman.bid.entity.enums.BidStatus;
import com.handyman.oddhandyman.bid.repository.BidRepository;
import com.handyman.oddhandyman.bid.service.BidService;
import com.handyman.oddhandyman.exception.BidNotFoundException;
import com.handyman.oddhandyman.exception.BidUnacceptableException;
import com.handyman.oddhandyman.exception.TaskNotFoundException;
import com.handyman.oddhandyman.exception.UserNotFoundException;
import com.handyman.oddhandyman.task.entity.Task;
import com.handyman.oddhandyman.task.entity.enums.TaskStatus;
import com.handyman.oddhandyman.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link BidService} providing business logic for managing bids.
 * <p>
 * This service handles placing bids, listing bids for a task, and updating bid statuses
 * (accepting or rejecting). It ensures transactional integrity when modifying multiple entities,
 * such as when accepting a bid and rejecting other pending bids for the same task.
 */
@Service
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public BidServiceImpl(BidRepository bidRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.bidRepository = bidRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    /**
     * Places a bid on a task by a specific handyman.
     * <p>
     * Validates that the handyman exists, the task exists, and the task is still PENDING.
     * The new bid is created with status {@link BidStatus#PENDING}.
     *
     * @param req           the {@link BidRequest} containing task ID and bid amount
     * @param handymanEmail the email of the handyman placing the bid
     * @return the saved {@link Bid} entity
     * @throws UserNotFoundException       if the handyman does not exist
     * @throws TaskNotFoundException       if the task does not exist
     * @throws BidUnacceptableException    if the task is not in a PENDING state
     */
    @Transactional
    public Bid placeBid(BidRequest req, String handymanEmail) {
        User handyman = userRepository.findByEmail(handymanEmail)
                .orElseThrow(() -> new UserNotFoundException("Handyman Not Found"));

        Task task = taskRepository.findById(req.getTaskId())
                .orElseThrow(() -> new TaskNotFoundException("Task Not Found"));

        if (task.getStatus() != TaskStatus.PENDING) {
            throw new BidUnacceptableException("Cannot Bid On This Task");
        }

        Bid bid = new Bid();
        bid.setTask(task);
        bid.setHandyman(handyman);
        bid.setAmount(req.getAmount());
        bid.setStatus(BidStatus.PENDING);

        return bidRepository.save(bid);
    }

    /**
     * Retrieves all bids for a given task.
     *
     * @param taskId the ID of the task
     * @return a list of {@link Bid} entities associated with the task
     * @throws TaskNotFoundException if the task does not exist
     */
    public List<Bid> listBidsForTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task Not Found"));
        return bidRepository.findByTask(task);
    }

    /**
     * Accepts a bid, marking its status as {@link BidStatus#ACCEPTED} and
     * automatically rejecting other pending bids for the same task.
     * <p>
     * Also updates the associated task by assigning the handyman and setting its
     * status to {@link TaskStatus#ASSIGNED}.
     *
     * @param bidId the ID of the bid to accept
     * @return the updated {@link Bid} entity
     * @throws TaskNotFoundException if the bid does not exist
     */
    @Transactional
    public Bid acceptBid(Long bidId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new TaskNotFoundException("Bid Not Found"));

        Task task = bid.getTask();

        // Accept this bid
        bid.setStatus(BidStatus.ACCEPTED);
        bidRepository.save(bid);

        // Reject other bids
        bidRepository.findByTaskAndStatus(task, BidStatus.PENDING).forEach(b -> {
            if (!b.getId().equals(bid.getId())) {
                b.setStatus(BidStatus.REJECTED);
                bidRepository.save(b);
            }
        });

        // Assign handyman and update task status
        task.setAssignedHandyman(bid.getHandyman());
        task.setStatus(TaskStatus.ASSIGNED);
        taskRepository.save(task);

        return bid;
    }

    /**
     * Rejects a bid by setting its status to {@link BidStatus#REJECTED}.
     *
     * @param bidId the ID of the bid to reject
     * @return the updated {@link Bid} entity
     * @throws BidNotFoundException if the bid does not exist
     */
    @Transactional
    public Bid rejectBid(Long bidId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new BidNotFoundException("Bid Not Found"));
        bid.setStatus(BidStatus.REJECTED);
        return bidRepository.save(bid);
    }
}
