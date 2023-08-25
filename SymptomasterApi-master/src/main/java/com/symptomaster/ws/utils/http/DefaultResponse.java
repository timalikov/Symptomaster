package com.symptomaster.ws.utils.http;

import com.google.gson.annotations.Expose;
import com.symptomaster.ws.utils.Utils;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * Created by Maxim Kasyanov on 08/01/16.
 */
public class DefaultResponse implements Serializable {

    @Expose
    private Object data;
    @Expose
    private Long timestamp;
    @Expose
    private int state = -1;

    public DefaultResponse(Object data, int state) {
        this.timestamp = System.currentTimeMillis();
        this.data = data;
        this.state = state;
    }

    public DefaultResponse(Object data) {
        this.data = data;
        this.timestamp = System.currentTimeMillis();
        this.state = HttpStatus.OK.value();
    }

    public DefaultResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public Object getData() {
        return data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public int getState() {
        return state;
    }

    public String toJson() {
        if (data == null || state == -1 || timestamp == null) {
            data = "Response incomplete, one of the fields is null: " + (data == null ? "data" : (state + " " + timestamp));
            state = 500;
            timestamp = System.currentTimeMillis();
        }
        return Utils.toJson(this);
    }
}

