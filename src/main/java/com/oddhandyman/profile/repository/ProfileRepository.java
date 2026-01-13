package com.oddhandyman.profile.repository;


import com.oddhandyman.auth.entity.User;
import com.oddhandyman.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    List<Profile> findByAvailableTrue();

    List<Profile> findByAvailableTrueAndSkillsContainingIgnoreCase(String skill);

    List<Profile> findByAvailableTrueAndUserNameContainingIgnoreCase(String name);

    List<Profile> findByAvailableTrueAndUserEmailContainingIgnoreCase(String email);

    Optional<Profile> findByUser(User user);

    Optional<Profile> findByUserId(Long userId);

}