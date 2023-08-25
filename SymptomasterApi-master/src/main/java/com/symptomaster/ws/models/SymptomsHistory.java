package com.symptomaster.ws.models;

import com.google.gson.annotations.Expose;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikolay on 11/08/16.
 */
public class SymptomsHistory {
    private static final String STACK_SEPARATOR = ",";

    @Expose
    private int id;

    @Expose
    private String transactionId;

    @Expose
    private int userId;

    @Expose
    private int symptomId;

    @Expose
    private List<Symptom> answersStack;

    @Expose
    private Result result;

    @Expose
    private Date timestamp;

    @Expose
    int enabled;

    public SymptomsHistory() {
        this.id = 0;
        this.transactionId = "";
        this.userId = 0;
        this.symptomId = 0;
        this.answersStack = new ArrayList<Symptom>();
        this.result = new Result();
        this.timestamp = new Date(System.currentTimeMillis());
    }
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSymptomId() {
        return symptomId;
    }

    public void setSymptomId(int symptomId) {
        this.symptomId = symptomId;
    }

    public List<Symptom> getAnswersStack() {
        return answersStack;
    }

    public boolean getEnabled() {
        return enabled == 1;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled ? 1: 0;
    }

    public void setAnswersStack(List<Symptom> answersStack) {
        this.answersStack = answersStack;
    }


    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
