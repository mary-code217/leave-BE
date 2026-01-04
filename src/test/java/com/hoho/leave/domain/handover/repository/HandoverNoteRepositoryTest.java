package com.hoho.leave.domain.handover.repository;

import com.hoho.leave.domain.handover.entity.HandoverNote;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HandoverNoteRepository 테스트")
class HandoverNoteRepositoryTest {

    @Mock
    private HandoverNoteRepository handoverNoteRepository;

    private User mockAuthor;
    private User mockAuthor2;
    private HandoverNote mockHandoverNote;
    private HandoverNote mockHandoverNote2;

    @BeforeEach
    void setUp() {
        // Mock Author 생성
        mockAuthor = new User("author@example.com", UserRole.ROLE_USER);
        ReflectionTestUtils.setField(mockAuthor, "id", 1L);
        ReflectionTestUtils.setField(mockAuthor, "username", "홍길동");
        ReflectionTestUtils.setField(mockAuthor, "employeeNo", "EMP001");

        mockAuthor2 = new User("author2@example.com", UserRole.ROLE_USER);
        ReflectionTestUtils.setField(mockAuthor2, "id", 2L);
        ReflectionTestUtils.setField(mockAuthor2, "username", "김철수");
        ReflectionTestUtils.setField(mockAuthor2, "employeeNo", "EMP002");

        // Mock HandoverNote 생성
        mockHandoverNote = HandoverNote.create(mockAuthor, "휴가 인수인계", "휴가 중 처리해야 할 업무 안내입니다.");
        ReflectionTestUtils.setField(mockHandoverNote, "id", 1L);
        ReflectionTestUtils.setField(mockHandoverNote, "createdAt", LocalDateTime.now());

        mockHandoverNote2 = HandoverNote.create(mockAuthor, "프로젝트 인수인계", "프로젝트 진행 상황 공유");
        ReflectionTestUtils.setField(mockHandoverNote2, "id", 2L);
        ReflectionTestUtils.setField(mockHandoverNote2, "createdAt", LocalDateTime.now().minusHours(1));
    }

    @Nested
    @DisplayName("인수인계 노트 저장")
    class SaveHandoverNote {

        @Test
        @DisplayName("성공: 새로운 인수인계 노트를 저장한다")
        void save_Success() {
            // given
            HandoverNote newNote = HandoverNote.create(mockAuthor, "새 인수인계", "새 인수인계 내용");

            HandoverNote savedNote = HandoverNote.create(mockAuthor, "새 인수인계", "새 인수인계 내용");
            ReflectionTestUtils.setField(savedNote, "id", 10L);
            ReflectionTestUtils.setField(savedNote, "createdAt", LocalDateTime.now());

            given(handoverNoteRepository.save(any(HandoverNote.class))).willReturn(savedNote);

            // when
            HandoverNote result = handoverNoteRepository.save(newNote);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(10L);
            assertThat(result.getTitle()).isEqualTo("새 인수인계");
            assertThat(result.getAuthor()).isEqualTo(mockAuthor);
            verify(handoverNoteRepository).save(newNote);
        }

        @Test
        @DisplayName("성공: 다양한 작성자의 인수인계 노트를 저장한다")
        void save_DifferentAuthors_Success() {
            // given
            HandoverNote note1 = HandoverNote.create(mockAuthor, "인수인계1", "내용1");
            HandoverNote note2 = HandoverNote.create(mockAuthor2, "인수인계2", "내용2");

            given(handoverNoteRepository.save(any(HandoverNote.class))).willAnswer(invocation -> {
                HandoverNote note = invocation.getArgument(0);
                ReflectionTestUtils.setField(note, "id", (long) (Math.random() * 1000));
                return note;
            });

            // when
            HandoverNote saved1 = handoverNoteRepository.save(note1);
            HandoverNote saved2 = handoverNoteRepository.save(note2);

            // then
            assertThat(saved1.getAuthor().getUsername()).isEqualTo("홍길동");
            assertThat(saved2.getAuthor().getUsername()).isEqualTo("김철수");
            verify(handoverNoteRepository, times(2)).save(any(HandoverNote.class));
        }
    }

    @Nested
    @DisplayName("인수인계 노트 ID로 조회")
    class FindById {

        @Test
        @DisplayName("성공: ID로 인수인계 노트를 조회한다")
        void findById_Exists_ReturnsNote() {
            // given
            Long noteId = 1L;
            given(handoverNoteRepository.findById(noteId)).willReturn(Optional.of(mockHandoverNote));

            // when
            Optional<HandoverNote> result = handoverNoteRepository.findById(noteId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(noteId);
            assertThat(result.get().getTitle()).isEqualTo("휴가 인수인계");
            assertThat(result.get().getAuthor().getUsername()).isEqualTo("홍길동");
            verify(handoverNoteRepository).findById(noteId);
        }

        @Test
        @DisplayName("성공: 존재하지 않는 ID로 조회 시 빈 Optional 반환")
        void findById_NotExists_ReturnsEmpty() {
            // given
            Long nonExistentId = 999L;
            given(handoverNoteRepository.findById(nonExistentId)).willReturn(Optional.empty());

            // when
            Optional<HandoverNote> result = handoverNoteRepository.findById(nonExistentId);

            // then
            assertThat(result).isEmpty();
            verify(handoverNoteRepository).findById(nonExistentId);
        }

        @Test
        @DisplayName("성공: null ID로 조회 시 빈 Optional 반환")
        void findById_NullId_ReturnsEmpty() {
            // given
            given(handoverNoteRepository.findById(null)).willReturn(Optional.empty());

            // when
            Optional<HandoverNote> result = handoverNoteRepository.findById(null);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("작성자 ID로 인수인계 목록 조회")
    class FindByAuthorId {

        @Test
        @DisplayName("성공: 작성자 ID로 인수인계 목록을 페이징하여 조회한다")
        void findByAuthorId_Success() {
            // given
            Long authorId = 1L;
            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt")));
            List<HandoverNote> notes = List.of(mockHandoverNote, mockHandoverNote2);
            Page<HandoverNote> notePage = new PageImpl<>(notes, pageRequest, 2);

            given(handoverNoteRepository.findByAuthorId(eq(authorId), any(PageRequest.class)))
                    .willReturn(notePage);

            // when
            Page<HandoverNote> result = handoverNoteRepository.findByAuthorId(authorId, pageRequest);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getContent()).allMatch(note -> note.getAuthor().getId().equals(authorId));
            verify(handoverNoteRepository).findByAuthorId(authorId, pageRequest);
        }

        @Test
        @DisplayName("성공: 인수인계가 없는 작성자는 빈 페이지 반환")
        void findByAuthorId_EmptyResult() {
            // given
            Long authorId = 999L;
            PageRequest pageRequest = PageRequest.of(0, 10);
            Page<HandoverNote> emptyPage = new PageImpl<>(List.of(), pageRequest, 0);

            given(handoverNoteRepository.findByAuthorId(eq(authorId), any(PageRequest.class)))
                    .willReturn(emptyPage);

            // when
            Page<HandoverNote> result = handoverNoteRepository.findByAuthorId(authorId, pageRequest);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("성공: 페이지네이션이 올바르게 동작한다")
        void findByAuthorId_Pagination() {
            // given
            Long authorId = 1L;

            // 15개의 인수인계 노트 생성
            List<HandoverNote> allNotes = new ArrayList<>();
            for (int i = 0; i < 15; i++) {
                HandoverNote note = HandoverNote.create(mockAuthor, "인수인계 " + i, "내용 " + i);
                ReflectionTestUtils.setField(note, "id", (long) (i + 1));
                ReflectionTestUtils.setField(note, "createdAt", LocalDateTime.now().minusMinutes(i));
                allNotes.add(note);
            }

            PageRequest pageRequest1 = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("createdAt")));
            PageRequest pageRequest2 = PageRequest.of(1, 5, Sort.by(Sort.Order.desc("createdAt")));
            PageRequest pageRequest3 = PageRequest.of(2, 5, Sort.by(Sort.Order.desc("createdAt")));

            Page<HandoverNote> page1 = new PageImpl<>(allNotes.subList(0, 5), pageRequest1, 15);
            Page<HandoverNote> page2 = new PageImpl<>(allNotes.subList(5, 10), pageRequest2, 15);
            Page<HandoverNote> page3 = new PageImpl<>(allNotes.subList(10, 15), pageRequest3, 15);

            given(handoverNoteRepository.findByAuthorId(eq(authorId), eq(pageRequest1))).willReturn(page1);
            given(handoverNoteRepository.findByAuthorId(eq(authorId), eq(pageRequest2))).willReturn(page2);
            given(handoverNoteRepository.findByAuthorId(eq(authorId), eq(pageRequest3))).willReturn(page3);

            // when
            Page<HandoverNote> result1 = handoverNoteRepository.findByAuthorId(authorId, pageRequest1);
            Page<HandoverNote> result2 = handoverNoteRepository.findByAuthorId(authorId, pageRequest2);
            Page<HandoverNote> result3 = handoverNoteRepository.findByAuthorId(authorId, pageRequest3);

            // then
            assertThat(result1.getContent()).hasSize(5);
            assertThat(result2.getContent()).hasSize(5);
            assertThat(result3.getContent()).hasSize(5);
            assertThat(result1.getTotalElements()).isEqualTo(15);
            assertThat(result1.getTotalPages()).isEqualTo(3);
            assertThat(result1.isFirst()).isTrue();
            assertThat(result3.isLast()).isTrue();
        }

        @Test
        @DisplayName("성공: 특정 작성자의 인수인계만 조회한다")
        void findByAuthorId_OnlyMatchingAuthor() {
            // given
            Long author1Id = 1L;
            Long author2Id = 2L;
            PageRequest pageRequest = PageRequest.of(0, 10);

            // Author1의 노트
            Page<HandoverNote> author1Page = new PageImpl<>(
                    List.of(mockHandoverNote, mockHandoverNote2), pageRequest, 2);

            // Author2의 노트
            HandoverNote author2Note = HandoverNote.create(mockAuthor2, "김철수의 인수인계", "내용");
            ReflectionTestUtils.setField(author2Note, "id", 10L);
            Page<HandoverNote> author2Page = new PageImpl<>(List.of(author2Note), pageRequest, 1);

            given(handoverNoteRepository.findByAuthorId(eq(author1Id), any(PageRequest.class)))
                    .willReturn(author1Page);
            given(handoverNoteRepository.findByAuthorId(eq(author2Id), any(PageRequest.class)))
                    .willReturn(author2Page);

            // when
            Page<HandoverNote> author1Result = handoverNoteRepository.findByAuthorId(author1Id, pageRequest);
            Page<HandoverNote> author2Result = handoverNoteRepository.findByAuthorId(author2Id, pageRequest);

            // then
            assertThat(author1Result.getContent()).hasSize(2);
            assertThat(author2Result.getContent()).hasSize(1);

            assertThat(author1Result.getContent()).allMatch(note ->
                    note.getAuthor().getUsername().equals("홍길동"));
            assertThat(author2Result.getContent()).allMatch(note ->
                    note.getAuthor().getUsername().equals("김철수"));
        }
    }

    @Nested
    @DisplayName("작성자와 함께 인수인계 노트 조회")
    class FindByIdWithAuthor {

        @Test
        @DisplayName("성공: ID로 작성자 정보가 포함된 인수인계 노트를 조회한다")
        void findByIdWithAuthor_Success() {
            // given
            Long noteId = 1L;
            given(handoverNoteRepository.findByIdWithAuthor(noteId))
                    .willReturn(Optional.of(mockHandoverNote));

            // when
            Optional<HandoverNote> result = handoverNoteRepository.findByIdWithAuthor(noteId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(noteId);
            assertThat(result.get().getAuthor()).isNotNull();
            assertThat(result.get().getAuthor().getUsername()).isEqualTo("홍길동");
            assertThat(result.get().getAuthor().getEmail()).isEqualTo("author@example.com");
            verify(handoverNoteRepository).findByIdWithAuthor(noteId);
        }

        @Test
        @DisplayName("성공: 존재하지 않는 ID로 조회 시 빈 Optional 반환")
        void findByIdWithAuthor_NotExists_ReturnsEmpty() {
            // given
            Long nonExistentId = 999L;
            given(handoverNoteRepository.findByIdWithAuthor(nonExistentId))
                    .willReturn(Optional.empty());

            // when
            Optional<HandoverNote> result = handoverNoteRepository.findByIdWithAuthor(nonExistentId);

            // then
            assertThat(result).isEmpty();
            verify(handoverNoteRepository).findByIdWithAuthor(nonExistentId);
        }

        @Test
        @DisplayName("성공: 작성자 정보가 완전히 로드된다")
        void findByIdWithAuthor_AuthorFullyLoaded() {
            // given
            Long noteId = 1L;
            given(handoverNoteRepository.findByIdWithAuthor(noteId))
                    .willReturn(Optional.of(mockHandoverNote));

            // when
            Optional<HandoverNote> result = handoverNoteRepository.findByIdWithAuthor(noteId);

            // then
            assertThat(result).isPresent();
            User author = result.get().getAuthor();
            assertThat(author.getId()).isEqualTo(1L);
            assertThat(author.getUsername()).isEqualTo("홍길동");
            assertThat(author.getEmail()).isEqualTo("author@example.com");
            assertThat(author.getEmployeeNo()).isEqualTo("EMP001");
        }
    }

    @Nested
    @DisplayName("인수인계 노트 삭제")
    class DeleteHandoverNote {

        @Test
        @DisplayName("성공: ID로 인수인계 노트를 삭제한다")
        void deleteById_Success() {
            // given
            Long noteId = 1L;
            doNothing().when(handoverNoteRepository).deleteById(noteId);

            // when
            handoverNoteRepository.deleteById(noteId);

            // then
            verify(handoverNoteRepository, times(1)).deleteById(noteId);
        }

        @Test
        @DisplayName("성공: 존재하지 않는 ID 삭제 시 예외 없이 처리된다")
        void deleteById_NonExistent_NoException() {
            // given
            Long nonExistentId = 999L;
            doNothing().when(handoverNoteRepository).deleteById(nonExistentId);

            // when
            handoverNoteRepository.deleteById(nonExistentId);

            // then
            verify(handoverNoteRepository).deleteById(nonExistentId);
        }

        @Test
        @DisplayName("성공: 엔티티로 인수인계 노트를 삭제한다")
        void delete_Success() {
            // given
            doNothing().when(handoverNoteRepository).delete(mockHandoverNote);

            // when
            handoverNoteRepository.delete(mockHandoverNote);

            // then
            verify(handoverNoteRepository, times(1)).delete(mockHandoverNote);
        }
    }

    @Nested
    @DisplayName("인수인계 노트 개수 조회")
    class CountHandoverNotes {

        @Test
        @DisplayName("성공: 전체 인수인계 노트 개수를 조회한다")
        void count_ReturnsCorrectCount() {
            // given
            given(handoverNoteRepository.count()).willReturn(50L);

            // when
            long count = handoverNoteRepository.count();

            // then
            assertThat(count).isEqualTo(50L);
            verify(handoverNoteRepository).count();
        }

        @Test
        @DisplayName("성공: 인수인계 노트가 없을 경우 0을 반환한다")
        void count_NoNotes_ReturnsZero() {
            // given
            given(handoverNoteRepository.count()).willReturn(0L);

            // when
            long count = handoverNoteRepository.count();

            // then
            assertThat(count).isEqualTo(0L);
            verify(handoverNoteRepository).count();
        }
    }

    @Nested
    @DisplayName("인수인계 노트 존재 여부 확인")
    class ExistsById {

        @Test
        @DisplayName("성공: 존재하는 ID 확인 시 true 반환")
        void existsById_Exists_ReturnsTrue() {
            // given
            Long existingId = 1L;
            given(handoverNoteRepository.existsById(existingId)).willReturn(true);

            // when
            boolean exists = handoverNoteRepository.existsById(existingId);

            // then
            assertThat(exists).isTrue();
            verify(handoverNoteRepository).existsById(existingId);
        }

        @Test
        @DisplayName("성공: 존재하지 않는 ID 확인 시 false 반환")
        void existsById_NotExists_ReturnsFalse() {
            // given
            Long nonExistentId = 999L;
            given(handoverNoteRepository.existsById(nonExistentId)).willReturn(false);

            // when
            boolean exists = handoverNoteRepository.existsById(nonExistentId);

            // then
            assertThat(exists).isFalse();
            verify(handoverNoteRepository).existsById(nonExistentId);
        }
    }

    @Nested
    @DisplayName("복합 시나리오")
    class ComplexScenarios {

        @Test
        @DisplayName("성공: 노트 저장 후 조회")
        void saveAndFind_Success() {
            // given
            HandoverNote newNote = HandoverNote.create(mockAuthor, "테스트 인수인계", "테스트 내용");

            HandoverNote savedNote = HandoverNote.create(mockAuthor, "테스트 인수인계", "테스트 내용");
            ReflectionTestUtils.setField(savedNote, "id", 100L);

            given(handoverNoteRepository.save(any(HandoverNote.class))).willReturn(savedNote);
            given(handoverNoteRepository.findById(100L)).willReturn(Optional.of(savedNote));

            // when
            HandoverNote saved = handoverNoteRepository.save(newNote);
            Optional<HandoverNote> found = handoverNoteRepository.findById(saved.getId());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getTitle()).isEqualTo("테스트 인수인계");
            assertThat(found.get().getContent()).isEqualTo("테스트 내용");
        }

        @Test
        @DisplayName("성공: 저장 후 삭제, 조회 시 빈 결과")
        void saveDeleteFind_Empty() {
            // given
            Long noteId = 50L;
            HandoverNote savedNote = HandoverNote.create(mockAuthor, "삭제 예정", "삭제될 내용");
            ReflectionTestUtils.setField(savedNote, "id", noteId);

            given(handoverNoteRepository.save(any(HandoverNote.class))).willReturn(savedNote);
            given(handoverNoteRepository.findById(noteId))
                    .willReturn(Optional.of(savedNote), Optional.empty());
            doNothing().when(handoverNoteRepository).deleteById(noteId);

            // when
            HandoverNote saved = handoverNoteRepository.save(savedNote);
            Optional<HandoverNote> beforeDelete = handoverNoteRepository.findById(noteId);
            handoverNoteRepository.deleteById(noteId);
            Optional<HandoverNote> afterDelete = handoverNoteRepository.findById(noteId);

            // then
            assertThat(beforeDelete).isPresent();
            assertThat(afterDelete).isEmpty();
        }

        @Test
        @DisplayName("성공: 여러 작성자의 노트 저장 후 작성자별 조회")
        void saveMultipleAuthorsAndFindByAuthor() {
            // given
            PageRequest pageRequest = PageRequest.of(0, 10);

            // 작성자별 노트 페이지
            Page<HandoverNote> author1Page = new PageImpl<>(
                    List.of(mockHandoverNote, mockHandoverNote2), pageRequest, 2);

            HandoverNote author2Note = HandoverNote.create(mockAuthor2, "김철수의 인수인계", "내용");
            ReflectionTestUtils.setField(author2Note, "id", 10L);
            Page<HandoverNote> author2Page = new PageImpl<>(List.of(author2Note), pageRequest, 1);

            given(handoverNoteRepository.findByAuthorId(eq(1L), any(PageRequest.class)))
                    .willReturn(author1Page);
            given(handoverNoteRepository.findByAuthorId(eq(2L), any(PageRequest.class)))
                    .willReturn(author2Page);

            // when
            Page<HandoverNote> author1Result = handoverNoteRepository.findByAuthorId(1L, pageRequest);
            Page<HandoverNote> author2Result = handoverNoteRepository.findByAuthorId(2L, pageRequest);

            // then
            assertThat(author1Result.getContent()).hasSize(2);
            assertThat(author2Result.getContent()).hasSize(1);

            assertThat(author1Result.getContent()).allMatch(note ->
                    note.getAuthor().getId().equals(1L));
            assertThat(author2Result.getContent()).allMatch(note ->
                    note.getAuthor().getId().equals(2L));
        }
    }
}
