package com.symptomaster.ws.controller;

import com.symptomaster.infra.enumeration.EnumLocale;
import com.symptomaster.infra.enumeration.EnumUserGender;
import com.symptomaster.infra.security.PasswordHasher;
import com.symptomaster.infra.utils.IV;
import com.symptomaster.ws.models.Transaction;
import com.symptomaster.ws.models.User;
import com.symptomaster.ws.models.WebServiceStatus;
import com.symptomaster.ws.service.ReEncryptionService;
import com.symptomaster.ws.service.SymptomsHistoryService;
import com.symptomaster.ws.service.UserService;
import com.symptomaster.ws.usersession.UserSession;
import com.symptomaster.ws.usersession.UserSessionManager;
import com.symptomaster.ws.utils.http.DefaultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@RestController
@Api(value = "User", description = "Operations with User")
@RequestMapping("/user")
public class UserController extends BaseRestController {
    @Autowired
    private UserSessionManager userSessionManager;
    @Autowired
    private UserService userService;

    @Autowired
    private ReEncryptionService reEncryptionService;

    @Autowired
    private SymptomsHistoryService symptomsHistoryService;

    @ApiOperation(value = "Update user password")
    @RequestMapping(value = "/updatepassword", method = RequestMethod.POST, produces = "application/json")
    public void updatePassword(HttpServletResponse response,
                      @RequestParam(value = "oldPassword") String oldPassword,
                      @RequestParam(value = "newPassword") String newPassword,
                      @RequestParam(value = "authtoken") String authToken) {
        DefaultResponse JSONresponse;

        UserSession userSession = userSessionManager.getSession(authToken);
        int userId = userSession.getUserId();

        try {
            if(IV.validatePassword(newPassword).isVerified()) {
                User user = userService.getUser(userId);

                if(user == null) {
                    throw new Exception("Failed to get User");
                }

                String oldPasswordHash = PasswordHasher.sha512(oldPassword, user.getSalt());

                if(oldPasswordHash.equals(user.getPassword())) {
                    String newSalt = PasswordHasher.generateSalt();
                    String newPasswordHash = PasswordHasher.sha512(newPassword, newSalt);
                    String newEncSalt = PasswordHasher.generateSalt();

                    if(userService.updateUserPassword(userId, newPasswordHash, newSalt, newEncSalt)) {
                        String newHistoryKey = PasswordHasher.sha512(newPassword, newEncSalt);
                        reEncryptionService.reEncryption(PasswordHasher.sha512(oldPassword, user.getEncryptionSalt()), userId, newHistoryKey);

                        userSessionManager.clearSessions(userId, authToken);
                        JSONresponse = new DefaultResponse(newHistoryKey);
                        renderJSON(response, JSONresponse.toJson(), HttpStatus.OK);
                    } else {
                        throw new Exception("Internal server error");
                    }
                } else {
                    throw new IllegalAccessException("Incorrect old password");
                }
            } else {
                throw new IllegalArgumentException("Invalid new password");
            }
        } catch (Exception e) {
            renderError(response, e);
        }
    }


    // TODO: research binding complex objects in RequestParam
    @ApiOperation(value = "New user registration")
    @RequestMapping(value = "/signup", method = RequestMethod.POST, produces = "application/json")
    public void signUp(HttpServletResponse response,
                      @RequestParam(value = "firstname") String firstName,
                      @RequestParam(value = "middlename", defaultValue = "") String middleName,
                      @RequestParam(value = "lastname") String lastName,
                      @RequestParam(value = "gender") EnumUserGender gender,
                      @RequestParam(value = "dob") long dobL,
                      @RequestParam(value = "email") String email,
                      @RequestParam(value = "password") String passwordPlain,
                      @RequestParam(value = "locale", defaultValue = "RUS") EnumLocale locale) {
        DefaultResponse JSONresponse;

        try {
            User newUser = User.Mapper.map(firstName, middleName, lastName, gender, dobL, email, passwordPlain);
            WebServiceStatus webServiceStatus = userService.signup(newUser, locale);
            JSONresponse = new DefaultResponse(webServiceStatus);
            renderJSON(response, JSONresponse.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            renderError(response, e);
        }
    }


    @ApiOperation(value = "User profile update")
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json")
    public void updateProfile(HttpServletResponse response,
                      @RequestParam(value = "firstname") String firstName,
                      @RequestParam(value = "middlename", defaultValue = "") String middleName,
                      @RequestParam(value = "lastname") String lastName,
                      @RequestParam(value = "gender") EnumUserGender gender,
                      @RequestParam(value = "dob") long dobL,
                      @RequestParam(value = "authtoken") String authToken) {
        DefaultResponse JSONresponse;

        UserSession userSession = userSessionManager.getSession(authToken);

        if (userSession != null) {
            int userId = userSession.getUserId();
            User updatedUser = User.Mapper.map(userId, firstName, middleName, lastName, gender, dobL);
            WebServiceStatus webServiceStatus = userService.updateProfile(updatedUser);

            try {
                JSONresponse = new DefaultResponse(webServiceStatus);
                renderJSON(response, JSONresponse.toJson(), HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                renderError(response, e);
            }
        }
    }


    @ApiOperation(value = "Get user profile")
    @RequestMapping(value = "/profile", method = RequestMethod.GET, produces = "application/json")
    public void getProfile(HttpServletResponse response,
                      @RequestParam(value = "authtoken") String authToken) {
        DefaultResponse JSONresponse;
        UserSession userSession = userSessionManager.getSession(authToken);

        if (userSession != null) {
            int userId = userSession.getUserId();
            User user = userService.getProfile(userId);

            try {
                JSONresponse = new DefaultResponse(user);
                renderJSON(response, JSONresponse.toJson(), HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                renderError(response, e);
            }
        }
    }

    @ApiOperation(value = "Send forgotPasswordCode to email")
    @RequestMapping(value = "/forgot", method = RequestMethod.POST, produces = "application/json")
    public void forgot(HttpServletResponse response,
                       @RequestParam(value = "email") String email) {
        DefaultResponse JSONresponse;

        try {
            User user = userService.getUser(email);

            if(user == null) {
                throw new NullPointerException("Email not found");
            }

            String code = userService.createForgotPasswordCode(user.getId());

            if(code == null) {
                throw new Exception("Internal server error");
            }

            userService.sendForgotPasswordCode(user.getId(), code);

            JSONresponse = new DefaultResponse("");
            renderJSON(response, JSONresponse.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            renderError(response, e);
        }
    }

    @ApiOperation(value = "Validate forgotPasswordCode")
    @RequestMapping(value = "/forgot/validate-code", method = RequestMethod.POST, produces = "application/json")
    public void validatePasswordCode(HttpServletResponse response,
                                     @RequestParam(value = "code") String code) {
        DefaultResponse JSONresponse;

        try {
            int id = userService.getUserIdByPasswordCode(code);

            JSONresponse = new DefaultResponse(userService.validateForgotPasswordCode(id, code));
            renderJSON(response, JSONresponse.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            renderError(response, e);
        }
    }

    @ApiOperation(value = "Restore password")
    @RequestMapping(value = "/forgot/new-password", method = RequestMethod.POST, produces = "application/json")
    public void newPassword(HttpServletResponse response,
                            @RequestParam(value = "code") String code,
                            @RequestParam("newPassword") String newPassword) {
        DefaultResponse JSONresponse;

        try {
            int id = userService.getUserIdByPasswordCode(code);

            if(id > 0) {
                User user = userService.getUser(id);

                if(user != null) {
                    if(userService.validateForgotPasswordCode(id, code) && IV.validatePassword(newPassword).isVerified()) {
                        String salt = PasswordHasher.generateSalt();
                        String encSalt = PasswordHasher.generateSalt();
                        userService.updateUserPassword(id, PasswordHasher.sha512(newPassword, salt), salt, encSalt);
                        userService.clearForgotPasswordCode(id);
                        userSessionManager.clearSessions(id, null);
                        symptomsHistoryService.disableSymptomsHistoryForUser(user.getId());

                        JSONresponse = new DefaultResponse("");
                        renderJSON(response, JSONresponse.toJson(), HttpStatus.OK);
                        return;
                    } else {
                        throw new IllegalArgumentException("Invalid new password or passwordCode");
                    }
                }
            }

            throw new Exception("Internal server error");
        } catch (Exception e) {
            renderError(response, e);
        }
    }


    @ApiOperation(value = "Get user profile")
    @RequestMapping(value = "/history", method = RequestMethod.GET, produces = "application/json")
    public void getProfile(HttpServletResponse response,
                           @RequestParam(value = "authtoken") String authToken,
                           @RequestParam(value = "historykey") String historykey,
                           @RequestParam(value = "locale", defaultValue = "RUS") EnumLocale locale) {
        DefaultResponse JSONresponse;

        int userId = userSessionManager.getSession(authToken).getUserId();

        try {
            List<Transaction> transactionList = userService.getTransactionList(userId, historykey, locale);
            JSONresponse = new DefaultResponse(transactionList);
            renderJSON(response, JSONresponse.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            renderError(response, e);
        }
    }
}
