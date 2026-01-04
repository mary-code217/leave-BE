package com.hoho.leave.domain.handover.repository;

import com.hoho.leave.domain.handover.entity.HandoverNote;
import com.hoho.leave.domain.handover.entity.HandoverRecipient;
import com.hoho.leave.domain.user.entity.User;
import com.hoho.leave.domain.user.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HandoverRecipientRepository 테스트")
class HandoverRecipientRepositoryTest {

    @Mock
    private HandoverRecipientRepository handoverRecipientRepository;

    private User mockAuthor;
    private User mockRecipient1;
    private User mockRecipient2;
    private User mockRecipient3;
    private HandoverNote mockHandoverNote;
    private HandoverNote mockHandoverNote2;
    private HandoverRecipient mockHandoverRecipient;
    private HandoverRecipient mockHandoverRecipient2;

    @BeforeEach
    void setUp() {
        // Mock Author 생성
        mockAuthor = new User("author@example.com", UserRole.ROLE_USER);
        ReflectionTestUtils.setField(mockAuthor, "id", 1L);
        ReflectionTestUtils.setField(mockAuthor, "username", "홍길동");
        ReflectionTestUtils.setField(mockAuthor, "employeeNo", "EMP001");

        // Mock Recipients 생성
        mockRecipient1 = new User("recipient1@example.com", UserRole.ROLE_USER);
        ReflectionTestUtils.setField(mockRecipient1, "id", 2L);
        ReflectionTestUtils.setField(mockRecipient1, "username", "김철수");
        ReflectionTestUtils.setField(mockRecipient1, "employeeNo", "EMP002");

        mockRecipient2 = new User("recipient2@example.com", UserRole.ROLE_USER);
        ReflectionTestUtils.setField(mockRecipient2, "id", 3L);
        ReflectionTestUtils.setField(mockRecipient2, "username", "이영희");
        ReflectionTestUtils.setField(mockRecipient2, "employeeNo", "EMP003");

        mockRecipient3 = new User("recipient3@example.com", UserRole.ROLE_USER);
        ReflectionTestUtils.setField(mockRecipient3, "id", 4L);
        ReflectionTestUtils.setField(mockRecipient3, "username", "박민수");
        ReflectionTestUtils.setField(mockRecipient3, "employeeNo", "EMP004");

        // Mock HandoverNote 생성
        mockHandoverNote = HandoverNote.create(mockAuthor, "휴가 인수인계", "휴가 중 처리해야 할 업무 안내입니다.");
        ReflectionTestUtils.setField(mockHandoverNote, "id", 1L);
        ReflectionTestUtils.setField(mockHandoverNote, "createdAt", LocalDateTime.now());

        mockHandoverNote2 = HandoverNote.create(mockAuthor, "프로젝트 인수인계", "프로젝트 진행 상황 공유");
        ReflectionTestUtils.setField(mockHandoverNote2, "id", 2L);
        ReflectionTestUtils.setField(mockHandoverNote2, "createdAt", LocalDateTime.now().minusHours(1));

        // Mock HandoverRecipient 생성
        mockHandoverRecipient = HandoverRecipient.create(mockHandoverNote, mockRecipient1);
        ReflectionTestUtils.setField(mockHandoverRecipient, "id", 1L);
        ReflectionTestUtils.setField(mockHandoverRecipient, "createdAt", LocalDateTime.now());

        mockHandoverRecipient2 = HandoverRecipient.create(mockHandoverNote, mockRecipient2);
        ReflectionTestUtils.setField(mockHandoverRecipient2, "id", 2L);
        ReflectionTestUtils.setField(mockHandoverRecipient2, "createdAt", LocalDateTime.now());
    }

    @Nested
    @DisplayName("인수인계 수신자 저장")
    class SaveHandoverRecipient {

        @Test
        @DisplayName("성공: 새로운 인수인계 수신자를 저장한다")
        void save_Success() {
            // given
            HandoverRecipient newRecipient = HandoverRecipient.create(mockHandoverNote, mockRecipient3);

            HandoverRecipient savedRecipient = HandoverRecipient.create(mockHandoverNote, mockRecipient3);
            ReflectionTestUtils.setField(savedRecipient, "id", 10L);
            ReflectionTestUtils.setField(savedRecipient, "createdAt", LocalDateTime.now());

            given(handoverRecipientRepository.save(any(HandoverRecipient.class))).willReturn(savedRecipient);

            // when
            HandoverRecipient result = handoverRecipientRepository.save(newRecipient);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(10L);
            assertThat(result.getHandoverNote()).isEqualTo(mockHandoverNote);
            assertThat(result.getRecipient()).isEqualTo(mockRecipient3);
            verify(handoverRecipientRepository).save(newRecipient);
        }

        @Test
        @DisplayName("성공: 여러 수신자를 저장한다")
        void saveAll_Success() {
            // given
            List<HandoverRecipient> recipients = List.of(mockHandoverRecipient, mockHandoverRecipient2);

            given(handoverRecipientRepository.saveAll(anyList())).willReturn(recipients);

            // when
            List<HandoverRecipient> result = handoverRecipientRepository.saveAll(recipients);

            // then
            assertThat(result).hasSize(2);
            verify(handoverRecipientRepository).saveAll(recipients);
        }
    }

    @Nested
    @DisplayName("수신자 ID로 인수인계 목록 조회")
    class FindByRecipientId {

        @Test
        @DisplayName("성공: 수신자 ID로 수신한 인수인계 목록을 페이징하여 조회한다")
        void findByRecipientId_Success() {
            // given
            Long recipientId = 2L;
            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt")));
            List<HandoverRecipient> recipients = List.of(mockHandoverRecipient);
            Page<HandoverRecipient> recipientPage = new PageImpl<>(recipients, pageRequest, 1);

            given(handoverRecipientRepository.findByRecipientId(eq(recipientId), any(PageRequest.class)))
                    .willReturn(recipientPage);

            // when
            Page<HandoverRecipient> result = handoverRecipientRepository.findByRecipientId(recipientId, pageRequest);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getRecipient().getId()).isEqualTo(recipientId);
            verify(handoverRecipientRepository).findByRecipientId(recipientId, pageRequest);
        }

        @Test
        @DisplayName("성공: 수신 인수인계가 없는 경우 빈 페이지 반환")
        void findByRecipientId_EmptyResult() {
            // given
            Long recipientId = 999L;
            PageRequest pageRequest = PageRequest.of(0, 10);
            Page<HandoverRecipient> emptyPage = new PageImpl<>(List.of(), pageRequest, 0);

            given(handoverRecipientRepository.findByRecipientId(eq(recipientId), any(PageRequest.class)))
                    .willReturn(emptyPage);

            // when
            Page<HandoverRecipient> result = handoverRecipientRepository.findByRecipientId(recipientId, pageRequest);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("성공: 페이지네이션이 올바르게 동작한다")
        void findByRecipientId_Pagination() {
            // given
            Long recipientId = 2L;

            // 15개의 인수인계 수신 생성
            List<HandoverRecipient> allRecipients = new ArrayList<>();
            for (int i = 0; i < 15; i++) {
                HandoverNote note = HandoverNote.create(mockAuthor, "인수인계 " + i, "내용 " + i);
                ReflectionTestUtils.setField(note, "id", (long) (i + 10));
                ReflectionTestUtils.setField(note, "createdAt", LocalDateTime.now().minusMinutes(i));

                HandoverRecipient hr = HandoverRecipient.create(note, mockRecipient1);
                ReflectionTestUtils.setField(hr, "id", (long) (i + 1));
                allRecipients.add(hr);
            }

            PageRequest pageRequest1 = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("createdAt")));
            PageRequest pageRequest2 = PageRequest.of(1, 5, Sort.by(Sort.Order.desc("createdAt")));
            PageRequest pageRequest3 = PageRequest.of(2, 5, Sort.by(Sort.Order.desc("createdAt")));

            Page<HandoverRecipient> page1 = new PageImpl<>(allRecipients.subList(0, 5), pageRequest1, 15);
            Page<HandoverRecipient> page2 = new PageImpl<>(allRecipients.subList(5, 10), pageRequest2, 15);
            Page<HandoverRecipient> page3 = new PageImpl<>(allRecipients.subList(10, 15), pageRequest3, 15);

            given(handoverRecipientRepository.findByRecipientId(eq(recipientId), eq(pageRequest1))).willReturn(page1);
            given(handoverRecipientRepository.findByRecipientId(eq(recipientId), eq(pageRequest2))).willReturn(page2);
            given(handoverRecipientRepository.findByRecipientId(eq(recipientId), eq(pageRequest3))).willReturn(page3);

            // when
            Page<HandoverRecipient> result1 = handoverRecipientRepository.findByRecipientId(recipientId, pageRequest1);
            Page<HandoverRecipient> result2 = handoverRecipientRepository.findByRecipientId(recipientId, pageRequest2);
            Page<HandoverRecipient> result3 = handoverRecipientRepository.findByRecipientId(recipientId, pageRequest3);

            // then
            assertThat(result1.getContent()).hasSize(5);
            assertThat(result2.getContent()).hasSize(5);
            assertThat(result3.getContent()).hasSize(5);
            assertThat(result1.getTotalElements()).isEqualTo(15);
            assertThat(result1.getTotalPages()).isEqualTo(3);
            assertThat(result1.isFirst()).isTrue();
            assertThat(result3.isLast()).isTrue();
        }
    }

    @Nested
    @DisplayName("인수인계 노트 ID로 모든 수신자 조회")
    class FindAllByHandoverNoteId {

        @Test
        @DisplayName("성공: 인수인계 노트 ID로 모든 수신자를 조회한다")
        void findAllByHandoverNoteId_Success() {
            // given
            Long handoverNoteId = 1L;
            List<HandoverRecipient> recipients = List.of(mockHandoverRecipient, mockHandoverRecipient2);

            given(handoverRecipientRepository.findAllByHandoverNoteId(handoverNoteId))
                    .willReturn(recipients);

            // when
            List<HandoverRecipient> result = handoverRecipientRepository.findAllByHandoverNoteId(handoverNoteId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(hr -> hr.getHandoverNote().getId().equals(handoverNoteId));
            verify(handoverRecipientRepository).findAllByHandoverNoteId(handoverNoteId);
        }

        @Test
        @DisplayName("성공: 수신자가 없는 인수인계 노트는 빈 리스트 반환")
        void findAllByHandoverNoteId_EmptyResult() {
            // given
            Long handoverNoteId = 999L;

            given(handoverRecipientRepository.findAllByHandoverNoteId(handoverNoteId))
                    .willReturn(List.of());

            // when
            List<HandoverRecipient> result = handoverRecipientRepository.findAllByHandoverNoteId(handoverNoteId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("성공: 수신자 정보가 완전히 로드된다")
        void findAllByHandoverNoteId_RecipientFullyLoaded() {
            // given
            Long handoverNoteId = 1L;
            List<HandoverRecipient> recipients = List.of(mockHandoverRecipient, mockHandoverRecipient2);

            given(handoverRecipientRepository.findAllByHandoverNoteId(handoverNoteId))
                    .willReturn(recipients);

            // when
            List<HandoverRecipient> result = handoverRecipientRepository.findAllByHandoverNoteId(handoverNoteId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getRecipient().getUsername()).isEqualTo("김철수");
            assertThat(result.get(1).getRecipient().getUsername()).isEqualTo("이영희");
        }
    }

    @Nested
    @DisplayName("인수인계 노트 ID 목록으로 수신자 사용자명 조회")
    class FindRecipientUsernamesByNoteIds {

        @Test
        @DisplayName("성공: 여러 인수인계 노트의 수신자 사용자명을 조회한다")
        void findRecipientUsernamesByNoteIds_Success() {
            // given
            Collection<Long> noteIds = List.of(1L, 2L);

            // Mock RecipientUsernameRow 생성
            HandoverRecipientRepository.RecipientUsernameRow row1 = mock(HandoverRecipientRepository.RecipientUsernameRow.class);
            given(row1.getNoteId()).willReturn(1L);

            HandoverRecipientRepository.RecipientUsernameRow row2 = mock(HandoverRecipientRepository.RecipientUsernameRow.class);
            given(row2.getNoteId()).willReturn(1L);

            HandoverRecipientRepository.RecipientUsernameRow row3 = mock(HandoverRecipientRepository.RecipientUsernameRow.class);
            given(row3.getNoteId()).willReturn(2L);

            List<HandoverRecipientRepository.RecipientUsernameRow> rows = List.of(row1, row2, row3);

            given(handoverRecipientRepository.findRecipientUsernamesByNoteIds(noteIds))
                    .willReturn(rows);

            // when
            List<HandoverRecipientRepository.RecipientUsernameRow> result =
                    handoverRecipientRepository.findRecipientUsernamesByNoteIds(noteIds);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.stream().filter(r -> r.getNoteId().equals(1L)).count()).isEqualTo(2);
            assertThat(result.stream().filter(r -> r.getNoteId().equals(2L)).count()).isEqualTo(1);
            verify(handoverRecipientRepository).findRecipientUsernamesByNoteIds(noteIds);
        }

        @Test
        @DisplayName("성공: 존재하지 않는 노트 ID들은 빈 리스트 반환")
        void findRecipientUsernamesByNoteIds_EmptyResult() {
            // given
            Collection<Long> noteIds = List.of(999L, 1000L);

            given(handoverRecipientRepository.findRecipientUsernamesByNoteIds(noteIds))
                    .willReturn(List.of());

            // when
            List<HandoverRecipientRepository.RecipientUsernameRow> result =
                    handoverRecipientRepository.findRecipientUsernamesByNoteIds(noteIds);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("인수인계 노트의 수신자 이름 목록 조회")
    class FindRecipientNamesByHandoverNoteId {

        @Test
        @DisplayName("성공: 인수인계 노트의 수신자 이름 목록을 조회한다")
        void findRecipientNamesByHandoverNoteId_Success() {
            // given
            Long handoverNoteId = 1L;
            List<String> recipientNames = List.of("김철수", "이영희", "박민수");

            given(handoverRecipientRepository.findRecipientNamesByHandoverNoteId(handoverNoteId))
                    .willReturn(recipientNames);

            // when
            List<String> result = handoverRecipientRepository.findRecipientNamesByHandoverNoteId(handoverNoteId);

            // then
            assertThat(result).hasSize(3);
            assertThat(result).containsExactly("김철수", "이영희", "박민수");
            verify(handoverRecipientRepository).findRecipientNamesByHandoverNoteId(handoverNoteId);
        }

        @Test
        @DisplayName("성공: 수신자가 없는 경우 빈 리스트 반환")
        void findRecipientNamesByHandoverNoteId_EmptyResult() {
            // given
            Long handoverNoteId = 999L;

            given(handoverRecipientRepository.findRecipientNamesByHandoverNoteId(handoverNoteId))
                    .willReturn(List.of());

            // when
            List<String> result = handoverRecipientRepository.findRecipientNamesByHandoverNoteId(handoverNoteId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("성공: 단일 수신자 조회")
        void findRecipientNamesByHandoverNoteId_SingleRecipient() {
            // given
            Long handoverNoteId = 1L;
            List<String> recipientNames = List.of("김철수");

            given(handoverRecipientRepository.findRecipientNamesByHandoverNoteId(handoverNoteId))
                    .willReturn(recipientNames);

            // when
            List<String> result = handoverRecipientRepository.findRecipientNamesByHandoverNoteId(handoverNoteId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo("김철수");
        }
    }

    @Nested
    @DisplayName("인수인계 노트와 수신자 ID로 수신자 삭제")
    class DeleteByNoteIdAndRecipientIds {

        @Test
        @DisplayName("성공: 특정 수신자들을 삭제한다")
        void deleteByNoteIdAndRecipientIds_Success() {
            // given
            Long noteId = 1L;
            Collection<Long> recipientIds = Set.of(2L, 3L);

            doNothing().when(handoverRecipientRepository)
                    .deleteByNoteIdAndRecipientIds(noteId, recipientIds);

            // when
            handoverRecipientRepository.deleteByNoteIdAndRecipientIds(noteId, recipientIds);

            // then
            verify(handoverRecipientRepository, times(1))
                    .deleteByNoteIdAndRecipientIds(noteId, recipientIds);
        }

        @Test
        @DisplayName("성공: 빈 수신자 ID 목록으로 호출해도 예외 없이 처리")
        void deleteByNoteIdAndRecipientIds_EmptyRecipientIds() {
            // given
            Long noteId = 1L;
            Collection<Long> emptyRecipientIds = Set.of();

            doNothing().when(handoverRecipientRepository)
                    .deleteByNoteIdAndRecipientIds(noteId, emptyRecipientIds);

            // when
            handoverRecipientRepository.deleteByNoteIdAndRecipientIds(noteId, emptyRecipientIds);

            // then
            verify(handoverRecipientRepository).deleteByNoteIdAndRecipientIds(noteId, emptyRecipientIds);
        }
    }

    @Nested
    @DisplayName("인수인계 노트의 모든 수신자 삭제")
    class DeleteByHandoverNoteId {

        @Test
        @DisplayName("성공: 인수인계 노트의 모든 수신자를 삭제한다")
        void deleteByHandoverNoteId_Success() {
            // given
            Long handoverNoteId = 1L;

            doNothing().when(handoverRecipientRepository).deleteByHandoverNoteId(handoverNoteId);

            // when
            handoverRecipientRepository.deleteByHandoverNoteId(handoverNoteId);

            // then
            verify(handoverRecipientRepository, times(1)).deleteByHandoverNoteId(handoverNoteId);
        }

        @Test
        @DisplayName("성공: 존재하지 않는 인수인계 노트 삭제 시 예외 없이 처리")
        void deleteByHandoverNoteId_NonExistent() {
            // given
            Long nonExistentNoteId = 999L;

            doNothing().when(handoverRecipientRepository).deleteByHandoverNoteId(nonExistentNoteId);

            // when
            handoverRecipientRepository.deleteByHandoverNoteId(nonExistentNoteId);

            // then
            verify(handoverRecipientRepository).deleteByHandoverNoteId(nonExistentNoteId);
        }
    }

    @Nested
    @DisplayName("인수인계 수신자 개수 조회")
    class CountHandoverRecipients {

        @Test
        @DisplayName("성공: 전체 인수인계 수신자 개수를 조회한다")
        void count_ReturnsCorrectCount() {
            // given
            given(handoverRecipientRepository.count()).willReturn(100L);

            // when
            long count = handoverRecipientRepository.count();

            // then
            assertThat(count).isEqualTo(100L);
            verify(handoverRecipientRepository).count();
        }

        @Test
        @DisplayName("성공: 수신자가 없을 경우 0을 반환한다")
        void count_NoRecipients_ReturnsZero() {
            // given
            given(handoverRecipientRepository.count()).willReturn(0L);

            // when
            long count = handoverRecipientRepository.count();

            // then
            assertThat(count).isEqualTo(0L);
            verify(handoverRecipientRepository).count();
        }
    }

    @Nested
    @DisplayName("복합 시나리오")
    class ComplexScenarios {

        @Test
        @DisplayName("성공: 수신자 저장 후 조회")
        void saveAndFind_Success() {
            // given
            HandoverRecipient newRecipient = HandoverRecipient.create(mockHandoverNote, mockRecipient3);

            HandoverRecipient savedRecipient = HandoverRecipient.create(mockHandoverNote, mockRecipient3);
            ReflectionTestUtils.setField(savedRecipient, "id", 100L);

            given(handoverRecipientRepository.save(any(HandoverRecipient.class))).willReturn(savedRecipient);
            given(handoverRecipientRepository.findAllByHandoverNoteId(1L))
                    .willReturn(List.of(mockHandoverRecipient, mockHandoverRecipient2, savedRecipient));

            // when
            HandoverRecipient saved = handoverRecipientRepository.save(newRecipient);
            List<HandoverRecipient> allRecipients = handoverRecipientRepository.findAllByHandoverNoteId(1L);

            // then
            assertThat(allRecipients).hasSize(3);
            assertThat(allRecipients).contains(savedRecipient);
        }

        @Test
        @DisplayName("성공: 저장 후 삭제, 조회 시 삭제된 수신자 없음")
        void saveDeleteFind_Success() {
            // given
            Long handoverNoteId = 1L;

            // 처음에는 2명의 수신자
            given(handoverRecipientRepository.findAllByHandoverNoteId(handoverNoteId))
                    .willReturn(List.of(mockHandoverRecipient, mockHandoverRecipient2))
                    .willReturn(List.of(mockHandoverRecipient)); // 삭제 후

            doNothing().when(handoverRecipientRepository)
                    .deleteByNoteIdAndRecipientIds(handoverNoteId, Set.of(3L));

            // when
            List<HandoverRecipient> beforeDelete = handoverRecipientRepository.findAllByHandoverNoteId(handoverNoteId);
            handoverRecipientRepository.deleteByNoteIdAndRecipientIds(handoverNoteId, Set.of(3L));
            List<HandoverRecipient> afterDelete = handoverRecipientRepository.findAllByHandoverNoteId(handoverNoteId);

            // then
            assertThat(beforeDelete).hasSize(2);
            assertThat(afterDelete).hasSize(1);
            assertThat(afterDelete.get(0).getRecipient().getUsername()).isEqualTo("김철수");
        }

        @Test
        @DisplayName("성공: 인수인계 노트 삭제 시 모든 수신자 삭제")
        void deleteAllRecipientsWhenNoteDeleted() {
            // given
            Long handoverNoteId = 1L;

            given(handoverRecipientRepository.findAllByHandoverNoteId(handoverNoteId))
                    .willReturn(List.of(mockHandoverRecipient, mockHandoverRecipient2))
                    .willReturn(List.of()); // 삭제 후

            doNothing().when(handoverRecipientRepository).deleteByHandoverNoteId(handoverNoteId);

            // when
            List<HandoverRecipient> beforeDelete = handoverRecipientRepository.findAllByHandoverNoteId(handoverNoteId);
            handoverRecipientRepository.deleteByHandoverNoteId(handoverNoteId);
            List<HandoverRecipient> afterDelete = handoverRecipientRepository.findAllByHandoverNoteId(handoverNoteId);

            // then
            assertThat(beforeDelete).hasSize(2);
            assertThat(afterDelete).isEmpty();
        }

        @Test
        @DisplayName("성공: 한 사용자가 여러 인수인계를 수신한 경우 모두 조회")
        void multipleReceivedHandovers() {
            // given
            Long recipientId = 2L;
            PageRequest pageRequest = PageRequest.of(0, 10);

            HandoverRecipient hr1 = HandoverRecipient.create(mockHandoverNote, mockRecipient1);
            ReflectionTestUtils.setField(hr1, "id", 1L);

            HandoverRecipient hr2 = HandoverRecipient.create(mockHandoverNote2, mockRecipient1);
            ReflectionTestUtils.setField(hr2, "id", 2L);

            Page<HandoverRecipient> recipientPage = new PageImpl<>(List.of(hr1, hr2), pageRequest, 2);

            given(handoverRecipientRepository.findByRecipientId(eq(recipientId), any(PageRequest.class)))
                    .willReturn(recipientPage);

            // when
            Page<HandoverRecipient> result = handoverRecipientRepository.findByRecipientId(recipientId, pageRequest);

            // then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).getHandoverNote().getTitle()).isEqualTo("휴가 인수인계");
            assertThat(result.getContent().get(1).getHandoverNote().getTitle()).isEqualTo("프로젝트 인수인계");
        }
    }
}
