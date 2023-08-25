package com.symptomaster.ws.interceptors;

import com.symptomaster.ws.usersession.UserSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by nikolay on 02/02/2017.
 */
@Component
public class UserAccessInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private UserSessionManager userSessionManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        JsonRenderer jsonRenderer = new JsonRenderer();
        String authToken = request.getParameter("authtoken");

        if(authToken != null) {
            if(userSessionManager.isSessionValid(authToken)) {
                return true;
            }
        }

        jsonRenderer.renderError(response, new IllegalAccessException("User is unauthorized"));
        return false;
    }
}
