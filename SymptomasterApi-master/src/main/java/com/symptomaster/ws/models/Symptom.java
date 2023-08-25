package com.symptomaster.ws.models;

import com.google.gson.annotations.Expose;

/**
 * Created by Maxim Kasyanov on 13/01/16.
 */
public class Symptom {

    @Expose
    private int id;

    @Expose
    private String name;

    @Expose
    private String description;

    @Expose
    private String question;

    @Expose
    private String  precaution;

    @Expose
    private String suggestion;


    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getPrecaution() {
        return precaution;
    }

    public void setPrecaution(String precaution) {
        this.precaution = precaution;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}
