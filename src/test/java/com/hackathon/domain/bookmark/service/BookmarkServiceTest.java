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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

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

        Checklist checklist1 = createChecklist(1L, bookmark1, "1번");
        Checklist checklist2 = createChecklist(2L, bookmark2, "2번");

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

    private Checklist createChecklist(Long checklistId, Bookmark bookmark, String content) {
        Checklist checklist = new Checklist(bookmark, content);
        ReflectionTestUtils.setField(checklist, "id", checklistId);
        return checklist;
    }
}