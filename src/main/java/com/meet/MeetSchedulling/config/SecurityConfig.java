package com.meet.MeetSchedulling.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.meet.MeetSchedulling.security.OAuthSuccessHandler;
@Configuration
public class SecurityConfig {

    @Autowired
    private OAuthSuccessHandler successHandler;

   @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) // 🔥 important
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/login**").permitAll()
            .requestMatchers("/api/**").authenticated() // 🔥 secure karo
            .anyRequest().authenticated()
        )
        .oauth2Login(oauth -> oauth
            .successHandler(successHandler)
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .deleteCookies("JSESSIONID")
        );

    return http.build();
}
}