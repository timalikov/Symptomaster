package com.symptomaster.ws.models;

import com.google.gson.annotations.Expose;

/**
 * Created by nikolay on 29/06/16.
 */
public class Result {

    @Expose
    private int id;

    @Expose
    private int symptomId;

    @Expose
    private int stringId;

    @Expose
    private int enabled;

    @Expose
    private String cause;

    @Expose
    private String recommendation;

    @Expose
    private int priority;

    @Expose
    private int linkToSymptom;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSymptomId() {
        return symptomId;
    }

    public void setSymptomId(int symptomId) {
        this.symptomId = symptomId;
    }

    public int getStringId() {
        return stringId;
    }

    public void setStringId(int stringId) {
        this.stringId = stringId;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getLinkToSymptom() {
        return linkToSymptom;
    }

    public void setLinkToSymptom(int linkToSymptom) {
        this.linkToSymptom = linkToSymptom;
    }
}
