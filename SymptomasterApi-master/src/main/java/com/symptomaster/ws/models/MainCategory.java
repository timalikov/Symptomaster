package com.symptomaster.ws.models;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by nikolay on 30/06/16.
 */
public class MainCategory {
    @Expose
    private int categoryId;

    @Expose
    private String categoryName;

    @Expose
    private boolean babySymptom;

    @Expose
    private List<Symptom> symptoms;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<Symptom> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<Symptom> symptoms) {
        this.symptoms = symptoms;
    }

    public boolean isBabySymptom() {
        return babySymptom;
    }

    public void setBabySymptom(boolean babySymptom) {
        this.babySymptom = babySymptom;
    }
}
