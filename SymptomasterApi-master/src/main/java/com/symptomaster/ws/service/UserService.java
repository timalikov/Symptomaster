package com.symptomaster.ws.service;

import com.symptomaster.infra.enumeration.EnumLocale;
import com.symptomaster.ws.models.Transaction;
import com.symptomaster.ws.models.User;
import com.symptomaster.ws.models.WebServiceStatus;

import java.util.List;

/**
 * Created by nikolay on 31/01/2017.
 */
public interface UserService {
    User getUser(String email);
    User getUser(int id);

    boolean updateUserPassword(int userId, String newPasswordHash, String newSalt, String newEncryptionSalt);

    boolean isEmailAlreadyExists(String email);

    WebServiceStatus signup(User newUser, EnumLocale locale);

    WebServiceStatus updateProfile(User updatedUser);

    User getProfile(int userId);

    List<Transaction> getTransactionList(int userId, String historyKey, EnumLocale locale);

    String createForgotPasswordCode(int userId);

    String getEmailConfirmHash(String email);

    boolean validateForgotPasswordCode(int userId, String code);

    boolean sendForgotPasswordCode(int userId, String code);

    void clearForgotPasswordCode(int userId);

    int getUserIdByPasswordCode(String code);
}
