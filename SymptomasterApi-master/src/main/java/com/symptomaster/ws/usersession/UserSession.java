package com.symptomaster.ws.usersession;

import java.util.Date;

public interface UserSession {
    String ATTR_USER_ID = "userId";

    void addAttribute(Object key, Object value);

    Object getAttribute(Object key);

    void updateLastUpdatedTime();

    Date getLastUpdatedTime();

    int getUserId();
}
