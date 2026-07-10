package com.hackathon.global.entity;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BaseEntityTest {

	@Test
	void onCreateInitializesCreatedAtAndUpdatedAt() {
		TestEntity entity = new TestEntity();

		ReflectionTestUtils.invokeMethod(entity, "onCreate");

		assertThat(entity.getCreatedAt()).isNotNull();
		assertThat(entity.getUpdatedAt()).isNotNull();
		assertThat(entity.getUpdatedAt()).isEqualTo(entity.getCreatedAt());
	}

	@Test
	void onUpdateRefreshesUpdatedAt() {
		TestEntity entity = new TestEntity();
		LocalDateTime createdAt = LocalDateTime.now().minusMinutes(2);
		LocalDateTime previousUpdatedAt = createdAt.plusMinutes(1);

		ReflectionTestUtils.setField(entity, "createdAt", createdAt);
		ReflectionTestUtils.setField(entity, "updatedAt", previousUpdatedAt);

		ReflectionTestUtils.invokeMethod(entity, "onUpdate");

		assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
		assertThat(entity.getUpdatedAt()).isAfter(previousUpdatedAt);
	}

	private static final class TestEntity extends BaseEntity {
	}
}
