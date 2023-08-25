package com.symptomaster.ws.service;

import com.symptomaster.infra.enumeration.EnumLocale;
import com.symptomaster.ws.models.User;
import com.symptomaster.ws.models.LoginTokens;
import com.symptomaster.ws.models.WebServiceStatus;

public interface AuthService {
    boolean authentication(String username, String password);
    boolean isEmailConfirmed(String username);
    LoginTokens login(String username, String password);
}
