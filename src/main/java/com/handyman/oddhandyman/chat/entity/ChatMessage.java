package com.handyman.oddhandyman.chat.entity;

import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.task.entity.Task;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Task task;

    @ManyToOne
    private User sender;

    @Column(length = 1000)
    private String message;

    private LocalDateTime timestamp = LocalDateTime.now();

    private boolean readByCustomer = false;

    private boolean readByHandyman = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isReadByCustomer() {
        return readByCustomer;
    }

    public void setReadByCustomer(boolean readByCustomer) {
        this.readByCustomer = readByCustomer;
    }

    public boolean isReadByHandyman() {
        return readByHandyman;
    }

    public void setReadByHandyman(boolean readByHandyman) {
        this.readByHandyman = readByHandyman;
    }
}
