package com.be.auth.service;

import com.be.auth.dto.AuthDtos.*;
import com.be.user.domain.Role;
import com.be.user.domain.User;
import com.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(SignupReq req) {
        String username = normalizeUsername(req.username());
        validatePassword(req.password());

        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "username already taken");
        }

        User user = User.of(username, passwordEncoder.encode(req.password()), Role.USER);
        userRepository.save(user);
    }

    public User authenticate(LoginReq req) {
        String username = normalizeUsername(req.username());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid credentials");
        }

        return user;
    }

    private String normalizeUsername(String username) {
        if (username == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username required");
        String u = username.trim();
        if (u.length() < 3 || u.length() > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username length 3~30");
        }
        return u;
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password length >= 8");
        }
    }
}