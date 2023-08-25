package com.symptomaster.ws.controller;

import com.symptomaster.ws.exceptions.AuthenticationException;
import com.symptomaster.ws.models.LoginTokens;
import com.symptomaster.ws.models.User;
import com.symptomaster.ws.service.AuthService;
import com.symptomaster.ws.service.UserService;
import com.symptomaster.ws.usersession.UserSessionManager;
import com.symptomaster.ws.utils.http.DefaultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@Api(value = "Auth", description = "Operations with Auth")
@RequestMapping("/auth")
public class AuthController extends BaseRestController {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserSessionManager userSessionManager;

    @ApiOperation(value = "Authenticate user")
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
    public void login(HttpServletResponse response,
                      @RequestParam(value = "username") String username,
                      @RequestParam(value = "password") String password) {
        DefaultResponse JSONresponse;

        try {
            if(!authService.authentication(username, password)) {
                throw new AuthenticationException("Authentication failed");
            }

            if(!authService.isEmailConfirmed(username)) {
                throw new IllegalAccessException("Unconfirmed email");
            }

            LoginTokens loginTokens = authService.login(username, password);
            JSONresponse = new DefaultResponse(loginTokens);

            User user = userService.getUser(username);

            if(user != null)
                userService.clearForgotPasswordCode(user.getId());

            renderJSON(response, JSONresponse.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            renderError(response, e);
        }
    }
}
