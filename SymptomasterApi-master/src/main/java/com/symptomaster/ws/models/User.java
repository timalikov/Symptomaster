package com.symptomaster.ws.models;


import com.google.gson.annotations.Expose;
import com.symptomaster.infra.enumeration.EnumUserGender;
import com.symptomaster.infra.security.PasswordHasher;

import java.util.Date;

/**
 * Created by dkarmazi on 1/30/17.
 */
public class User {
    @Expose
    private int id;
    @Expose
    private String email;
    private String password;
    private String salt;
    private String encryptionSalt;
    @Expose
    private String firstName;
    @Expose
    private String middleName;
    @Expose
    private String lastName;
    @Expose
    private EnumUserGender gender;
    @Expose
    private Date dob;
    private String externalId;
    private String confirmEmailHash;

    public User() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public EnumUserGender getGender() {
        return gender;
    }

    public void setGender(EnumUserGender gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEncryptionSalt() {
        return encryptionSalt;
    }

    public void setEncryptionSalt(String encryptionSalt) {
        this.encryptionSalt = encryptionSalt;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getConfirmEmailHash() {
        return confirmEmailHash;
    }

    public void setConfirmEmailHash(String confirmEmailHash) {
        this.confirmEmailHash = confirmEmailHash;
    }

    public static class Mapper {
        public static User map(String firstName,
                               String middleName,
                               String lastName,
                               EnumUserGender gender,
                               long dobL,
                               String email,
                               String passwordPlain) {
            User newUser = new User();

            Date dob = new Date(dobL);
            String salt = PasswordHasher.generateSalt();
            String encryptionSalt = PasswordHasher.generateSalt();
            String password = PasswordHasher.sha512(passwordPlain, salt);

            newUser.setFirstName(firstName);
            newUser.setMiddleName(middleName);
            newUser.setLastName(lastName);
            newUser.setGender(gender);
            newUser.setDob(dob);
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setSalt(salt);
            newUser.setEncryptionSalt(encryptionSalt);
            newUser.setExternalId(PasswordHasher.generateExternalId());
            newUser.setConfirmEmailHash(PasswordHasher.generateConfirmEmailHash());

            return newUser;
        }

        public static User map(int userId,
                               String firstName,
                               String middleName,
                               String lastName,
                               EnumUserGender gender,
                               long dobL) {
            User newUser = new User();

            Date dob = new Date(dobL);

            newUser.setId(userId);
            newUser.setFirstName(firstName);
            newUser.setMiddleName(middleName);
            newUser.setLastName(lastName);
            newUser.setGender(gender);
            newUser.setDob(dob);

            return newUser;
        }

        public static Object[] mapToObjectArrayForInsert(User newUser) {
            Object[] objects = new Object[11];

            objects[0] = newUser.getEmail();
            objects[1] = newUser.getPassword();
            objects[2] = newUser.getSalt();
            objects[3] = newUser.getEncryptionSalt();
            objects[4] = newUser.getFirstName();
            objects[5] = newUser.getMiddleName();
            objects[6] = newUser.getLastName();
            objects[7] = newUser.getGender().getId();
            objects[8] = newUser.getDob();
            objects[9] = newUser.getExternalId();
            objects[10] = newUser.getConfirmEmailHash();

            return objects;
        }

        public static Object[] mapToObjectArrayForUpdate(User user) {
            Object[] objects = new Object[6];

            objects[0] = user.getFirstName();
            objects[1] = user.getMiddleName();
            objects[2] = user.getLastName();
            objects[3] = user.getGender().getId();
            objects[4] = user.getDob();
            objects[5] = user.getId();

            return objects;
        }
    }
}
