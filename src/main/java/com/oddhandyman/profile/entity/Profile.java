package com.oddhandyman.profile.entity;

import com.oddhandyman.auth.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private boolean verified = false; // Admin verification

    private boolean available = false; // Appears in search

    private String phoneNumber;

    private String location;

    private Double rating = 0.0; // Overall rating

    @ElementCollection
    private List<String> skills;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Profile completeness
    private boolean profileComplete = false;

    // Map to store document URLs by type
    @ElementCollection
    @CollectionTable(name = "profile_documents", joinColumns = @JoinColumn(name = "profile_id"))
    @MapKeyColumn(name = "document_type")
    @Column(name = "url")
    private Map<String, String> documents = new HashMap<>();

    private String cloudinaryFolderUuid;

    @PrePersist
    public void prePersist() {
        if (cloudinaryFolderUuid == null) {
            cloudinaryFolderUuid = UUID.randomUUID().toString().replace("-", "");
        }
    }

    // Constructors, getters, setters

    public Profile() {}

    public Profile(User user) {
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isProfileComplete() {
        return profileComplete;
    }

    public void setProfileComplete(boolean profileComplete) {
        this.profileComplete = profileComplete;
    }

    public Map<String, String> getDocuments() {
        return documents;
    }

    public void setDocuments(Map<String, String> documents) {
        this.documents = documents;
    }

    public String getCloudinaryFolderUuid() {
        return cloudinaryFolderUuid;
    }

    public void setCloudinaryFolderUuid(String cloudinaryFolderUuid) {
        this.cloudinaryFolderUuid = cloudinaryFolderUuid;
    }
}