package com.symptomaster.ws.controller;

import com.google.common.reflect.TypeToken;
import com.symptomaster.infra.dto.CouponDto;
import com.symptomaster.infra.enumeration.EnumLocale;
import com.symptomaster.infra.security.EncryptionInfo;
import com.symptomaster.infra.security.PasswordHasher;
import com.symptomaster.ws.models.SymptomsHistory;
import com.symptomaster.ws.models.Transaction;
import com.symptomaster.ws.models.TransactionEntry;
import com.symptomaster.ws.models.User;
import com.symptomaster.ws.service.EncryptionService;
import com.symptomaster.ws.service.SymptomsHistoryService;
import com.symptomaster.ws.service.UserService;
import com.symptomaster.ws.usersession.UserSession;
import com.symptomaster.ws.usersession.UserSessionManager;
import com.symptomaster.ws.utils.Utils;
import com.symptomaster.ws.utils.http.DefaultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by nikolay on 10/08/16.
 */
@RestController
@Api(value = "SymptomsHistory", description = "Services for users_symptoms_history")
@RequestMapping("/symptoms-history")
public class SymptomsHistoryController extends BaseRestController {

    @Autowired
    private SymptomsHistoryService symptomsHistoryService;

    @Autowired
    private UserSessionManager userSessionManager;

    @Autowired
    private UserService userService;

    @Autowired
    private EncryptionService encryptionService;

    @ApiOperation(value = "Get USH by TransactionTag")
    @RequestMapping(value = "/guest/{transactionTag}", method = RequestMethod.GET, produces = "application/json")
    public void getSymptomList(HttpServletResponse response,
                               @PathVariable String transactionTag,
                               @RequestParam(value = "locale", defaultValue = "RUS") EnumLocale locale) {

        DefaultResponse JSONreponse;

        try {
            List<SymptomsHistory> histories = symptomsHistoryService.getSymptomsHistoryForGuest(locale, transactionTag);

            JSONreponse = new DefaultResponse(histories);
            renderJSON(response, JSONreponse.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            renderError(response, e);
        }
    }

    @ApiOperation(value = "Save transaction")
    @RequestMapping(value = "/savetransaction", method = RequestMethod.POST, produces = "application/json")
    public void saveTransaction(HttpServletResponse response,
                                @RequestParam("history") String history,
                                @RequestParam(value = "authtoken", required = false) String token,
                                @RequestParam(value = "historyKey", required = false) String historyKey) {

        DefaultResponse JSONreponse;

        try {
            List<TransactionEntry> transactionEntries = Utils.json.fromJson(history, new TypeToken<List<TransactionEntry>>(){}.getType());
            int userId = 0;

            if(token != null && !token.trim().equals("")) {
                if(userSessionManager.isSessionValid(token) && historyKey != null) {
                    userId = userSessionManager.getSession(token).getUserId();

                    for(TransactionEntry entry : transactionEntries) {
                        String IV = encryptionService.generateIV();
                        EncryptionInfo eRes = encryptionService.encrypt(historyKey, entry.getResult(), IV),
                                eSec = encryptionService.encrypt(historyKey, entry.getSymptomSequence(), IV);

                        entry.setIv(IV);
                        entry.setSymptomSequenceEnc(eSec.getEncryptedData());
                        entry.setResultIdEnc(eRes.getEncryptedData());
                        entry.setResult(null);
                        entry.setSymptomSequence(null);
                    }
                } else {
                    throw new IllegalAccessException("User is unauthorized");
                }
            }

            Transaction transaction = new Transaction();

            //TODO: check on duplicate
            transaction.setTransactionId(PasswordHasher.generateTransactionId());
            transaction.setTransactionEntryList(transactionEntries);

            symptomsHistoryService.saveTransaction(transaction, userId);
            CouponDto couponDto = new CouponDto();
            couponDto.setCouponNumber(PasswordHasher.generateCouponNumber());
            couponDto.setTransactionId(transaction.getTransactionId());
            couponDto.setUserId(userId);
            symptomsHistoryService.insertCoupon(couponDto);

            JSONreponse = new DefaultResponse(couponDto.getCouponNumber());
            renderJSON(response, JSONreponse.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            renderError(response, e);
        }
    }

    @ApiOperation(value = "Delete transaction")
    @RequestMapping(value = "/delete/transaction", method = RequestMethod.POST, produces = "application/json")
    public void deleteTransaction(HttpServletResponse response,
                                  @RequestParam("transactionId") String transactionId,
                                  @RequestParam(value = "authtoken", required = false) String token) {

        DefaultResponse JSONreponse;
        int userId = userSessionManager.getSession(token).getUserId();

        try {
            if(!symptomsHistoryService.isTransactionBelongsUser(transactionId, userId)) {
                throw new IllegalAccessException("Transaction does not belong to user");
            }

            symptomsHistoryService.disableSymptomsHistoryForUser(transactionId, userId);
            JSONreponse = new DefaultResponse("");
            renderJSON(response, JSONreponse.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            renderError(response, e);
        }
    }

    @ApiOperation(value = "Delete transactionEntry")
    @RequestMapping(value = "/delete/transaction-entry", method = RequestMethod.POST, produces = "application/json")
    public void deleteTransactionEntry(HttpServletResponse response,
                                       @RequestParam("transactionEntryId") int transactionEntryId,
                                       @RequestParam(value = "authtoken", required = false) String token) {

        DefaultResponse JSONreponse;
        int userId = userSessionManager.getSession(token).getUserId();

        try {
            if(!symptomsHistoryService.isTransactionEntryBelongsUser(transactionEntryId, userId)) {
                throw new IllegalAccessException("TransactionEntry does not belong to user");
            }

            symptomsHistoryService.disableSymptomsHistoryForUser(transactionEntryId, userId);
            JSONreponse = new DefaultResponse("");
            renderJSON(response, JSONreponse.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            renderError(response, e);
        }
    }

    @ApiOperation(value = "Send coupon to email")
    @RequestMapping(value = "/coupon/send", method = RequestMethod.POST, produces = "application/json")
    public void deleteTransactionEntry(HttpServletResponse response,
                                       @RequestParam(value = "transactionId", required = false) String transactionId,
                                       @RequestParam(value = "couponNumber", required = false) String couponNumber,
                                       @RequestParam(value = "authtoken", required = false) String token,
                                       @RequestParam(value = "email", required = false) String email) {

        DefaultResponse JSONreponse;

        try {
            if(token != null && !token.trim().equals("")) {
                if(userSessionManager.isSessionValid(token)) {

                } else {
                    throw new IllegalAccessException("User is unauthorized");
                }

                int userId = userSessionManager.getSession(token).getUserId();

                if(transactionId != null && !transactionId.trim().equals("")) {
                    couponNumber = symptomsHistoryService.getCouponByTransactionId(transactionId);
                }

                if(symptomsHistoryService.isCouponBelongsUser(couponNumber, userId)) {
                    User user = userService.getUser(userId);
                    symptomsHistoryService.sendCouponToEmail(couponNumber, user.getEmail());
                } else {
                    throw new IllegalAccessException("Coupon does not belong to user");
                }
            } else {
                if(couponNumber == null || couponNumber.trim().equals("")) {
                    throw new IllegalAccessException("Coupon does not belong to user");
                }
                symptomsHistoryService.sendCouponToEmail(couponNumber, email);
            }

            JSONreponse = new DefaultResponse("");
            renderJSON(response, JSONreponse.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            renderError(response, e);
        }
    }
}
