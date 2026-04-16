package com.meet.MeetSchedulling.service;

import com.meet.MeetSchedulling.dto.MeetingRequestDTO;
import com.meet.MeetSchedulling.entity.Meeting;
import com.meet.MeetSchedulling.entity.Users;
import com.meet.MeetSchedulling.entity.GoogleToken;
import com.meet.MeetSchedulling.repository.MeetingRepository;
import com.meet.MeetSchedulling.repository.UserRepository;
import com.meet.MeetSchedulling.repository.GoogleTokenRepository;
import com.google.api.services.calendar.model.Event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final EmailService emailService;
    private final GoogleCalendarService googleService;
    private final UserRepository userRepository;
    private final GoogleTokenRepository tokenRepository;

    public String scheduleMeeting(MeetingRequestDTO request, String email) throws Exception {

        // Create start & end time
        LocalDateTime start = LocalDateTime.of(
                request.getMeetingDate(),
                request.getStartTime()
        );

        LocalDateTime end = LocalDateTime.of(
                request.getMeetingDate(),
                request.getEndTime()
        );

        // Validations
        if (start.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot schedule meeting in past");
        }

        if (end.isBefore(start)) {
            throw new RuntimeException("Invalid time range");
        }

        boolean conflict = meetingRepository
                .existsByStartTimeLessThanAndEndTimeGreaterThan(end, start);

        if (conflict) {
            throw new RuntimeException("Slot already booked");
        }

        //  Fetch user & token
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GoogleToken token = tokenRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        String accessToken = token.getAccessToken();
        Event event;

        // ✅ Google event creation + refresh logic
        try {
            event = googleService.createGoogleEvent(request, accessToken);
        } catch (Exception e) {

            if (token.getRefreshToken() != null) {

                String newAccessToken = googleService.refreshAccessToken(token.getRefreshToken());

                token.setAccessToken(newAccessToken);
                tokenRepository.save(token);

                event = googleService.createGoogleEvent(request, newAccessToken);

            } else {
                throw new RuntimeException("Token expired and no refresh token available");
            }
        }

        String meetLink = event.getHangoutLink();

        // PROPER DATE FORMAT (THIS WILL WORK)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "dd MMM yyyy, hh:mm a",
                Locale.ENGLISH
        );

        String meetingTime = start.format(formatter);
        String meetingEndTime = end.format(formatter);

        //  Combine clean time range
        String fullTime = meetingTime + " - " + meetingEndTime;

        //  Send email to user
        emailService.sendMeetingEmail(
                request.getUserEmail(),
                meetLink,
                fullTime
        );

        emailService.sendMeetingEmail(
                googleService.getManagerMail(),
                meetLink,
                fullTime
        );

        //  Save meeting
        Meeting meeting = new Meeting();
        meeting.setTitle(request.getTitle());
        meeting.setStartTime(start);
        meeting.setEndTime(end);
        meeting.setMeetingLink(meetLink);
        meeting.setUserEmail(request.getUserEmail());
        meeting.setManagerEmail(googleService.getManagerMail());

        meetingRepository.save(meeting);

        return meetLink;
    }
}