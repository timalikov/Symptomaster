package com.symptomaster.ws.controller;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.symptomaster.ws.exceptions.AuthenticationException;
import com.symptomaster.ws.utils.http.DefaultResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maxim Kasyanov on 08/01/16.
 */
public abstract class BaseRestController {


    public static final String ENCODING = "UTF-8";

    public void renderJSON(HttpServletResponse response, String json) {
        renderJSON(response, json, HttpStatus.OK);
    }

    public void renderJSON(HttpServletResponse response, String json, HttpStatus status) {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(ENCODING);

        PrintWriter out = null;

        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(out != null)
            out.println(json);
    }

    public void renderError(HttpServletResponse response, Exception ex) {
        PrintWriter out = null;
        String errorJson = null;

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(ENCODING);

        try {
            out = response.getWriter();

            if (ex instanceof IllegalStateException ||
                    ex instanceof MalformedJsonException ||
                    ex instanceof JsonSyntaxException ||
                    ex instanceof JsonParseException ||
                    ex instanceof IllegalArgumentException ||
                    ex instanceof IndexOutOfBoundsException ||
                    ex instanceof ClassCastException) {
                errorJson = handleException(ex.getMessage() != null ? ex.getMessage() : ex.getClass().toString(), response, HttpStatus.BAD_REQUEST);

            } else if (ex instanceof NullPointerException) {
                errorJson = handleException(ex.getMessage() != null ? ex.getMessage() : ex.getClass().toString(), response, HttpStatus.NOT_FOUND);

            } else if (ex instanceof InvocationTargetException) {
                String message = ex.getMessage() != null ? ex.getMessage() : ex.getClass().toString();
                if (((InvocationTargetException) ex).getTargetException() != null && ((InvocationTargetException) ex).getTargetException().getMessage() != null)
                    message = ((InvocationTargetException) ex).getTargetException().getLocalizedMessage();

                errorJson = handleException(message, response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof IllegalAccessException) {
                errorJson = handleException(ex.getMessage() != null ? ex.getMessage() : ex.getClass().toString(), response, HttpStatus.FORBIDDEN);
            } else if (ex instanceof AuthenticationException) {
                errorJson = handleException(ex.getMessage() != null ? ex.getMessage() : ex.getClass().toString(), response, HttpStatus.UNAUTHORIZED);
            } else {
                errorJson = handleException(ex.getMessage() != null ? ex.getMessage() : ex.getClass().toString(), response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(out != null)
            out.println(errorJson);
    }

    private String handleException(String message, HttpServletResponse response, HttpStatus status) {
        Map<String, String> error = new HashMap<>();
        error.put("message", message);
        DefaultResponse defaultResponse = new DefaultResponse(error, status.value());
        response.setStatus(status.value());
        return defaultResponse.toJson();
    }
}
