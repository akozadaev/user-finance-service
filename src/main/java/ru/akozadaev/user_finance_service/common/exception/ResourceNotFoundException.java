package ru.akozadaev.user_finance_service.common.exception;

public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException(String message) { super(message); }
}
