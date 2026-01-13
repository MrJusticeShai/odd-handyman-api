package com.oddhandyman.bid.service;

import com.oddhandyman.bid.dto.BidRequest;
import com.oddhandyman.bid.entity.Bid;

import java.util.List;

public interface BidService {

    Bid placeBid(BidRequest req, String handymanEmail);

    List<Bid> listBidsForTask(Long taskId);

    Bid acceptBid(Long bidId);

    Bid rejectBid(Long bidId);
}
