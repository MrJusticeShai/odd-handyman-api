package com.oddhandyman.chat.service;

import com.oddhandyman.auth.entity.Role;
import com.oddhandyman.chat.dto.ChatMessageRequest;
import com.oddhandyman.chat.entity.ChatMessage;
import java.util.List;
import java.util.Map;

public interface ChatService {

    ChatMessage sendMessage(ChatMessageRequest req, String senderEmail);

    List<ChatMessage> getMessagesForTask(Long taskId);

    void markMessagesAsRead(Long taskId, String userEmail);

    Map<Long, Long> getUnreadCounts(String email, Role role);

}
