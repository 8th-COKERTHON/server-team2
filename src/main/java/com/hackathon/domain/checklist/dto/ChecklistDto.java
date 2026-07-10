package com.hackathon.domain.checklist.dto;

import com.hackathon.domain.checklist.entity.Checklist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ChecklistDto {

	public record CreateRequest(
			@NotBlank(message = "체크리스트 내용은 필수입니다.")
			@Size(max = 500, message = "체크리스트 내용은 500자 이하로 입력해주세요.")
			String content
	) {}

	public record UpdateRequest(
			@NotBlank(message = "체크리스트 내용은 필수입니다.")
			@Size(max = 500, message = "체크리스트 내용은 500자 이하로 입력해주세요.")
			String content
	) {}

	public record ChecklistResponse(
			Long checklistId,
			Long bookmarkId,
			String content,
			Boolean isChecked,
			LocalDateTime createdAt
	) {
		public static ChecklistResponse from(Checklist checklist) {
			return new ChecklistResponse(
					checklist.getId(),
					checklist.getBookmark().getId(),
					checklist.getContent(),
					checklist.isChecked(),
					checklist.getCreatedAt()
			);
		}
	}

	public record ChecklistCheckResponse(
			Long checklistId,
			Long bookmarkId,
			String content,
			Boolean isChecked,
			LocalDateTime updatedAt
	) {
		public static ChecklistCheckResponse from(Checklist checklist) {
			return new ChecklistCheckResponse(
					checklist.getId(),
					checklist.getBookmark().getId(),
					checklist.getContent(),
					checklist.isChecked(),
					checklist.getUpdatedAt()
			);
		}
	}
}
