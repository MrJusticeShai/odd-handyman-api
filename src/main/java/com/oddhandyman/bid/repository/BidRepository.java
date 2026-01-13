package com.oddhandyman.bid.repository;

import com.oddhandyman.bid.entity.Bid;
import com.oddhandyman.bid.entity.enums.BidStatus;
import com.oddhandyman.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByTask(Task task);
    List<Bid> findByTaskAndStatus(Task task, BidStatus status);
}
