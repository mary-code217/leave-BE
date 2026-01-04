package com.hoho.leave.domain.handover.entity;

import com.hoho.leave.domain.user.entity.User;
import com.hoho.leave.domain.user.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HandoverNote 엔티티 테스트")
class HandoverNoteTest {

    private User mockAuthor;
    private HandoverNote handoverNote;
    private String title;
    private String content;

    @BeforeEach
    void setUp() {
        mockAuthor = new User("test@example.com", UserRole.ROLE_USER);
        ReflectionTestUtils.setField(mockAuthor, "id", 1L);
        ReflectionTestUtils.setField(mockAuthor, "username", "홍길동");
        ReflectionTestUtils.setField(mockAuthor, "employeeNo", "EMP001");

        title = "휴가 인수인계";
        content = "휴가 중 처리해야 할 업무 안내입니다.";

        handoverNote = HandoverNote.create(mockAuthor, title, content);
    }

    @Nested
    @DisplayName("인수인계 노트 생성")
    class Create {

        @Test
        @DisplayName("성공: create 정적 메서드로 인수인계 노트를 생성한다")
        void create_Success() {
            // given
            User author = new User("author@example.com", UserRole.ROLE_USER);
            ReflectionTestUtils.setField(author, "id", 10L);
            ReflectionTestUtils.setField(author, "username", "김철수");

            String testTitle = "프로젝트 인수인계";
            String testContent = "프로젝트 진행 상황과 주요 이슈입니다.";

            // when
            HandoverNote createdNote = HandoverNote.create(author, testTitle, testContent);

            // then
            assertThat(createdNote).isNotNull();
            assertThat(createdNote.getAuthor()).isEqualTo(author);
            assertThat(createdNote.getTitle()).isEqualTo(testTitle);
            assertThat(createdNote.getContent()).isEqualTo(testContent);
        }

        @Test
        @DisplayName("성공: 작성자 정보가 올바르게 저장된다")
        void create_AuthorStoredCorrectly() {
            // when
            HandoverNote createdNote = HandoverNote.create(mockAuthor, title, content);

            // then
            assertThat(createdNote.getAuthor()).isNotNull();
            assertThat(createdNote.getAuthor().getId()).isEqualTo(1L);
            assertThat(createdNote.getAuthor().getUsername()).isEqualTo("홍길동");
            assertThat(createdNote.getAuthor().getEmail()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("성공: null 값으로도 생성할 수 있다")
        void create_NullValues_Success() {
            // given & when
            HandoverNote createdNote = HandoverNote.create(null, null, null);

            // then
            assertThat(createdNote).isNotNull();
            assertThat(createdNote.getAuthor()).isNull();
            assertThat(createdNote.getTitle()).isNull();
            assertThat(createdNote.getContent()).isNull();
        }

        @Test
        @DisplayName("성공: 빈 문자열로도 생성할 수 있다")
        void create_EmptyStrings_Success() {
            // given
            String emptyTitle = "";
            String emptyContent = "";

            // when
            HandoverNote createdNote = HandoverNote.create(mockAuthor, emptyTitle, emptyContent);

            // then
            assertThat(createdNote).isNotNull();
            assertThat(createdNote.getTitle()).isEmpty();
            assertThat(createdNote.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("필드 검증")
    class FieldValidation {

        @Test
        @DisplayName("성공: author 필드가 올바르게 저장된다")
        void author_StoredCorrectly() {
            // given
            User expectedAuthor = mockAuthor;

            // when
            User actualAuthor = handoverNote.getAuthor();

            // then
            assertThat(actualAuthor).isEqualTo(expectedAuthor);
        }

        @Test
        @DisplayName("성공: title 필드가 올바르게 저장된다")
        void title_StoredCorrectly() {
            // given
            String expectedTitle = "휴가 인수인계";

            // when
            String actualTitle = handoverNote.getTitle();

            // then
            assertThat(actualTitle).isEqualTo(expectedTitle);
        }

        @Test
        @DisplayName("성공: content 필드가 올바르게 저장된다")
        void content_StoredCorrectly() {
            // given
            String expectedContent = "휴가 중 처리해야 할 업무 안내입니다.";

            // when
            String actualContent = handoverNote.getContent();

            // then
            assertThat(actualContent).isEqualTo(expectedContent);
        }

        @Test
        @DisplayName("성공: id 필드는 초기값이 null이다")
        void id_InitiallyNull() {
            // when
            Long id = handoverNote.getId();

            // then
            assertThat(id).isNull();
        }

        @Test
        @DisplayName("성공: ReflectionTestUtils로 id를 설정할 수 있다")
        void id_CanBeSetWithReflection() {
            // given
            Long expectedId = 100L;

            // when
            ReflectionTestUtils.setField(handoverNote, "id", expectedId);
            Long actualId = handoverNote.getId();

            // then
            assertThat(actualId).isEqualTo(expectedId);
        }
    }

    @Nested
    @DisplayName("인수인계 노트 수정")
    class Update {

        @Test
        @DisplayName("성공: 제목과 내용을 수정한다")
        void update_Success() {
            // given
            String newTitle = "수정된 인수인계 제목";
            String newContent = "수정된 인수인계 내용입니다.";

            // when
            handoverNote.update(newTitle, newContent);

            // then
            assertThat(handoverNote.getTitle()).isEqualTo(newTitle);
            assertThat(handoverNote.getContent()).isEqualTo(newContent);
        }

        @Test
        @DisplayName("성공: 제목만 수정한다")
        void update_TitleOnly() {
            // given
            String originalContent = handoverNote.getContent();
            String newTitle = "새로운 제목";

            // when
            handoverNote.update(newTitle, originalContent);

            // then
            assertThat(handoverNote.getTitle()).isEqualTo(newTitle);
            assertThat(handoverNote.getContent()).isEqualTo(originalContent);
        }

        @Test
        @DisplayName("성공: 내용만 수정한다")
        void update_ContentOnly() {
            // given
            String originalTitle = handoverNote.getTitle();
            String newContent = "새로운 내용입니다.";

            // when
            handoverNote.update(originalTitle, newContent);

            // then
            assertThat(handoverNote.getTitle()).isEqualTo(originalTitle);
            assertThat(handoverNote.getContent()).isEqualTo(newContent);
        }

        @Test
        @DisplayName("성공: 빈 문자열로 수정할 수 있다")
        void update_EmptyStrings() {
            // given
            String emptyTitle = "";
            String emptyContent = "";

            // when
            handoverNote.update(emptyTitle, emptyContent);

            // then
            assertThat(handoverNote.getTitle()).isEmpty();
            assertThat(handoverNote.getContent()).isEmpty();
        }

        @Test
        @DisplayName("성공: null로 수정할 수 있다")
        void update_NullValues() {
            // when
            handoverNote.update(null, null);

            // then
            assertThat(handoverNote.getTitle()).isNull();
            assertThat(handoverNote.getContent()).isNull();
        }

        @Test
        @DisplayName("성공: 작성자는 수정 후에도 변경되지 않는다")
        void update_AuthorUnchanged() {
            // given
            User originalAuthor = handoverNote.getAuthor();

            // when
            handoverNote.update("새 제목", "새 내용");

            // then
            assertThat(handoverNote.getAuthor()).isEqualTo(originalAuthor);
        }
    }

    @Nested
    @DisplayName("다양한 제목 형식")
    class VariousTitleFormats {

        @Test
        @DisplayName("성공: 한글 제목을 저장한다")
        void koreanTitle_Success() {
            // given
            String koreanTitle = "긴급 업무 인수인계";

            // when
            HandoverNote note = HandoverNote.create(mockAuthor, koreanTitle, content);

            // then
            assertThat(note.getTitle()).isEqualTo(koreanTitle);
        }

        @Test
        @DisplayName("성공: 영문 제목을 저장한다")
        void englishTitle_Success() {
            // given
            String englishTitle = "Vacation Handover";

            // when
            HandoverNote note = HandoverNote.create(mockAuthor, englishTitle, content);

            // then
            assertThat(note.getTitle()).isEqualTo(englishTitle);
        }

        @Test
        @DisplayName("성공: 특수문자가 포함된 제목을 저장한다")
        void specialCharacterTitle_Success() {
            // given
            String specialTitle = "[긴급] 휴가 인수인계 (2025.01.01~01.03)";

            // when
            HandoverNote note = HandoverNote.create(mockAuthor, specialTitle, content);

            // then
            assertThat(note.getTitle()).isEqualTo(specialTitle);
            assertThat(note.getTitle()).contains("[긴급]");
            assertThat(note.getTitle()).contains("~");
        }

        @Test
        @DisplayName("성공: 긴 제목을 저장한다")
        void longTitle_Success() {
            // given
            String longTitle = "이것은 매우 긴 인수인계 제목입니다. ".repeat(5);

            // when
            HandoverNote note = HandoverNote.create(mockAuthor, longTitle, content);

            // then
            assertThat(note.getTitle()).isEqualTo(longTitle);
            assertThat(note.getTitle().length()).isGreaterThan(100);
        }
    }

    @Nested
    @DisplayName("다양한 내용 형식")
    class VariousContentFormats {

        @Test
        @DisplayName("성공: 한글 내용을 저장한다")
        void koreanContent_Success() {
            // given
            String koreanContent = "1. 프로젝트 A 진행 현황\n2. 고객사 미팅 일정\n3. 긴급 이슈 대응";

            // when
            HandoverNote note = HandoverNote.create(mockAuthor, title, koreanContent);

            // then
            assertThat(note.getContent()).isEqualTo(koreanContent);
            assertThat(note.getContent()).contains("프로젝트 A");
        }

        @Test
        @DisplayName("성공: 줄바꿈이 포함된 내용을 저장한다")
        void multilineContent_Success() {
            // given
            String multilineContent = """
                    1. 첫 번째 업무
                    2. 두 번째 업무
                    3. 세 번째 업무
                    """;

            // when
            HandoverNote note = HandoverNote.create(mockAuthor, title, multilineContent);

            // then
            assertThat(note.getContent()).isEqualTo(multilineContent);
            assertThat(note.getContent()).contains("\n");
        }

        @Test
        @DisplayName("성공: 긴 내용을 저장한다")
        void longContent_Success() {
            // given
            String longContent = "인수인계 상세 내용입니다. ".repeat(50);

            // when
            HandoverNote note = HandoverNote.create(mockAuthor, title, longContent);

            // then
            assertThat(note.getContent()).isEqualTo(longContent);
            assertThat(note.getContent().length()).isGreaterThan(500);
        }
    }

    @Nested
    @DisplayName("BaseEntity 상속 검증")
    class BaseEntityInheritance {

        @Test
        @DisplayName("성공: createdAt 필드를 ReflectionTestUtils로 설정할 수 있다")
        void createdAt_CanBeSetWithReflection() {
            // given
            LocalDateTime expectedCreatedAt = LocalDateTime.of(2025, 1, 1, 10, 0, 0);

            // when
            ReflectionTestUtils.setField(handoverNote, "createdAt", expectedCreatedAt);
            LocalDateTime actualCreatedAt = handoverNote.getCreatedAt();

            // then
            assertThat(actualCreatedAt).isEqualTo(expectedCreatedAt);
        }

        @Test
        @DisplayName("성공: updatedAt 필드를 ReflectionTestUtils로 설정할 수 있다")
        void updatedAt_CanBeSetWithReflection() {
            // given
            LocalDateTime expectedUpdatedAt = LocalDateTime.of(2025, 1, 2, 15, 30, 0);

            // when
            ReflectionTestUtils.setField(handoverNote, "updatedAt", expectedUpdatedAt);
            LocalDateTime actualUpdatedAt = handoverNote.getUpdatedAt();

            // then
            assertThat(actualUpdatedAt).isEqualTo(expectedUpdatedAt);
        }

        @Test
        @DisplayName("성공: createdAt과 updatedAt 초기값은 null이다")
        void timestamps_InitiallyNull() {
            // when
            LocalDateTime createdAt = handoverNote.getCreatedAt();
            LocalDateTime updatedAt = handoverNote.getUpdatedAt();

            // then
            assertThat(createdAt).isNull();
            assertThat(updatedAt).isNull();
        }
    }

    @Nested
    @DisplayName("복합 시나리오")
    class ComplexScenarios {

        @Test
        @DisplayName("성공: 인수인계 노트 생성 후 수정")
        void createAndUpdate_Success() {
            // given
            String originalTitle = "원본 제목";
            String originalContent = "원본 내용";
            HandoverNote note = HandoverNote.create(mockAuthor, originalTitle, originalContent);

            // when
            String newTitle = "수정된 제목";
            String newContent = "수정된 내용";
            note.update(newTitle, newContent);

            // then
            assertThat(note.getTitle()).isEqualTo(newTitle);
            assertThat(note.getContent()).isEqualTo(newContent);
            assertThat(note.getAuthor()).isEqualTo(mockAuthor);
        }

        @Test
        @DisplayName("성공: 여러 번 수정해도 정상 동작한다")
        void multipleUpdates_Success() {
            // given
            HandoverNote note = HandoverNote.create(mockAuthor, "제목1", "내용1");

            // when
            note.update("제목2", "내용2");
            note.update("제목3", "내용3");
            note.update("최종 제목", "최종 내용");

            // then
            assertThat(note.getTitle()).isEqualTo("최종 제목");
            assertThat(note.getContent()).isEqualTo("최종 내용");
        }

        @Test
        @DisplayName("성공: 다른 작성자로 여러 인수인계 노트 생성")
        void multipleNotesWithDifferentAuthors() {
            // given
            User author1 = new User("author1@example.com", UserRole.ROLE_USER);
            ReflectionTestUtils.setField(author1, "id", 1L);
            ReflectionTestUtils.setField(author1, "username", "작성자1");

            User author2 = new User("author2@example.com", UserRole.ROLE_USER);
            ReflectionTestUtils.setField(author2, "id", 2L);
            ReflectionTestUtils.setField(author2, "username", "작성자2");

            // when
            HandoverNote note1 = HandoverNote.create(author1, "제목1", "내용1");
            HandoverNote note2 = HandoverNote.create(author2, "제목2", "내용2");

            // then
            assertThat(note1.getAuthor().getUsername()).isEqualTo("작성자1");
            assertThat(note2.getAuthor().getUsername()).isEqualTo("작성자2");
            assertThat(note1.getAuthor()).isNotEqualTo(note2.getAuthor());
        }
    }
}
