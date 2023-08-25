package com.symptomaster.ws.usersession;

import com.symptomaster.infra.security.PasswordHasher;
import com.symptomaster.ws.models.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nikolay on 01/02/2017.
 */
@Service
public class UserSessionManagerImpl implements UserSessionManager {
    private Map<String, UserSession> sessions = new ConcurrentHashMap<>();

    @Override
    public String createUserSession(User user) {
        String userToken = PasswordHasher.generateUserToken();

        if(sessions.containsKey(userToken)) {
            userToken = createUserSession(user);
        } else {
            UserSession userSession = new UserSessionImpl();
            sessions.put(userToken, userSession);
            userSession.addAttribute(UserSession.ATTR_USER_ID, user.getId());
        }

        return userToken;
    }

    @Override
    public boolean removeSession(String key) {
        return sessions.remove(key) != null;
    }

    @Override
    public UserSession getSession(String key) {
        return sessions.get(key);
    }

    @Override
    public boolean isSessionExpired(String key) {
        UserSession session = getSession(key);

        if(session != null) {
            long currentTime = System.currentTimeMillis();
            long sessionTime = session.getLastUpdatedTime().getTime();

            long diff = ((currentTime - sessionTime) / 1000) / 60;

            //TODO: move to config
            return diff > 30;
        }

        return true;
    }

    @Override
    public boolean isSessionValid(String key) {
        UserSession userSession = sessions.get(key);

        if(userSession != null && !isSessionExpired(key)) {
            userSession.updateLastUpdatedTime();
            return true;
        }

        return false;
    }

    @Override
    public synchronized void clearExpiredSessions() {
        for(Map.Entry<String, UserSession> entry : sessions.entrySet()) {
            if(isSessionExpired(entry.getKey())) {
                removeSession(entry.getKey());
            }
        }
    }

    @Override
    public void clearSessions(int userId, String excludeSession) {
        for(Map.Entry<String, UserSession> entry : sessions.entrySet()) {
            UserSession userSession = getSession(entry.getKey());

            if((userSession.getUserId() == userId && !entry.getKey().equals(excludeSession)) || excludeSession == null) {
                removeSession(entry.getKey());
            }
        }
    }
}
