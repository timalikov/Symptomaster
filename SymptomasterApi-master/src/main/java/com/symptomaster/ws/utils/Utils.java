package com.symptomaster.ws.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Maxim Kasyanov on 05/01/16.
 */
public class Utils {
    public static final Gson json = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping()
                                                        .setPrettyPrinting().setDateFormat("dd-MM-yyyy HH:mm").create();

    public static String toJson(Object object) {
        return json.toJson(object);
    }

}
