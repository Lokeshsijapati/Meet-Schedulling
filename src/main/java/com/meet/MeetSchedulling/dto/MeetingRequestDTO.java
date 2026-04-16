package com.meet.MeetSchedulling.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class MeetingRequestDTO {

    private String title;

    private LocalDate meetingDate;

    private LocalTime startTime;
    private LocalTime endTime;

    private String userEmail;
    
    // private String managerEmail;
}