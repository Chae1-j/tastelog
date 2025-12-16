package com.tastelog.common.response;

public class ErrorDetail {
    private final String field;
    private final String reason;

    public ErrorDetail(String field, String reason) {
        this.field = field;
        this.reason = reason;
    }

    public String getField() {
        return field;
    }
    public String getReason() {
        return reason;
    }
}
