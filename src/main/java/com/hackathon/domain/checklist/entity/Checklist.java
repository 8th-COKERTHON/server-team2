package com.hackathon.domain.checklist.entity;

import com.hackathon.domain.bookmark.entity.Bookmark;
import com.hackathon.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "checklist")
public class Checklist extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "bookmark_id", nullable = false)
	private Bookmark bookmark;

	@Column(nullable = false, length = 500)
	private String content;

	@Column(name = "is_checked", nullable = false)
	private boolean checked;

	public Checklist(Bookmark bookmark, String content) {
		this.bookmark = bookmark;
		this.content = content;
		this.checked = false;
	}

	public void updateContent(String content) {
		this.content = content;
	}

	public void toggleChecked() {
		this.checked = !this.checked;
	}
}
