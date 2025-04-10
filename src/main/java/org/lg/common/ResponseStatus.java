package org.lg.common;

public class ResponseStatus {
    private final int code;
    private final String message;

    public ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ResponseStatus of(StatusCode statusCode) {
        return new ResponseStatus(statusCode.code(), statusCode.message());
    }

    public static ResponseStatus of(int code, String message) {
        return new ResponseStatus(code, message);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
