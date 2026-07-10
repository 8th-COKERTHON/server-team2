package com.hackathon.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<Map<String, String>> handleCustomException(CustomException e) {
		return ResponseEntity
				.status(e.getErrorCode().getStatus())
				.body(Map.of("message", e.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getFieldErrors().stream()
				.findFirst()
				.map(error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "잘못된 요청입니다.")
				.orElse("잘못된 요청입니다.");

		return ResponseEntity
				.badRequest()
				.body(Map.of("message", message));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
		return ResponseEntity
				.badRequest()
				.body(Map.of("message", e.getMessage() != null ? e.getMessage() : "잘못된 요청입니다."));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleException(Exception e) {
		return ResponseEntity
				.internalServerError()
				.body(Map.of("message", "서버 내부 오류: " + e.getMessage()));
	}
}
