package com.handyman.oddhandyman.bid.repository;

import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.bid.entity.Bid;
import com.handyman.oddhandyman.bid.entity.enums.BidStatus;
import com.handyman.oddhandyman.task.entity.Task;
import com.handyman.oddhandyman.task.entity.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class BidRepositoryTest {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User handyman;
    private Task task;

    @BeforeEach
    void setUp() {
        // 1. Create and persist User (Handyman)
        handyman = new User();
        handyman.setEmail("handy@man.com");
        handyman.setName("Bob Builder");
        handyman.setPassword("encoded_pass");
        handyman.setRole(Role.HANDYMAN);
        entityManager.persist(handyman);

        // 2. Create and persist Task
        task = new Task();
        task.setTitle("Fix Roof");
        task.setDescription("Leaking roof needs fixing");
        task.setStatus(TaskStatus.PENDING);
        entityManager.persist(task);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find all bids for a specific task")
    void findByTask_ReturnsBids() {
        // Given
        Bid bid1 = new Bid();
        bid1.setTask(task);
        bid1.setHandyman(handyman);
        bid1.setAmount(500.0);
        bid1.setStatus(BidStatus.PENDING);
        entityManager.persist(bid1);

        Bid bid2 = new Bid();
        bid2.setTask(task);
        bid2.setHandyman(handyman);
        bid2.setAmount(600.0);
        bid2.setStatus(BidStatus.REJECTED);
        entityManager.persist(bid2);

        entityManager.flush();

        // When
        List<List<Bid>> results = List.of(bidRepository.findByTask(task));

        // Then
        assertEquals(2, results.get(0).size());
    }

    @Test
    @DisplayName("Should find bids by task and status")
    void findByTaskAndStatus_ReturnsFilteredBids() {
        // Given
        Bid pendingBid = new Bid();
        pendingBid.setTask(task);
        pendingBid.setHandyman(handyman);
        pendingBid.setAmount(100.0);
        pendingBid.setStatus(BidStatus.PENDING);
        entityManager.persist(pendingBid);

        Bid acceptedBid = new Bid();
        acceptedBid.setTask(task);
        acceptedBid.setHandyman(handyman);
        acceptedBid.setAmount(200.0);
        acceptedBid.setStatus(BidStatus.ACCEPTED);
        entityManager.persist(acceptedBid);

        entityManager.flush();

        // When
        List<Bid> results = bidRepository.findByTaskAndStatus(task, BidStatus.PENDING);

        // Then
        assertEquals(1, results.size());
        assertEquals(BidStatus.PENDING, results.get(0).getStatus());
        assertEquals(100.0, results.get(0).getAmount());
    }

    @Test
    @DisplayName("Should return empty list if task has no bids")
    void findByTask_NoBids_ReturnsEmptyList() {
        // When
        List<Bid> results = bidRepository.findByTask(task);

        // Then
        assertTrue(results.isEmpty());
    }
}
