package com.be.global.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class SessionAuth {

    private static final String SESSION_USER_ID = "USER_ID";

    public Long requireUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        Object idObj = session.getAttribute(SESSION_USER_ID);
        if (!(idObj instanceof Long userId)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return userId;
    }
}