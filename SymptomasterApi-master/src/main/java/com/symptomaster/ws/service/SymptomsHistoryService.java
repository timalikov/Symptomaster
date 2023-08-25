package com.symptomaster.ws.service;

import com.symptomaster.infra.dto.CouponDto;
import com.symptomaster.infra.dto.HistoryDto;
import com.symptomaster.infra.enumeration.EnumLocale;
import com.symptomaster.ws.models.SymptomsHistory;
import com.symptomaster.ws.models.Transaction;

import java.util.List;

/**
 * Created by nikolay on 10/08/16.
 */
public interface SymptomsHistoryService {

    List<SymptomsHistory> getSymptomsHistoryForGuest(EnumLocale locale, String transactionTag);

    List<HistoryDto> getUserSymptomsHistory(int userId);

    void updateEncryptedSymptomsHistoryInfo(HistoryDto h);

    void disableSymptomsHistoryForUser(int userId);

    void disableSymptomsHistoryForUser(String transactionId, int userId);

    void disableSymptomsHistoryForUser(int transactionEntryId, int userId);

    void saveTransaction(Transaction transaction, int userId);

    boolean isTransactionEntryBelongsUser(int symptomHistoryId, int userId);

    boolean isTransactionBelongsUser(String transactionId, int userId);

    void insertCoupon(CouponDto couponDto);

    boolean isCouponBelongsUser(String couponNumber, int userId);

    void sendCouponToEmail(String couponNumber, String email);

    String getCouponByTransactionId(String transactionId);
}
