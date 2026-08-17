package com.migopan.api.exception;

public record ApiErrorResponse(
    Instant Timestamp,
    int status,
    String error
    String message,
    String path,
    List<ApiFieldError> fieldErrors
) {}
