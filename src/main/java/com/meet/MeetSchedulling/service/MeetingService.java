package com.meet.MeetSchedulling.service;

import com.meet.MeetSchedulling.dto.MeetingRequestDTO;
import com.meet.MeetSchedulling.entity.Meeting;
import com.meet.MeetSchedulling.entity.Users;
import com.meet.MeetSchedulling.entity.GoogleToken;
import com.meet.MeetSchedulling.exception.SameDateTimeException;
import com.meet.MeetSchedulling.repository.MeetingRepository;
import com.meet.MeetSchedulling.repository.UserRepository;
import com.meet.MeetSchedulling.repository.GoogleTokenRepository;
import com.google.api.services.calendar.model.Event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final EmailService emailService;
    private final GoogleCalendarService googleService;
    private final UserRepository userRepository;
    private final GoogleTokenRepository tokenRepository;

    public String scheduleMeeting(MeetingRequestDTO request, String email) throws Exception {

        // 1. Create start & end time
        LocalDateTime start = LocalDateTime.of(
                request.getMeetingDate(),
                request.getStartTime()
        );

        LocalDateTime end = LocalDateTime.of(
                request.getMeetingDate(),
                request.getEndTime()
        );

        // 2. Validations
        if (start.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot schedule meeting in past");
        }

        if (end.isBefore(start)) {
            throw new RuntimeException("Invalid time range");
        }

        boolean conflict = meetingRepository
                .existsByStartTimeLessThanAndEndTimeGreaterThan(end, start);

        if (conflict) {
            throw new SameDateTimeException("Slot already booked");
        }

        // 3. Fetch user & token
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GoogleToken token = tokenRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        String accessToken = token.getAccessToken();
        Event event;

        // 4. Google event creation + refresh logic
        try {
            event = googleService.createGoogleEvent(request, accessToken);
        } catch (Exception e) {

            log.warn("Access token expired, trying refresh...");

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

        // 5. Format time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "dd MMM yyyy, hh:mm a",
                Locale.ENGLISH
        );

        String meetingStart = start.format(formatter);
        String meetingEnd = end.format(formatter);
        String fullTime = meetingStart + " - " + meetingEnd;

        // 6. Send ONE email (user + manager inside EmailService)
        emailService.sendMeetingEmail(
                request.getUserEmail(),
                meetLink,
                fullTime
        );

        // 7. Save meeting
        Meeting meeting = new Meeting();
        meeting.setTitle(request.getTitle());
        meeting.setStartTime(start);
        meeting.setEndTime(end);
        meeting.setMeetingLink(meetLink);
        meeting.setUserEmail(request.getUserEmail());
        meeting.setManagerEmail(googleService.getManagerMail());

        meetingRepository.save(meeting);

        log.info("Meeting scheduled successfully for {}", request.getUserEmail());

        return meetLink;
    }
}
