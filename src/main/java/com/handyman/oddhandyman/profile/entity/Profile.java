package com.handyman.oddhandyman.profile.entity;

import com.handyman.oddhandyman.auth.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Entity representing a handyman's profile in the system.
 */
@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the profile", example = "1")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "User associated with this profile")
    private User user;

    @Schema(description = "Indicates whether the profile has been verified by an admin", example = "true")
    private boolean verified = false;

    @Schema(description = "Indicates whether the handyman is available for tasks", example = "true")
    private boolean available = false;

    @Schema(description = "Handyman's phone number", example = "+27123456789")
    private String phoneNumber;

    @Schema(description = "Handyman's location or area of service", example = "Cape Town, South Africa")
    private String location;

    @Schema(description = "Overall rating of the handyman based on reviews", example = "4.5")
    private Double rating = 0.0;

    @ElementCollection
    @Schema(description = "List of skills possessed by the handyman", example = "[\"Plumbing\", \"Electrical\"]")
    private List<String> skills;

    @Schema(description = "Timestamp when the profile was created")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Schema(description = "Indicates whether the profile is complete", example = "true")
    private boolean profileComplete = false;

    @ElementCollection
    @CollectionTable(name = "profile_documents", joinColumns = @JoinColumn(name = "profile_id"))
    @MapKeyColumn(name = "document_type")
    @Column(name = "url")
    @Schema(description = "Map of document types to their URLs")
    private Map<String, String> documents = new HashMap<>();

    @Schema(description = "Cloudinary folder UUID where profile documents are stored")
    private String cloudinaryFolderUuid;

    @PrePersist
    public void prePersist() {
        if (cloudinaryFolderUuid == null) {
            cloudinaryFolderUuid = UUID.randomUUID().toString().replace("-", "");
        }
    }

    // Constructors
    public Profile() {}

    public Profile(User user) {
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
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
