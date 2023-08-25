package com.symptomaster.ws.service.impl;

import com.symptomaster.infra.security.PasswordHasher;
import com.symptomaster.ws.models.User;
import com.symptomaster.ws.models.LoginTokens;
import com.symptomaster.ws.service.AuthService;
import com.symptomaster.ws.service.UserService;
import com.symptomaster.ws.usersession.UserSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by dkarmazi on 1/22/17.
 */
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private UserSessionManager userSessionManager;

    @Override
    public boolean authentication(String email, String password) {
        User user = userService.getUser(email);

        if(user != null) {
            String currentPasswordHash = PasswordHasher.sha512(password, user.getSalt());
            return user.getPassword().equals(currentPasswordHash);
        }

        return false;
    }

    @Override
    public boolean isEmailConfirmed(String username) {
        return userService.getEmailConfirmHash(username) == null;
    }

    @Override
    public LoginTokens login(String email, String password) {
        User user = userService.getUser(email);

        if(user != null && user.getEncryptionSalt() != null) {
            String historyKey = PasswordHasher.sha512(password, user.getEncryptionSalt());

            LoginTokens loginTokens = new LoginTokens();
            loginTokens.setAuthToken(userSessionManager.createUserSession(user));
            loginTokens.setHistoryKey(historyKey);

            return loginTokens;
        }

        return null;
    }
}
