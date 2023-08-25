package com.symptomaster.ws.usersession;


import com.sun.istack.internal.Nullable;
import com.symptomaster.ws.models.User;

/**
 * Created by nikolay on 01/02/2017.
 */
public interface UserSessionManager {
    String createUserSession(User user);

    boolean removeSession(String key);

    @Nullable
    UserSession getSession(String key);

    boolean isSessionExpired(String key);

    boolean isSessionValid(String key);

    void clearExpiredSessions();

    void clearSessions(int userId, String excludeSession);
}
