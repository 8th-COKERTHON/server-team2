package com.hackathon.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
	DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 가입된 아이디입니다."),
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
	INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
	BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 북마크입니다."),
	INVALID_BOOKMARK_URL(HttpStatus.BAD_REQUEST, "올바른 HTTP 또는 HTTPS URL이 아닙니다."),
	INVALID_BOOKMARK_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 북마크 요청입니다."),
	BOOKMARK_TAG_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "태그는 최대 5개까지 등록할 수 있습니다.");

	private final HttpStatus status;
	private final String message;

	ErrorCode(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}
}
