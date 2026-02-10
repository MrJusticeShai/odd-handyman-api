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

@RestController
@RequestMapping("/api/bids")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @Operation(summary = "Place a bid on a task", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<Bid> placeBid(@Valid @RequestBody BidRequest req, Authentication auth) {
        return ResponseEntity.ok(bidService.placeBid(req, auth.getName()));
    }

    @Operation(summary = "List bids for a task", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Bid>> listBids(@PathVariable Long taskId) {
        return ResponseEntity.ok(bidService.listBidsForTask(taskId));
    }

    @Operation(summary = "Accept a bid", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{bidId}/accept")
    public ResponseEntity<Bid> acceptBid(@PathVariable Long bidId) {
        return ResponseEntity.ok(bidService.acceptBid(bidId));
    }

    @Operation(summary = "Reject a bid", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{bidId}/reject")
    public ResponseEntity<Bid> rejectBid(@PathVariable Long bidId) {
        return ResponseEntity.ok(bidService.rejectBid(bidId));
    }
}
