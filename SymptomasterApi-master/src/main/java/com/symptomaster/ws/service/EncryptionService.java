package com.symptomaster.ws.service;

import com.symptomaster.infra.security.EncryptionInfo;
import com.symptomaster.infra.security.Encryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by nikolay on 06/02/2017.
 */
@Service
public class EncryptionService {
    @Autowired
    private UserService userService;

    public String generateIV() {
        return Encryptor.generateIV();
    }

    public EncryptionInfo encrypt(String encryptionKey, String plainText) {
        EncryptionInfo ei = new EncryptionInfo();

        try {
            ei.setIV(Encryptor.generateIV());
            ei.setEncryptedData(Encryptor.encrypt(plainText, encryptionKey, ei.getIV()));
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

        return ei;
    }

    public EncryptionInfo encrypt(String encryptionKey, String cipherText, String IV) {
        EncryptionInfo ei = new EncryptionInfo();

        try {
            ei.setIV(IV);
            ei.setEncryptedData(Encryptor.encrypt(cipherText, encryptionKey, ei.getIV()));
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

        return ei;
    }

    public EncryptionInfo decrypt(String encryptionKey, byte[] encryptedData, String IV) {
        EncryptionInfo ed = new EncryptionInfo();

        try {
            String decryptedData = Encryptor.decrypt(encryptedData, encryptionKey, IV);
            ed.setDecryptedData(decryptedData);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

        return ed;
    }
}
