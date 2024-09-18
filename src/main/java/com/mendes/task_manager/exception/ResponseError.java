package com.mendes.task_manager.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ResponseError(
    LocalDateTime timestamp,
    Integer statusCode,
    String statusError,
    List<String> errors
) {
}
