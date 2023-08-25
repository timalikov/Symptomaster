package com.symptomaster.ws.service.impl;

import com.symptomaster.infra.constants.C;
import com.symptomaster.infra.enumeration.EnumLocale;
import com.symptomaster.infra.security.PasswordHasher;
import com.symptomaster.infra.utils.EmailExchanger;
import com.symptomaster.ws.constants.LabelsTemp;
import com.symptomaster.ws.models.Transaction;
import com.symptomaster.ws.models.TransactionEntry;
import com.symptomaster.ws.models.User;
import com.symptomaster.ws.models.WebServiceStatus;
import com.symptomaster.ws.service.SymptomService;
import com.symptomaster.ws.service.UserService;
import com.symptomaster.ws.service.jdbc.mapper.TransactionEntryMapper;
import com.symptomaster.ws.service.jdbc.mapper.UserMapper;
import com.symptomaster.ws.service.jdbc.mapper.UserProfileMapper;
import com.symptomaster.ws.utils.StaticValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by nikolay on 31/01/2017.
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SymptomService symptomService;

    @Override
    public User getUser(String email) {
        StringBuilder query = new StringBuilder("SELECT _id,")
                .append("email,")
                .append("password,")
                .append("salt,")
                .append("encryption_salt,")
                .append("firstname,")
                .append("middlename,")
                .append("lastname,")
                .append("gender,")
                .append("dob FROM users WHERE email = ?");

        try {
            User user = jdbcTemplate.queryForObject(query.toString(), new Object[] {email}, new UserMapper());
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getUser(int id) {
        StringBuilder query = new StringBuilder("SELECT _id,")
                .append("email,")
                .append("password,")
                .append("salt,")
                .append("encryption_salt,")
                .append("firstname,")
                .append("middlename,")
                .append("lastname,")
                .append("gender,")
                .append("dob FROM users WHERE _id = ?");

        try {
            User user = jdbcTemplate.queryForObject(query.toString(), new Object[] {id}, new UserMapper());
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateUserPassword(int userId, String newPasswordHash, String newSalt, String newEncryptionSalt) {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE users SET ");
        query.append("password = ?, salt = ?, encryption_salt = ? WHERE _id = ? ");

        try {
            jdbcTemplate.update(query.toString(), new Object[] {newPasswordHash, newSalt, newEncryptionSalt, userId});
            return true;
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean isEmailAlreadyExists(String email) {
        StringBuilder query = new StringBuilder().append("SELECT email ")
                .append("FROM users ")
                .append("WHERE email = ?");

        String dbEmail = null;

        try {
            dbEmail = jdbcTemplate.queryForObject(query.toString(), new Object[]{email}, String.class);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
        }

        return email.equals(dbEmail);
    }

    @Override
    public WebServiceStatus signup(User newUser, EnumLocale locale) {
        WebServiceStatus webServiceStatus = WebServiceStatus.getInstanceError();

        if(isEmailAlreadyExists(newUser.getEmail())) {
            return WebServiceStatus.getInstanceEmailAlreadyExists();
        }

        // insert
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO users ");
        query.append("(email, password, salt, encryption_salt, firstname, middlename, lastname, gender, dob, external_id, confirm_email_hash) ");
        query.append("VALUES (?,?,?,?,?,?,?,?,?,?,?)");
        int rowsAffected = jdbcTemplate.update(query.toString(), User.Mapper.mapToObjectArrayForInsert(newUser));

        if(rowsAffected == 1) {
            sendConfirmMessage(locale, newUser.getEmail(), newUser.getConfirmEmailHash());
            webServiceStatus = WebServiceStatus.getInstanceSuccess();
        }

        return webServiceStatus;
    }

    @Override
    public WebServiceStatus updateProfile(User updatedUser) {
        WebServiceStatus webServiceStatus = WebServiceStatus.getInstanceError();

        // update
        StringBuilder query = new StringBuilder();
        query.append("UPDATE users ");
        query.append("SET firstname = ?, middlename = ?, lastname = ?, gender = ?, dob = ? ");
        query.append("WHERE _id = ?");

        if(jdbcTemplate.update(query.toString(), User.Mapper.mapToObjectArrayForUpdate(updatedUser)) == 1) {
            webServiceStatus = WebServiceStatus.getInstanceSuccess();
        }

        return webServiceStatus;
    }

    @Override
    public User getProfile(int userId) {
        StringBuilder query = new StringBuilder().append("SELECT users._id, users.email, users.firstname, users.middlename, users.lastname, users.gender, users.dob ")
                .append("FROM users ")
                .append("WHERE users._id = ?");

        User user = jdbcTemplate.queryForObject(query.toString(), new Object[]{userId}, new UserProfileMapper());

        return user;
    }

    @Override
    public List<Transaction> getTransactionList(int userId, String historyKey, EnumLocale locale) {
        StringBuilder query = new StringBuilder().append("SELECT _id, transaction_id, result_id_encrypted, symptoms_sequence_encrypted, encryption_iv, timestamp ")
                .append("FROM users_symptoms_history ")
                .append("WHERE user_id = ? AND enabled = 1");

        List<TransactionEntry> transactionEntryList = jdbcTemplate.query(query.toString(), new Object[]{userId}, new TransactionEntryMapper());
        TransactionEntry.Transformer.decryptEntries(transactionEntryList, historyKey);

        for(TransactionEntry transactionEntry : transactionEntryList) {
            int rootSymptomId = 0;
            int resultId = Integer.valueOf(transactionEntry.getResult().trim());

            if(transactionEntry.getSymptomSequence().contains(",")) {
                rootSymptomId = Integer.valueOf(
                        transactionEntry.getSymptomSequence().substring(0,
                                transactionEntry.getSymptomSequence().indexOf(",")).trim()
                );
            } else {
                rootSymptomId = Integer.valueOf(transactionEntry.getSymptomSequence().trim());
            }

            transactionEntry.setRootSymptom(symptomService.getSymptomById(rootSymptomId, locale));
            transactionEntry.setResultEntry(symptomService.getResult(resultId, locale));
            transactionEntry.setBabySymptom(symptomService.isSymptomBelongsToCategory(rootSymptomId, 9));
        }

        List<Transaction> transactionList = Transaction.Mapper.map(transactionEntryList);

        return transactionList;
    }
    @Override
    public String createForgotPasswordCode(int userId) {
        String code = PasswordHasher.generateSalt();
        StringBuilder query = new StringBuilder().append("UPDATE users SET forgot_password_code = ? WHERE _id = ?");

        try {
            jdbcTemplate.update(query.toString(), code, userId);
            return code;
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String getEmailConfirmHash(String email) {
        StringBuilder query = new StringBuilder().append("SELECT confirm_email_hash ")
                .append("FROM users ")
                .append("WHERE email = ?");

        try {
            return jdbcTemplate.queryForObject(query.toString(), new Object[]{email}, String.class);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean validateForgotPasswordCode(int userId, String code) {
        StringBuilder query = new StringBuilder().append("SELECT forgot_password_code FROM users WHERE _id = ?");

        String dbCode = null;

        try {
            dbCode = jdbcTemplate.queryForObject(query.toString(), new Object[] {userId}, String.class);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        if(dbCode != null) {
            return dbCode.equals(code);
        }

        return false;
    }

    @Override
    public boolean sendForgotPasswordCode(int userId, String code) {
        //TODO: hardcode
        User user = getUser(userId);

        if(user != null) {
            EmailExchanger.sendEmail(user.getEmail(), "Восстановление пароля на Symptomaster.com", "Ваш код для восстановления пароля: " + code);
            return true;
        }

        return false;
    }

    @Override
    public void clearForgotPasswordCode(int userId) {
        StringBuilder query = new StringBuilder().append("UPDATE users SET forgot_password_code = NULL WHERE _id = ?");

        try {
            jdbcTemplate.update(query.toString(), userId);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getUserIdByPasswordCode(String code) {
        StringBuilder query = new StringBuilder().append("SELECT _id FROM users WHERE forgot_password_code = ?");

        int id = 0;

        try {
            id = jdbcTemplate.queryForObject(query.toString(), new Object[] {code}, Integer.class);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        return id;
    }


    // TODO: apply localization, evict these methods
    private static void sendConfirmMessage(EnumLocale loc, String email, String hash) {
        String message = LabelsTemp.SIGNUP_BODY + " : " + generateConfirmUrl(email, hash, loc);
        EmailExchanger.sendEmail(email, LabelsTemp.SIGNUP_SUBJECT, message);
    }

    private static String generateConfirmUrl(String email, String hash, EnumLocale loc) {
        String url = "<a href=\"" +  StaticValues.RELATIVE_URL + "/confirm-email?" + C.PARAMETER_CONFIRM_EMAIL_EMAIL + "=" + email + "&" + C.PARAMETER_CONFIRM_EMAIL_HASH + "=" + hash + "\">" + LabelsTemp.SIGNUP_URL + "</a>";
        return url;
    }
}
