package com.hackathon.domain.member.controller;

import com.hackathon.domain.member.dto.AuthDto.LoginRequest;
import com.hackathon.domain.member.dto.AuthDto.SignUpRequest;
import com.hackathon.domain.member.dto.AuthDto.TokenResponse;
import com.hackathon.domain.member.service.AuthService;
import com.hackathon.global.apiPayload.ApiResponse;
import com.hackathon.global.apiPayload.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/signup")
	@Operation(summary = "회원가입")
	public ApiResponse<Void> signUp(@Valid @RequestBody SignUpRequest request) {
		authService.signUp(request);
		return ApiResponse.onSuccess(GeneralSuccessCode.CREATED, null);
	}

	@PostMapping("/login")
	@Operation(summary = "로그인", description = "아이디/비밀번호로 로그인 후 access/refresh 토큰 발급")
	public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
		TokenResponse tokens = authService.login(request);
		return ApiResponse.onSuccess(GeneralSuccessCode.OK, tokens);
	}
}
