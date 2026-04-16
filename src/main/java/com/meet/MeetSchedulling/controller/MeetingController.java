package com.meet.MeetSchedulling.controller;

import com.meet.MeetSchedulling.dto.MeetingRequestDTO;
import com.meet.MeetSchedulling.service.MeetingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @PostMapping("/create-meeting")
    public ResponseEntity<?> createMeeting(
            OAuth2AuthenticationToken authentication,
            @RequestBody MeetingRequestDTO request) throws Exception {

        if (authentication == null) {
            return ResponseEntity
                    .status(401)
                    .body("User not authenticated");
        }

        String email = authentication.getPrincipal().getAttribute("email");

        if (request.getTitle() == null || request.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().body("Title is required");
        }

        String meetLink = meetingService.scheduleMeeting(request, email);

        return ResponseEntity.ok().body(
                "Meeting created successfully: " + meetLink);
    }
}