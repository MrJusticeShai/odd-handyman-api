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

    public List<Bid> listBidsForTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task Not Found"));
        return bidRepository.findByTask(task);
    }

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

    @Transactional
    public Bid rejectBid(Long bidId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new BidNotFoundException("Bid Not Found"));
        bid.setStatus(BidStatus.REJECTED);
        return bidRepository.save(bid);
    }
}
