package com.meet.MeetSchedulling.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meet.MeetSchedulling.entity.GoogleToken;
import com.meet.MeetSchedulling.entity.Users;
import com.meet.MeetSchedulling.repository.GoogleTokenRepository;
import com.meet.MeetSchedulling.repository.UserRepository;

@RestController
public class UserController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoogleTokenRepository tokenRepository;


    @GetMapping("/user")
    public Object user(@AuthenticationPrincipal OAuth2User principal, OAuth2AuthenticationToken authentication) {
        if (principal == null) {
            return "NOT AUTHENTICATED";
        }
            String email = authentication.getPrincipal().getAttribute("email");
          Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GoogleToken token = tokenRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        String accessToken = token.getAccessToken();
        System.out.println(accessToken);
        return principal.getAttributes();
    }
}