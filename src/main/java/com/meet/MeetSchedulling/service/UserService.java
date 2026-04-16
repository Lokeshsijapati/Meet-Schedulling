package com.meet.MeetSchedulling.service;
import com.meet.MeetSchedulling.entity.Users;
import com.meet.MeetSchedulling.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Users saveOrUpdateUser(OAuth2User principal) {

        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");

        Optional<Users> existingUser = userRepository.findByEmail(email);

        Users user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            user = new Users();
        }

        user.setEmail(email);
        user.setName(name);

        return userRepository.save(user);
    }
}