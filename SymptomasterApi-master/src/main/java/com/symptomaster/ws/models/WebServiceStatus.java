package com.symptomaster.ws.models;

import com.google.gson.annotations.Expose;
import com.symptomaster.infra.utils.EmailExchanger;

public class WebServiceStatus {
    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private static final String EMAIL_ALREADY_EXIST = "email_already_exists";

    @Expose
    private String webServiceResponse;

    public static WebServiceStatus getInstanceError() {
        return new WebServiceStatus(ERROR);
    }

    public static WebServiceStatus getInstanceSuccess() {
        return new WebServiceStatus(SUCCESS);
    }

    public static WebServiceStatus getInstanceEmailAlreadyExists() {
        return new WebServiceStatus(EMAIL_ALREADY_EXIST);
    }

    private WebServiceStatus(String webServiceResponse) {
        this.webServiceResponse = webServiceResponse;
    }

    public String getWebServiceResponse() {
        return webServiceResponse;
    }

    public void setWebServiceResponse(String webServiceResponse) {
        this.webServiceResponse = webServiceResponse;
    }
}
