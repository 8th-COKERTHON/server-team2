package com.hackathon.domain.member.controller;

import com.hackathon.domain.member.dto.AuthDto.LoginRequest;
import com.hackathon.domain.member.dto.AuthDto.MemberInfoResponse;
import com.hackathon.domain.member.dto.AuthDto.SignUpRequest;
import com.hackathon.domain.member.dto.AuthDto.TokenResponse;
import com.hackathon.domain.member.service.AuthService;
import com.hackathon.global.apiPayload.ApiResponse;
import com.hackathon.global.apiPayload.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

	@GetMapping("/me")
	@Operation(summary = "내 정보 조회")
	public ApiResponse<MemberInfoResponse> getMyInfo(Authentication authentication) {
		Long memberId = (Long) authentication.getPrincipal();
		return ApiResponse.onSuccess(GeneralSuccessCode.OK, authService.getMyInfo(memberId));
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃")
	public ApiResponse<Void> logout() {
		return ApiResponse.onSuccess(GeneralSuccessCode.OK, null);
	}

	@DeleteMapping("/withdraw")
	@Operation(summary = "회원탈퇴")
	public ApiResponse<Void> withdraw(Authentication authentication) {
		Long memberId = (Long) authentication.getPrincipal();
		authService.withdraw(memberId);
		return ApiResponse.onSuccess(GeneralSuccessCode.OK, null);
	}
}
