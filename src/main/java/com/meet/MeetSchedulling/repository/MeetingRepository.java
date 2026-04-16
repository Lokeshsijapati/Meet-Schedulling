package com.meet.MeetSchedulling.repository;

import com.meet.MeetSchedulling.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    boolean existsByStartTimeLessThanAndEndTimeGreaterThan(
            LocalDateTime endTime,
            LocalDateTime startTime
    );
}