package com.meet.MeetSchedulling.repository;

import com.meet.MeetSchedulling.entity.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);
}