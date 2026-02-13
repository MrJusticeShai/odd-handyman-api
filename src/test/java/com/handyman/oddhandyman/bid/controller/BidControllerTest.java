package com.handyman.oddhandyman.bid.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.handyman.oddhandyman.bid.dto.BidRequest;
import com.handyman.oddhandyman.bid.entity.Bid;
import com.handyman.oddhandyman.bid.entity.enums.BidStatus;
import com.handyman.oddhandyman.bid.service.BidService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BidController.class)
class BidControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BidService bidService;

    @Autowired
    private ObjectMapper objectMapper;

    private Bid mockBid;
    private LocalDateTime createdAtTime;

    @BeforeEach
    void setUp() {
        createdAtTime = LocalDateTime.of(2026, 2, 10, 14, 30);

        mockBid = new Bid();
        mockBid.setId(1L);
        mockBid.setAmount(150.00);
        mockBid.setStatus(BidStatus.PENDING);
        mockBid.setCreatedAt(createdAtTime);
    }

    @Nested
    @DisplayName("POST /api/bids - Place Bid")
    class PlaceBidTests {

        @Test
        @WithMockUser(username = "handyman1@email.com")
        @DisplayName("Positive: Should place bid successfully")
        void placeBid_Success() throws Exception {
            BidRequest req = new BidRequest();
            req.setTaskId(1L);
            req.setAmount(150.0);

            when(bidService.placeBid(any(BidRequest.class), eq("handyman1@email.com"))).thenReturn(mockBid);

            mockMvc.perform(post("/api/bids")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(mockBid.getId()))
                    .andExpect(jsonPath("$.amount").value(mockBid.getAmount()))
                    .andExpect(jsonPath("$.status").value(mockBid.getStatus().toString()))
                    .andExpect(jsonPath("$.createdAt").exists());
        }

        @Test
        @WithMockUser
        @DisplayName("Negative: Should return 400 when amount is negative")
        void placeBid_NegativeAmount() throws Exception {
            BidRequest req = new BidRequest();
            req.setTaskId(1L);
            req.setAmount(-10.0);

            mockMvc.perform(post("/api/bids")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative: Should return 401 when unauthorized")
        void placeBid_Unauthorized() throws Exception {
            mockMvc.perform(post("/api/bids")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("PUT /api/bids/{bidId}/status")
    class StatusUpdateTests {

        @Test
        @WithMockUser
        @DisplayName("Positive: Should accept bid")
        void acceptBid_Success() throws Exception {
            when(bidService.acceptBid(1L)).thenReturn(mockBid);

            mockMvc.perform(put("/api/bids/1/accept").with(csrf()))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Negative: Should return 401 when unauthorized")
        void acceptBid_Unauthorized() throws Exception {

            mockMvc.perform(put("/api/bids/1/accept").with(csrf()))
                    .andExpect(status().isUnauthorized());
        }


        @Test
        @WithMockUser
        @DisplayName("Positive: Should reject bid")
        void rejectBid_Success() throws Exception {
            when(bidService.rejectBid(1L)).thenReturn(mockBid);

            mockMvc.perform(put("/api/bids/1/reject").with(csrf()))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Negative: Should return 401 when unauthorized")
        void rejectBid_Unauthorized() throws Exception {

            mockMvc.perform(put("/api/bids/1/reject").with(csrf()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /api/bids/task/{taskId}")
    class ListBidsTests {

        @Test
        @WithMockUser
        @DisplayName("Positive: Should return list of bids")
        void listBids_Success() throws Exception {
            when(bidService.listBidsForTask(1L)).thenReturn(Collections.singletonList(mockBid));

            mockMvc.perform(get("/api/bids/task/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1L));
        }
    }
}
