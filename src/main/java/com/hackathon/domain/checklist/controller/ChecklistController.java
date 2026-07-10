package com.hackathon.domain.checklist.controller;

import com.hackathon.domain.checklist.dto.ChecklistDto.ChecklistCheckResponse;
import com.hackathon.domain.checklist.dto.ChecklistDto.ChecklistResponse;
import com.hackathon.domain.checklist.dto.ChecklistDto.CreateRequest;
import com.hackathon.domain.checklist.dto.ChecklistDto.UpdateRequest;
import com.hackathon.domain.checklist.service.ChecklistService;
import com.hackathon.global.exception.CustomException;
import com.hackathon.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks/{bookmarkId}/checklists")
@Tag(name = "Checklist", description = "체크리스트 API")
public class ChecklistController {

	private final ChecklistService checklistService;

	@PostMapping
	@Operation(summary = "체크리스트 등록")
	public ResponseEntity<ChecklistResponse> createChecklist(
			@AuthenticationPrincipal Long memberId,
			@PathVariable("bookmarkId") Long bookmarkId,
			@Valid @RequestBody CreateRequest request
	) {
		return ResponseEntity.ok(checklistService.createChecklist(requireMemberId(memberId), bookmarkId, request));
	}

	@PatchMapping("/{checklistId}")
	@Operation(summary = "체크리스트 수정")
	public ResponseEntity<ChecklistResponse> updateChecklist(
			@AuthenticationPrincipal Long memberId,
			@PathVariable("bookmarkId") Long bookmarkId,
			@PathVariable("checklistId") Long checklistId,
			@Valid @RequestBody UpdateRequest request
	) {
		return ResponseEntity.ok(
				checklistService.updateChecklist(requireMemberId(memberId), bookmarkId, checklistId, request)
		);
	}

	@DeleteMapping("/{checklistId}")
	@Operation(summary = "체크리스트 삭제")
	public ResponseEntity<Void> deleteChecklist(
			@AuthenticationPrincipal Long memberId,
			@PathVariable("bookmarkId") Long bookmarkId,
			@PathVariable("checklistId") Long checklistId
	) {
		checklistService.deleteChecklist(requireMemberId(memberId), bookmarkId, checklistId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{checklistId}/check")
	@Operation(summary = "체크리스트 체크/해제")
	public ResponseEntity<ChecklistCheckResponse> toggleChecklist(
			@AuthenticationPrincipal Long memberId,
			@PathVariable("bookmarkId") Long bookmarkId,
			@PathVariable("checklistId") Long checklistId
	) {
		return ResponseEntity.ok(checklistService.toggleChecklist(requireMemberId(memberId), bookmarkId, checklistId));
	}

	private Long requireMemberId(Long memberId) {
		if (memberId == null) {
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}
		return memberId;
	}
}
