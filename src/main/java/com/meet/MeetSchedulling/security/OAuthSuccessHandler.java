package com.meet.MeetSchedulling.security;

import com.meet.MeetSchedulling.entity.GoogleToken;
import com.meet.MeetSchedulling.entity.Users;
import com.meet.MeetSchedulling.repository.GoogleTokenRepository;
import com.meet.MeetSchedulling.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OAuth2AuthorizedClientService clientService;

    @Autowired
    private GoogleTokenRepository tokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;

        String email = authToken.getPrincipal().getAttribute("email");
        String name = authToken.getPrincipal().getAttribute("name");

        Users user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    Users newUser = new Users();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    return userRepository.save(newUser);
                });

        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                authToken.getAuthorizedClientRegistrationId(),
                authToken.getName()
        );

        String accessToken = client.getAccessToken().getTokenValue();
        String refreshToken = client.getRefreshToken() != null
                ? client.getRefreshToken().getTokenValue()
                : null;

        GoogleToken token = tokenRepository.findByUser(user)
                .orElse(new GoogleToken());

        token.setUser(user);
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);

        tokenRepository.save(token);

        response.sendRedirect("/user");
    }
}