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

@DisplayName("HandoverRecipient 엔티티 테스트")
class HandoverRecipientTest {

    private User mockAuthor;
    private User mockRecipient;
    private HandoverNote mockHandoverNote;
    private HandoverRecipient handoverRecipient;

    @BeforeEach
    void setUp() {
        // Mock Author 생성
        mockAuthor = new User("author@example.com", UserRole.ROLE_USER);
        ReflectionTestUtils.setField(mockAuthor, "id", 1L);
        ReflectionTestUtils.setField(mockAuthor, "username", "홍길동");
        ReflectionTestUtils.setField(mockAuthor, "employeeNo", "EMP001");

        // Mock Recipient 생성
        mockRecipient = new User("recipient@example.com", UserRole.ROLE_USER);
        ReflectionTestUtils.setField(mockRecipient, "id", 2L);
        ReflectionTestUtils.setField(mockRecipient, "username", "김철수");
        ReflectionTestUtils.setField(mockRecipient, "employeeNo", "EMP002");

        // Mock HandoverNote 생성
        mockHandoverNote = HandoverNote.create(mockAuthor, "휴가 인수인계", "휴가 중 처리해야 할 업무 안내입니다.");
        ReflectionTestUtils.setField(mockHandoverNote, "id", 1L);

        // HandoverRecipient 생성
        handoverRecipient = HandoverRecipient.create(mockHandoverNote, mockRecipient);
    }

    @Nested
    @DisplayName("인수인계 수신자 생성")
    class Create {

        @Test
        @DisplayName("성공: create 정적 메서드로 인수인계 수신자를 생성한다")
        void create_Success() {
            // given
            User newRecipient = new User("new@example.com", UserRole.ROLE_USER);
            ReflectionTestUtils.setField(newRecipient, "id", 10L);
            ReflectionTestUtils.setField(newRecipient, "username", "이영희");

            // when
            HandoverRecipient createdRecipient = HandoverRecipient.create(mockHandoverNote, newRecipient);

            // then
            assertThat(createdRecipient).isNotNull();
            assertThat(createdRecipient.getHandoverNote()).isEqualTo(mockHandoverNote);
            assertThat(createdRecipient.getRecipient()).isEqualTo(newRecipient);
        }

        @Test
        @DisplayName("성공: 인수인계 노트 정보가 올바르게 저장된다")
        void create_HandoverNoteStoredCorrectly() {
            // when
            HandoverRecipient createdRecipient = HandoverRecipient.create(mockHandoverNote, mockRecipient);

            // then
            assertThat(createdRecipient.getHandoverNote()).isNotNull();
            assertThat(createdRecipient.getHandoverNote().getId()).isEqualTo(1L);
            assertThat(createdRecipient.getHandoverNote().getTitle()).isEqualTo("휴가 인수인계");
            assertThat(createdRecipient.getHandoverNote().getAuthor()).isEqualTo(mockAuthor);
        }

        @Test
        @DisplayName("성공: 수신자 정보가 올바르게 저장된다")
        void create_RecipientStoredCorrectly() {
            // when
            HandoverRecipient createdRecipient = HandoverRecipient.create(mockHandoverNote, mockRecipient);

            // then
            assertThat(createdRecipient.getRecipient()).isNotNull();
            assertThat(createdRecipient.getRecipient().getId()).isEqualTo(2L);
            assertThat(createdRecipient.getRecipient().getUsername()).isEqualTo("김철수");
            assertThat(createdRecipient.getRecipient().getEmail()).isEqualTo("recipient@example.com");
        }

        @Test
        @DisplayName("성공: null 값으로도 생성할 수 있다")
        void create_NullValues_Success() {
            // given & when
            HandoverRecipient createdRecipient = HandoverRecipient.create(null, null);

            // then
            assertThat(createdRecipient).isNotNull();
            assertThat(createdRecipient.getHandoverNote()).isNull();
            assertThat(createdRecipient.getRecipient()).isNull();
        }
    }

    @Nested
    @DisplayName("필드 검증")
    class FieldValidation {

        @Test
        @DisplayName("성공: handoverNote 필드가 올바르게 저장된다")
        void handoverNote_StoredCorrectly() {
            // given
            HandoverNote expectedNote = mockHandoverNote;

            // when
            HandoverNote actualNote = handoverRecipient.getHandoverNote();

            // then
            assertThat(actualNote).isEqualTo(expectedNote);
        }

        @Test
        @DisplayName("성공: recipient 필드가 올바르게 저장된다")
        void recipient_StoredCorrectly() {
            // given
            User expectedRecipient = mockRecipient;

            // when
            User actualRecipient = handoverRecipient.getRecipient();

            // then
            assertThat(actualRecipient).isEqualTo(expectedRecipient);
        }

        @Test
        @DisplayName("성공: id 필드는 초기값이 null이다")
        void id_InitiallyNull() {
            // when
            Long id = handoverRecipient.getId();

            // then
            assertThat(id).isNull();
        }

        @Test
        @DisplayName("성공: ReflectionTestUtils로 id를 설정할 수 있다")
        void id_CanBeSetWithReflection() {
            // given
            Long expectedId = 100L;

            // when
            ReflectionTestUtils.setField(handoverRecipient, "id", expectedId);
            Long actualId = handoverRecipient.getId();

            // then
            assertThat(actualId).isEqualTo(expectedId);
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
            ReflectionTestUtils.setField(handoverRecipient, "createdAt", expectedCreatedAt);
            LocalDateTime actualCreatedAt = handoverRecipient.getCreatedAt();

            // then
            assertThat(actualCreatedAt).isEqualTo(expectedCreatedAt);
        }

        @Test
        @DisplayName("성공: updatedAt 필드를 ReflectionTestUtils로 설정할 수 있다")
        void updatedAt_CanBeSetWithReflection() {
            // given
            LocalDateTime expectedUpdatedAt = LocalDateTime.of(2025, 1, 2, 15, 30, 0);

            // when
            ReflectionTestUtils.setField(handoverRecipient, "updatedAt", expectedUpdatedAt);
            LocalDateTime actualUpdatedAt = handoverRecipient.getUpdatedAt();

            // then
            assertThat(actualUpdatedAt).isEqualTo(expectedUpdatedAt);
        }

        @Test
        @DisplayName("성공: createdAt과 updatedAt 초기값은 null이다")
        void timestamps_InitiallyNull() {
            // when
            LocalDateTime createdAt = handoverRecipient.getCreatedAt();
            LocalDateTime updatedAt = handoverRecipient.getUpdatedAt();

            // then
            assertThat(createdAt).isNull();
            assertThat(updatedAt).isNull();
        }
    }

    @Nested
    @DisplayName("복합 시나리오")
    class ComplexScenarios {

        @Test
        @DisplayName("성공: 같은 인수인계에 여러 수신자 생성")
        void multipleRecipientsForSameNote() {
            // given
            User recipient1 = new User("r1@example.com", UserRole.ROLE_USER);
            ReflectionTestUtils.setField(recipient1, "id", 10L);
            ReflectionTestUtils.setField(recipient1, "username", "수신자1");

            User recipient2 = new User("r2@example.com", UserRole.ROLE_USER);
            ReflectionTestUtils.setField(recipient2, "id", 11L);
            ReflectionTestUtils.setField(recipient2, "username", "수신자2");

            User recipient3 = new User("r3@example.com", UserRole.ROLE_USER);
            ReflectionTestUtils.setField(recipient3, "id", 12L);
            ReflectionTestUtils.setField(recipient3, "username", "수신자3");

            // when
            HandoverRecipient hr1 = HandoverRecipient.create(mockHandoverNote, recipient1);
            HandoverRecipient hr2 = HandoverRecipient.create(mockHandoverNote, recipient2);
            HandoverRecipient hr3 = HandoverRecipient.create(mockHandoverNote, recipient3);

            // then
            assertThat(hr1.getHandoverNote()).isEqualTo(mockHandoverNote);
            assertThat(hr2.getHandoverNote()).isEqualTo(mockHandoverNote);
            assertThat(hr3.getHandoverNote()).isEqualTo(mockHandoverNote);

            assertThat(hr1.getRecipient().getUsername()).isEqualTo("수신자1");
            assertThat(hr2.getRecipient().getUsername()).isEqualTo("수신자2");
            assertThat(hr3.getRecipient().getUsername()).isEqualTo("수신자3");
        }

        @Test
        @DisplayName("성공: 같은 수신자가 여러 인수인계를 받을 수 있다")
        void sameRecipientMultipleNotes() {
            // given
            HandoverNote note1 = HandoverNote.create(mockAuthor, "인수인계1", "내용1");
            ReflectionTestUtils.setField(note1, "id", 1L);

            HandoverNote note2 = HandoverNote.create(mockAuthor, "인수인계2", "내용2");
            ReflectionTestUtils.setField(note2, "id", 2L);

            HandoverNote note3 = HandoverNote.create(mockAuthor, "인수인계3", "내용3");
            ReflectionTestUtils.setField(note3, "id", 3L);

            // when
            HandoverRecipient hr1 = HandoverRecipient.create(note1, mockRecipient);
            HandoverRecipient hr2 = HandoverRecipient.create(note2, mockRecipient);
            HandoverRecipient hr3 = HandoverRecipient.create(note3, mockRecipient);

            // then
            assertThat(hr1.getRecipient()).isEqualTo(mockRecipient);
            assertThat(hr2.getRecipient()).isEqualTo(mockRecipient);
            assertThat(hr3.getRecipient()).isEqualTo(mockRecipient);

            assertThat(hr1.getHandoverNote().getTitle()).isEqualTo("인수인계1");
            assertThat(hr2.getHandoverNote().getTitle()).isEqualTo("인수인계2");
            assertThat(hr3.getHandoverNote().getTitle()).isEqualTo("인수인계3");
        }

        @Test
        @DisplayName("성공: 인수인계 노트와 수신자 관계 검증")
        void noteAndRecipientRelationship() {
            // given
            User author = new User("author@test.com", UserRole.ROLE_USER);
            ReflectionTestUtils.setField(author, "id", 100L);
            ReflectionTestUtils.setField(author, "username", "작성자");

            User recipient = new User("recipient@test.com", UserRole.ROLE_USER);
            ReflectionTestUtils.setField(recipient, "id", 200L);
            ReflectionTestUtils.setField(recipient, "username", "수신자");

            HandoverNote note = HandoverNote.create(author, "테스트 인수인계", "테스트 내용");
            ReflectionTestUtils.setField(note, "id", 50L);

            // when
            HandoverRecipient hr = HandoverRecipient.create(note, recipient);

            // then
            // 인수인계 노트 검증
            assertThat(hr.getHandoverNote().getId()).isEqualTo(50L);
            assertThat(hr.getHandoverNote().getAuthor().getId()).isEqualTo(100L);
            assertThat(hr.getHandoverNote().getAuthor().getUsername()).isEqualTo("작성자");

            // 수신자 검증
            assertThat(hr.getRecipient().getId()).isEqualTo(200L);
            assertThat(hr.getRecipient().getUsername()).isEqualTo("수신자");

            // 작성자와 수신자가 다름
            assertThat(hr.getHandoverNote().getAuthor()).isNotEqualTo(hr.getRecipient());
        }

        @Test
        @DisplayName("성공: 작성자와 수신자가 같은 경우도 생성 가능")
        void authorAndRecipientSame() {
            // given
            HandoverNote note = HandoverNote.create(mockAuthor, "자기 자신에게", "메모 용도");
            ReflectionTestUtils.setField(note, "id", 99L);

            // when
            HandoverRecipient hr = HandoverRecipient.create(note, mockAuthor);

            // then
            assertThat(hr.getHandoverNote().getAuthor()).isEqualTo(hr.getRecipient());
            assertThat(hr.getHandoverNote().getAuthor().getId()).isEqualTo(1L);
            assertThat(hr.getRecipient().getId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("다양한 사용자 역할")
    class VariousUserRoles {

        @Test
        @DisplayName("성공: ROLE_USER 권한의 수신자 생성")
        void roleUser_Success() {
            // given
            User userRecipient = new User("user@example.com", UserRole.ROLE_USER);
            ReflectionTestUtils.setField(userRecipient, "id", 10L);

            // when
            HandoverRecipient hr = HandoverRecipient.create(mockHandoverNote, userRecipient);

            // then
            assertThat(hr.getRecipient().getRole()).isEqualTo(UserRole.ROLE_USER);
        }

        @Test
        @DisplayName("성공: ROLE_ADMIN 권한의 수신자 생성")
        void roleAdmin_Success() {
            // given
            User adminRecipient = new User("admin@example.com", UserRole.ROLE_ADMIN);
            ReflectionTestUtils.setField(adminRecipient, "id", 20L);

            // when
            HandoverRecipient hr = HandoverRecipient.create(mockHandoverNote, adminRecipient);

            // then
            assertThat(hr.getRecipient().getRole()).isEqualTo(UserRole.ROLE_ADMIN);
        }
    }
}
