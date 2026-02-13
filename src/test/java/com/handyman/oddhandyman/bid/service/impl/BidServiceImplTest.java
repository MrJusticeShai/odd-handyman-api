package com.handyman.oddhandyman.bid.service.impl;

import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.auth.repository.UserRepository;
import com.handyman.oddhandyman.bid.dto.BidRequest;
import com.handyman.oddhandyman.bid.entity.Bid;
import com.handyman.oddhandyman.bid.entity.enums.BidStatus;
import com.handyman.oddhandyman.bid.repository.BidRepository;
import com.handyman.oddhandyman.exception.BidUnacceptableException;
import com.handyman.oddhandyman.task.entity.Task;
import com.handyman.oddhandyman.task.entity.enums.TaskStatus;
import com.handyman.oddhandyman.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BidServiceImplTest {

    @Mock private BidRepository bidRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private BidServiceImpl bidService;

    private User mockHandyman;
    private Task mockTask;
    private Bid mockBid;

    @BeforeEach
    void setUp() {
        mockHandyman = new User();
        mockHandyman.setEmail("handy@man.com");

        mockTask = new Task();
        mockTask.setId(100L);
        mockTask.setStatus(TaskStatus.PENDING);

        mockBid = new Bid();
        mockBid.setId(1L);
        mockBid.setTask(mockTask);
        mockBid.setHandyman(mockHandyman);
        mockBid.setStatus(BidStatus.PENDING);
    }

    @Nested
    @DisplayName("placeBid() Tests")
    class PlaceBidTests {
        @Test
        @DisplayName("Positive: Should place bid when task is PENDING")
        void placeBid_Success() {
            BidRequest req = new BidRequest();
            req.setTaskId(100L);
            req.setAmount(250.0);

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockHandyman));
            when(taskRepository.findById(100L)).thenReturn(Optional.of(mockTask));
            when(bidRepository.save(any(Bid.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Bid result = bidService.placeBid(req, "handy@man.com");

            assertNotNull(result);
            assertEquals(BidStatus.PENDING, result.getStatus());
            verify(bidRepository).save(any(Bid.class));
        }

        @Test
        @DisplayName("Negative: Should throw exception if task status is not PENDING")
        void placeBid_TaskNotPending_Fails() {
            BidRequest req = new BidRequest();
            req.setTaskId(100L); // <--- This must match the when(...) below
            req.setAmount(500.0);

            mockTask.setStatus(TaskStatus.ASSIGNED);
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockHandyman));
            when(taskRepository.findById(100L)).thenReturn(Optional.of(mockTask));

            assertThrows(BidUnacceptableException.class, () ->
                    bidService.placeBid(req , "handy@man.com")
            );
        }
    }

    @Nested
    @DisplayName("acceptBid() Tests")
    class AcceptBidTests {
        @Test
        @DisplayName("Positive: Accepting a bid should update task and reject others")
        void acceptBid_Success() {
            // Set up a second bid that should be rejected
            Bid otherBid = new Bid();
            otherBid.setId(2L);
            otherBid.setStatus(BidStatus.PENDING);

            when(bidRepository.findById(1L)).thenReturn(Optional.of(mockBid));
            when(bidRepository.findByTaskAndStatus(mockTask, BidStatus.PENDING))
                    .thenReturn(Arrays.asList(mockBid, otherBid));

            Bid result = bidService.acceptBid(1L);

            // Assertions
            assertEquals(BidStatus.ACCEPTED, result.getStatus());
            assertEquals(BidStatus.REJECTED, otherBid.getStatus());
            assertEquals(TaskStatus.ASSIGNED, mockTask.getStatus());
            assertEquals(mockHandyman, mockTask.getAssignedHandyman());

            verify(bidRepository, atLeast(2)).save(any(Bid.class));
            verify(taskRepository).save(mockTask);
        }
    }

    @Nested
    @DisplayName("rejectBid() Tests")
    class RejectBidTests {
        @Test
        @DisplayName("Positive: Should update status to REJECTED")
        void rejectBid_Success() {
            when(bidRepository.findById(1L)).thenReturn(Optional.of(mockBid));
            when(bidRepository.save(any(Bid.class))).thenReturn(mockBid);

            Bid result = bidService.rejectBid(1L);

            assertEquals(BidStatus.REJECTED, result.getStatus());
            verify(bidRepository).save(mockBid);
        }
    }
}
