package com.meet.MeetSchedulling.repository;

import com.meet.MeetSchedulling.entity.GoogleToken;
import com.meet.MeetSchedulling.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleTokenRepository extends JpaRepository<GoogleToken, Long> {

    Optional<GoogleToken> findByUser(Users user);
}