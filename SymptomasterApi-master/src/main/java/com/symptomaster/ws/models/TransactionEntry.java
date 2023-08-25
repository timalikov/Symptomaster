package com.symptomaster.ws.models;

import com.google.gson.annotations.Expose;
import com.symptomaster.infra.security.Encryptor;

import java.util.Date;
import java.util.List;


public class TransactionEntry {
    @Expose
    private int id;
    @Expose
    private String result;
    @Expose
    private String symptomSequence;

    private String transactionId;
    private byte[] resultIdEnc;
    private byte[] symptomSequenceEnc;
    private String iv;

    @Expose
    private Date date;

    @Expose
    private boolean babySymptom;

    @Expose
    private Symptom rootSymptom;
    @Expose
    private Result resultEntry;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getSymptomSequence() {
        return symptomSequence;
    }

    public void setSymptomSequence(String symptomSequence) {
        this.symptomSequence = symptomSequence;
    }

    public byte[] getResultIdEnc() {
        return resultIdEnc;
    }

    public void setResultIdEnc(byte[] resultIdEnc) {
        this.resultIdEnc = resultIdEnc;
    }

    public byte[] getSymptomSequenceEnc() {
        return symptomSequenceEnc;
    }

    public void setSymptomSequenceEnc(byte[] symptomSequenceEnc) {
        this.symptomSequenceEnc = symptomSequenceEnc;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isBabySymptom() {
        return babySymptom;
    }

    public void setBabySymptom(boolean babySymptom) {
        this.babySymptom = babySymptom;
    }

    public Symptom getRootSymptom() {
        return rootSymptom;
    }

    public void setRootSymptom(Symptom rootSymptom) {
        this.rootSymptom = rootSymptom;
    }

    public Result getResultEntry() {
        return resultEntry;
    }

    public void setResultEntry(Result resultEntry) {
        this.resultEntry = resultEntry;
    }

    public static class Transformer {
        public static void decryptEntries(List<TransactionEntry> transactionEntryList, String historyKey) {
            for (TransactionEntry transactionEntry : transactionEntryList) {
                try {
                    String resultId = Encryptor.decrypt(transactionEntry.getResultIdEnc(), historyKey, transactionEntry.getIv());
                    String symptomSequence = Encryptor.decrypt(transactionEntry.getSymptomSequenceEnc(), historyKey, transactionEntry.getIv());
                    transactionEntry.setResult(resultId);
                    transactionEntry.setSymptomSequence(symptomSequence);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
