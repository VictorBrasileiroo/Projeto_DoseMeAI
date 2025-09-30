package com.dosemeai.DoseMeAI.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseModel<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ResponseModel<T> ok(boolean success, String message, T data) {
        return new ResponseModel<>(success, message, data);
    }

    public static <T> ResponseModel<T> error(boolean success, String message) {
        return new ResponseModel<>(success, message, null);
    }
}