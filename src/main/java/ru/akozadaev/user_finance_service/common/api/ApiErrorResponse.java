package ru.akozadaev.user_finance_service.common.api;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse(Instant timestamp, int status, String error, String message, Map<String, String> details) {
}
