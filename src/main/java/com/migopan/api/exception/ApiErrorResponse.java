package com.migopan.api.exception;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        Instant Timestamp,
        int status,
        String error,
        String message,
        String path,
        List<ApiFieldError> fieldErrors
) {}
