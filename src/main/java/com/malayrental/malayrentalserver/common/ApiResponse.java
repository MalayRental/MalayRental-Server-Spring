package com.malayrental.malayrentalserver.common;

import lombok.Data;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
public class ApiResponse {
    private int code;
    private String message;
    private long timestamp;
    private Object data;

    public static ApiResponse ok(String message, Object content) {
        ApiResponse r = new ApiResponse();
        r.code = 200;
        r.message = message;
        r.timestamp = Instant.now().toEpochMilli();
        r.data = content;
        return r;
    }

    public static ApiResponse error(int code, String message) {
        ApiResponse r = new ApiResponse();
        r.code = code;
        r.message = message;
        r.timestamp = Instant.now().toEpochMilli();
        r.data = null;
        return r;
    }
}