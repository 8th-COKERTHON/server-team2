package com.hackathon.domain.bookmark.service;

import com.hackathon.domain.bookmark.dto.BookmarkReadDto;
import com.hackathon.domain.bookmark.entity.Bookmark;
import com.hackathon.domain.bookmark.repository.BookmarkRepository;
import com.hackathon.domain.checklist.entity.Checklist;
import com.hackathon.domain.checklist.repository.ChecklistRepository;
import com.hackathon.domain.member.entity.Member;
import com.hackathon.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

	@Mock
	private BookmarkRepository bookmarkRepository;

	@Mock
	private ChecklistRepository checklistRepository;

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private BookmarkService bookmarkService;

	@Test
	void findAllReturnsChecklistsForEachBookmark() {
		Bookmark firstBookmark = createBookmark(1L, 10L, "Spring Boot 예외 처리 정리");
		Bookmark secondBookmark = createBookmark(2L, 10L, "React 상태 관리 비교");
		Checklist firstChecklist = createChecklist(11L, firstBookmark, "게시글 읽기", false);
		Checklist secondChecklist = createChecklist(12L, firstBookmark, "예제 코드 실행하기", true);
		Checklist thirdChecklist = createChecklist(21L, secondBookmark, "링크 열람하기", false);

		given(bookmarkRepository.findOwnedActiveBookmarks(10L)).willReturn(List.of(firstBookmark, secondBookmark));
		given(checklistRepository.findByBookmarkId(1L)).willReturn(List.of(firstChecklist, secondChecklist));
		given(checklistRepository.findByBookmarkId(2L)).willReturn(List.of(thirdChecklist));

		BookmarkReadDto.Response response = bookmarkService.findAll(10L);

		assertThat(response.bookmarks()).hasSize(2);
		assertThat(response.bookmarks().get(0).checklists())
				.extracting(BookmarkReadDto.ChecklistResponse::content)
				.containsExactly("게시글 읽기", "예제 코드 실행하기");
		assertThat(response.bookmarks().get(0).checklists())
				.extracting(BookmarkReadDto.ChecklistResponse::isChecked)
				.containsExactly(false, true);
		assertThat(response.bookmarks().get(1).checklists())
				.extracting(BookmarkReadDto.ChecklistResponse::content)
				.containsExactly("링크 열람하기");
		verify(checklistRepository).findByBookmarkId(1L);
		verify(checklistRepository).findByBookmarkId(2L);
	}

	@Test
	void findOneReturnsChecklistsForBookmark() {
		Bookmark bookmark = createBookmark(1L, 10L, "Spring Boot 예외 처리 정리");
		Checklist firstChecklist = createChecklist(11L, bookmark, "게시글 읽기", true);
		Checklist secondChecklist = createChecklist(12L, bookmark, "예제 코드 실행하기", false);

		given(bookmarkRepository.findActiveBookmarkWithTags(1L)).willReturn(Optional.of(bookmark));
		given(checklistRepository.findByBookmarkId(1L)).willReturn(List.of(firstChecklist, secondChecklist));

		BookmarkReadDto.DetailResponse response = bookmarkService.findOne(10L, 1L);

		assertThat(response.checklists()).hasSize(2);
		assertThat(response.checklists())
				.extracting(BookmarkReadDto.ChecklistResponse::checklistId)
				.containsExactly(11L, 12L);
		assertThat(response.checklists())
				.extracting(BookmarkReadDto.ChecklistResponse::content)
				.containsExactly("게시글 읽기", "예제 코드 실행하기");
		assertThat(response.checklists())
				.extracting(BookmarkReadDto.ChecklistResponse::isChecked)
				.containsExactly(true, false);
		verify(checklistRepository).findByBookmarkId(1L);
	}

	@Test
	void findAllByTagReturnsOnlyMatchingActiveBookmarksOwnedByMember() {
		Bookmark bookmark = createBookmark(1L, 1L, "Spring 정리");
		given(bookmarkRepository.findOwnedActiveBookmarksByTagName(1L, "Spring"))
				.willReturn(List.of(bookmark));
		given(checklistRepository.findByBookmark_IdInOrderByIdAsc(List.of(1L)))
				.willReturn(List.of());

		BookmarkReadDto.TagFilterResponse response = bookmarkService.findAllByTag(1L, "Spring");

		assertThat(response.tagName()).isEqualTo("Spring");
		assertThat(response.bookmarks()).hasSize(1);
		assertThat(response.bookmarks().get(0).bookmarkId()).isEqualTo(1L);
		assertThat(response.bookmarks().get(0).title()).isEqualTo("Spring 정리");
	}

	@Test
	void findAllByTagReturnsEmptyListWhenNoBookmarkMatches() {
		given(bookmarkRepository.findOwnedActiveBookmarksByTagName(1L, "spring"))
				.willReturn(List.of());

		BookmarkReadDto.TagFilterResponse response = bookmarkService.findAllByTag(1L, "spring");

		assertThat(response.tagName()).isEqualTo("spring");
		assertThat(response.bookmarks()).isEmpty();
	}

	@Test
	void findAllByTagReturnsChecklistsGroupedByBookmark() {
		Bookmark bookmark1 = createBookmark(1L, 1L, "Spring 정리");
		Bookmark bookmark2 = createBookmark(2L, 1L, "Spring 심화");

		Checklist checklist1 = createChecklist(1L, bookmark1, "1번", false);
		Checklist checklist2 = createChecklist(2L, bookmark2, "2번", false);

		given(bookmarkRepository.findOwnedActiveBookmarksByTagName(1L, "Spring"))
				.willReturn(List.of(bookmark1, bookmark2));
		given(checklistRepository.findByBookmark_IdInOrderByIdAsc(List.of(1L, 2L)))
				.willReturn(List.of(checklist1, checklist2));

		BookmarkReadDto.TagFilterResponse response = bookmarkService.findAllByTag(1L, "Spring");

		assertThat(response.bookmarks()).hasSize(2);
		assertThat(response.bookmarks().get(0).checklists()).extracting("checklistId").containsExactly(1L);
		assertThat(response.bookmarks().get(1).checklists()).extracting("checklistId").containsExactly(2L);
	}

	private Bookmark createBookmark(Long bookmarkId, Long memberId, String title) {
		Member member = Member.builder()
				.loginId("yepot")
				.password("encoded-password")
				.nickname("은서")
				.totalScore(0)
				.build();
		ReflectionTestUtils.setField(member, "id", memberId);

		Bookmark bookmark = Bookmark.builder()
				.memberId(member)
				.url("https://example.com")
				.title(title)
				.status("ACTIVE")
				.remindAt(null)
				.build();
		ReflectionTestUtils.setField(bookmark, "id", bookmarkId);

		return bookmark;
	}

	private Checklist createChecklist(Long checklistId, Bookmark bookmark, String content, boolean checked) {
		Checklist checklist = new Checklist(bookmark, content);
		ReflectionTestUtils.setField(checklist, "id", checklistId);
		ReflectionTestUtils.setField(checklist, "checked", checked);
		return checklist;
	}
}