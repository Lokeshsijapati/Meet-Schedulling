package com.meet.MeetSchedulling.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.meet.MeetSchedulling.dto.MeetingRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import lombok.Getter;
import lombok.Setter;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@Getter
@Setter 
public class GoogleCalendarService {

    @Value("${manager.email}")
    private String managerMail;

    public Event createGoogleEvent(MeetingRequestDTO request, String accessToken) throws Exception {

        GoogleCredential credential = new GoogleCredential()
                .setAccessToken(accessToken);

        Calendar service = new Calendar.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("Meet Scheduler")
                .build();

        LocalDateTime startDateTime = LocalDateTime.of(
                request.getMeetingDate(),
                request.getStartTime());

        LocalDateTime endDateTime = LocalDateTime.of(
                request.getMeetingDate(),
                request.getEndTime());

        DateTime start = new DateTime(startDateTime.toString() + ":00+05:30");
        DateTime end = new DateTime(endDateTime.toString() + ":00+05:30");

        Event event = new Event()
                .setSummary(request.getTitle())
                .setDescription("Scheduled via Meet Scheduler");

        event.setStart(new EventDateTime().setDateTime(start));
        event.setEnd(new EventDateTime().setDateTime(end));

        EventAttendee user = new EventAttendee().setEmail(request.getUserEmail());
        EventAttendee manager = new EventAttendee().setEmail(managerMail);

        event.setAttendees(Arrays.asList(user, manager));

        ConferenceData conferenceData = new ConferenceData();
        CreateConferenceRequest confReq = new CreateConferenceRequest();
        confReq.setRequestId("meet-" + System.currentTimeMillis());

        conferenceData.setCreateRequest(confReq);
        event.setConferenceData(conferenceData);

        return service.events()
                .insert("primary", event)
                .setConferenceDataVersion(1)
                .execute();
    }


        public String refreshAccessToken(String refreshToken) throws Exception {

                String url = "https://oauth2.googleapis.com/token";

                String params = "" +
                                "" +
                                "&refresh_token=" + refreshToken +
                                "&grant_type=refresh_token";

                java.net.URL obj = new java.net.URL(url);
                java.net.HttpURLConnection con = (java.net.HttpURLConnection) obj.openConnection();

                con.setRequestMethod("POST");
                con.setDoOutput(true);

                java.io.OutputStream os = con.getOutputStream();
                os.write(params.getBytes());
                os.flush();
                os.close();

                java.io.BufferedReader in = new java.io.BufferedReader(
                                new java.io.InputStreamReader(con.getInputStream()));

                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                }
                in.close();

                // extract access_token manually
                String res = response.toString();

                String accessToken = res.split("\"access_token\":\"")[1].split("\"")[0];

                return accessToken;
        }
}
