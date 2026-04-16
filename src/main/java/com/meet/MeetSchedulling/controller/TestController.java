package com.meet.MeetSchedulling.controller;

import com.meet.MeetSchedulling.dto.MeetingRequestDTO;
import com.meet.MeetSchedulling.service.MeetingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
public class TestController {

    @Autowired
    private MeetingService meetingService;


@GetMapping("/test-meeting")
public String testMeeting(OAuth2AuthenticationToken authentication) throws Exception {

    if (authentication == null) {
        throw new RuntimeException("User not authenticated. Please login first.");
    }
    String email = authentication.getPrincipal().getAttribute("email");

    System.out.println("LOGIN EMAIL: " + email);

    MeetingRequestDTO req = new MeetingRequestDTO();
    req.setTitle("Test Meeting");
    req.setMeetingDate(LocalDate.now().plusDays(2));
    req.setStartTime(LocalTime.of(17, 0));
    req.setEndTime(LocalTime.of(18, 0));
    req.setUserEmail("santoshmaur9958@gmail.com"); // better
    // req.setManagerEmail("lokeshkr189@gmail.com");


    return meetingService.scheduleMeeting(req, email);
}
}