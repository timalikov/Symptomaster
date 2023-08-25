package com.symptomaster.ws.usersession;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nikolay on 01/02/2017.
 */
public class UserSessionImpl implements UserSession {
    private Map<Object, Object> attributes = new ConcurrentHashMap<>();
    private Date lastUpdatedTime;

    public UserSessionImpl() {
        updateLastUpdatedTime();
    }

    @Override
    public void addAttribute(Object key, Object value) {
        attributes.put(key, value);
    }

    @Override
    public Object getAttribute(Object key) {
        return attributes.get(key);
    }

    @Override
    public void updateLastUpdatedTime() {
        lastUpdatedTime = new Date(System.currentTimeMillis());
    }

    @Override
    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    @Override
    public int getUserId() {
        int userId = (int) this.getAttribute(ATTR_USER_ID);

        return userId;
    }
}
