package com.oddhandyman.bid.service.impl;

import com.oddhandyman.auth.entity.User;
import com.oddhandyman.auth.repository.UserRepository;
import com.oddhandyman.bid.dto.BidRequest;
import com.oddhandyman.bid.entity.Bid;
import com.oddhandyman.bid.entity.enums.BidStatus;
import com.oddhandyman.bid.repository.BidRepository;
import com.oddhandyman.bid.service.BidService;
import com.oddhandyman.task.entity.Task;
import com.oddhandyman.task.entity.enums.TaskStatus;
import com.oddhandyman.task.repository.TaskRepository;
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
                .orElseThrow(() -> new RuntimeException("Handyman not found"));

        Task task = taskRepository.findById(req.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task.getStatus() != TaskStatus.PENDING) {
            throw new RuntimeException("Cannot bid on this task");
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
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return bidRepository.findByTask(task);
    }

    @Transactional
    public Bid acceptBid(Long bidId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Bid not found"));

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
                .orElseThrow(() -> new RuntimeException("Bid not found"));
        bid.setStatus(BidStatus.REJECTED);
        return bidRepository.save(bid);
    }
}
