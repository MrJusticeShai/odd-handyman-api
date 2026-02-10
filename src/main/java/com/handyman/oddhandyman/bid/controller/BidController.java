package com.handyman.oddhandyman.bid.controller;

import com.handyman.oddhandyman.bid.dto.BidRequest;
import com.handyman.oddhandyman.bid.entity.Bid;
import com.handyman.oddhandyman.bid.service.BidService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing bids in the system.
 * <p>
 * Provides endpoints for placing bids, listing bids for a task, and accepting or rejecting bids.
 * All endpoints require authentication (JWT Bearer token) and use the {@link BidService} for business logic.
 */
@RestController
@RequestMapping("/api/bids")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @Operation(summary = "Place a bid on a task",
            description = "Authenticated handyman can place a bid on a given task. The bid is created with status PENDING.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public ResponseEntity<Bid> placeBid(@Valid @RequestBody BidRequest req, Authentication auth) {
        return ResponseEntity.ok(bidService.placeBid(req, auth.getName()));
    }

    @Operation(summary = "List bids for a task",
            description = "Returns all bids placed on the specified task. Authentication required.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Bid>> listBids(@PathVariable Long taskId) {
        return ResponseEntity.ok(bidService.listBidsForTask(taskId));
    }

    @Operation(summary = "Accept a bid",
            description = "Accepts a specific bid. Only authenticated users can perform this action.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{bidId}/accept")
    public ResponseEntity<Bid> acceptBid(@PathVariable Long bidId) {
        return ResponseEntity.ok(bidService.acceptBid(bidId));
    }

    @Operation(summary = "Reject a bid",
            description = "Rejects a specific bid. Only authenticated users can perform this action.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{bidId}/reject")
    public ResponseEntity<Bid> rejectBid(@PathVariable Long bidId) {
        return ResponseEntity.ok(bidService.rejectBid(bidId));
    }
}
