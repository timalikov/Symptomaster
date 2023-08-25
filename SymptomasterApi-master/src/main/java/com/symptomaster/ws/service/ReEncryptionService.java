package com.symptomaster.ws.service;

import com.symptomaster.infra.dto.HistoryDto;
import com.symptomaster.infra.security.EncryptionInfo;
import com.symptomaster.ws.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by nikolay on 06/02/2017.
 */
@Service
public class ReEncryptionService {
    @Autowired
    private SymptomsHistoryService symptomsHistoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private EncryptionService encryptionService;

    public void reEncryption(String oldEncryptionKey, int userId, String newEncryptionKey) {
        List<HistoryDto> encryptedHistory = symptomsHistoryService.getUserSymptomsHistory(userId);

        if(encryptedHistory != null) {
            if(encryptedHistory.size() > 0) {
                User user = userService.getUser(userId);

                if(user != null) {
                    for(HistoryDto h : encryptedHistory) {
                        decryptAndEncryptWithNewParameters(h, newEncryptionKey, oldEncryptionKey);
                        symptomsHistoryService.updateEncryptedSymptomsHistoryInfo(h);
                    }
                }
            }
        }
    }

    private  void decryptAndEncryptWithNewParameters(HistoryDto h, String newEncryptionKey, String oldEncryptionKey) {
        String IV = h.getAesIV();

        EncryptionInfo eRes = encryptionService.decrypt(oldEncryptionKey, h.getEncryptedResultId(), IV),
                eSer = encryptionService.decrypt(oldEncryptionKey, h.getEncryptedSerializedSequence(), IV);

        IV = encryptionService.generateIV();

        h.setAesIV(IV);
        eRes = encryptionService.encrypt(newEncryptionKey, eRes.getDecryptedData(), IV);
        eSer = encryptionService.encrypt(newEncryptionKey, eSer.getDecryptedData(), IV);

        h.setEncryptedResultId(eRes.getEncryptedData());
        h.setEncryptedSerializedSequence(eSer.getEncryptedData());
    }
}
