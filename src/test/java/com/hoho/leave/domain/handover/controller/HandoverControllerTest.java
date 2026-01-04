package com.hoho.leave.domain.handover.controller;

import com.hoho.leave.domain.handover.dto.request.HandoverCreateRequest;
import com.hoho.leave.domain.handover.dto.request.HandoverUpdateRequest;
import com.hoho.leave.domain.handover.dto.response.HandoverAuthorListResponse;
import com.hoho.leave.domain.handover.dto.response.HandoverAuthorResponse;
import com.hoho.leave.domain.handover.dto.response.HandoverDetailResponse;
import com.hoho.leave.domain.handover.dto.response.HandoverRecipientListResponse;
import com.hoho.leave.domain.handover.dto.response.HandoverRecipientResponse;
import com.hoho.leave.domain.handover.facade.HandoverModify;
import com.hoho.leave.domain.handover.service.HandoverRecipientService;
import com.hoho.leave.domain.handover.service.HandoverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("HandoverController 테스트")
class HandoverControllerTest {

    @InjectMocks
    private HandoverController handoverController;

    @Mock
    private HandoverRecipientService recipientService;

    @Mock
    private HandoverService handoverService;

    @Mock
    private HandoverModify handoverModify;

    private HandoverAuthorListResponse mockAuthorListResponse;
    private HandoverRecipientListResponse mockRecipientListResponse;
    private HandoverDetailResponse mockDetailResponse;
    private HandoverAuthorResponse mockAuthorResponse;
    private HandoverRecipientResponse mockRecipientResponse;

    @BeforeEach
    void setUp() {
        // Mock HandoverAuthorResponse 생성
        mockAuthorResponse = new HandoverAuthorResponse();
        mockAuthorResponse.setHandoverNoteId(1L);
        mockAuthorResponse.setAuthorName("홍길동");
        mockAuthorResponse.setRecipientName(List.of("김철수", "이영희"));
        mockAuthorResponse.setTitle("휴가 인수인계");
        mockAuthorResponse.setContent("휴가 중 처리해야 할 업무 안내");
        mockAuthorResponse.setOccurredAt(LocalDateTime.now());

        // Mock HandoverAuthorListResponse 생성
        mockAuthorListResponse = new HandoverAuthorListResponse();
        mockAuthorListResponse.setPage(1);
        mockAuthorListResponse.setSize(10);
        mockAuthorListResponse.setHandoverNotes(List.of(mockAuthorResponse));
        mockAuthorListResponse.setTotalPage(1);
        mockAuthorListResponse.setTotalElement(1L);
        mockAuthorListResponse.setFirstPage(true);
        mockAuthorListResponse.setLastPage(true);

        // Mock HandoverRecipientResponse 생성
        mockRecipientResponse = new HandoverRecipientResponse();
        mockRecipientResponse.setHandoverId(1L);
        mockRecipientResponse.setAuthorName("홍길동");
        mockRecipientResponse.setTitle("휴가 인수인계");
        mockRecipientResponse.setContent("휴가 중 처리해야 할 업무 안내");
        mockRecipientResponse.setOccurredAt(LocalDateTime.now());

        // Mock HandoverRecipientListResponse 생성
        mockRecipientListResponse = new HandoverRecipientListResponse();
        mockRecipientListResponse.setPage(1);
        mockRecipientListResponse.setSize(10);
        mockRecipientListResponse.setRecipients(List.of(mockRecipientResponse));
        mockRecipientListResponse.setTotalPage(1);
        mockRecipientListResponse.setTotalElement(1L);
        mockRecipientListResponse.setFirstPage(true);
        mockRecipientListResponse.setLastPage(true);

        // Mock HandoverDetailResponse 생성
        mockDetailResponse = new HandoverDetailResponse();
        mockDetailResponse.setHandoverId(1L);
        mockDetailResponse.setAuthorName("홍길동");
        mockDetailResponse.setRecipientNames(List.of("김철수", "이영희"));
        mockDetailResponse.setTitle("휴가 인수인계");
        mockDetailResponse.setContent("휴가 중 처리해야 할 업무 안내");
        mockDetailResponse.setOccurredAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("인수인계 생성")
    class CreateHandover {

        @Test
        @DisplayName("성공: 인수인계를 생성한다")
        void createHandover_Success() {
            // given
            HandoverCreateRequest request = new HandoverCreateRequest();
            request.setAuthorId(1L);
            request.setRecipientIds(List.of(2L, 3L));
            request.setTitle("휴가 인수인계");
            request.setContent("휴가 중 처리해야 할 업무 안내");

            doNothing().when(handoverModify).createHandover(request);

            // when
            ResponseEntity<?> response = handoverController.createHandover(request);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo("인수인계 등록 성공");
            verify(handoverModify).createHandover(request);
        }
    }

    @Nested
    @DisplayName("발신 인수인계 목록 조회")
    class GetHandoverAuthorList {

        @Test
        @DisplayName("성공: 사용자가 발신한 인수인계 목록을 조회한다")
        void getHandoverAuthorList_Success() {
            // given
            Long userId = 1L;
            Integer page = 1;
            Integer size = 10;

            given(handoverService.getHandoverAuthorList(userId, page, size))
                    .willReturn(mockAuthorListResponse);

            // when
            ResponseEntity<HandoverAuthorListResponse> response =
                    handoverController.getHandoverAuthorList(userId, page, size);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getHandoverNotes()).hasSize(1);
            assertThat(response.getBody().getPage()).isEqualTo(1);
            verify(handoverService).getHandoverAuthorList(userId, page, size);
        }

        @Test
        @DisplayName("성공: 빈 발신 인수인계 목록을 조회한다")
        void getHandoverAuthorList_EmptyList() {
            // given
            Long userId = 1L;
            Integer page = 1;
            Integer size = 10;

            HandoverAuthorListResponse emptyResponse = new HandoverAuthorListResponse();
            emptyResponse.setPage(1);
            emptyResponse.setSize(10);
            emptyResponse.setHandoverNotes(List.of());
            emptyResponse.setTotalPage(0);
            emptyResponse.setTotalElement(0L);
            emptyResponse.setFirstPage(true);
            emptyResponse.setLastPage(true);

            given(handoverService.getHandoverAuthorList(userId, page, size))
                    .willReturn(emptyResponse);

            // when
            ResponseEntity<HandoverAuthorListResponse> response =
                    handoverController.getHandoverAuthorList(userId, page, size);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getHandoverNotes()).isEmpty();
            assertThat(response.getBody().getTotalElement()).isEqualTo(0L);
        }

        @Test
        @DisplayName("성공: 페이지네이션 정보가 올바르게 반환된다")
        void getHandoverAuthorList_PaginationInfo() {
            // given
            Long userId = 1L;
            Integer page = 2;
            Integer size = 5;

            HandoverAuthorListResponse paginatedResponse = new HandoverAuthorListResponse();
            paginatedResponse.setPage(2);
            paginatedResponse.setSize(5);
            paginatedResponse.setHandoverNotes(List.of(mockAuthorResponse));
            paginatedResponse.setTotalPage(3);
            paginatedResponse.setTotalElement(15L);
            paginatedResponse.setFirstPage(false);
            paginatedResponse.setLastPage(false);

            given(handoverService.getHandoverAuthorList(userId, page, size))
                    .willReturn(paginatedResponse);

            // when
            ResponseEntity<HandoverAuthorListResponse> response =
                    handoverController.getHandoverAuthorList(userId, page, size);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getPage()).isEqualTo(2);
            assertThat(response.getBody().getSize()).isEqualTo(5);
            assertThat(response.getBody().getTotalPage()).isEqualTo(3);
            assertThat(response.getBody().getTotalElement()).isEqualTo(15L);
            assertThat(response.getBody().getFirstPage()).isFalse();
            assertThat(response.getBody().getLastPage()).isFalse();
        }
    }

    @Nested
    @DisplayName("수신 인수인계 목록 조회")
    class GetHandoverRecipientList {

        @Test
        @DisplayName("성공: 사용자가 수신한 인수인계 목록을 조회한다")
        void getHandoverRecipientList_Success() {
            // given
            Long recipientId = 2L;
            Integer page = 1;
            Integer size = 10;

            given(recipientService.getRecipientList(recipientId, page, size))
                    .willReturn(mockRecipientListResponse);

            // when
            ResponseEntity<HandoverRecipientListResponse> response =
                    handoverController.getHandoverRecipientList(recipientId, page, size);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getRecipients()).hasSize(1);
            assertThat(response.getBody().getPage()).isEqualTo(1);
            verify(recipientService).getRecipientList(recipientId, page, size);
        }

        @Test
        @DisplayName("성공: 빈 수신 인수인계 목록을 조회한다")
        void getHandoverRecipientList_EmptyList() {
            // given
            Long recipientId = 2L;
            Integer page = 1;
            Integer size = 10;

            HandoverRecipientListResponse emptyResponse = new HandoverRecipientListResponse();
            emptyResponse.setPage(1);
            emptyResponse.setSize(10);
            emptyResponse.setRecipients(List.of());
            emptyResponse.setTotalPage(0);
            emptyResponse.setTotalElement(0L);
            emptyResponse.setFirstPage(true);
            emptyResponse.setLastPage(true);

            given(recipientService.getRecipientList(recipientId, page, size))
                    .willReturn(emptyResponse);

            // when
            ResponseEntity<HandoverRecipientListResponse> response =
                    handoverController.getHandoverRecipientList(recipientId, page, size);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getRecipients()).isEmpty();
            assertThat(response.getBody().getTotalElement()).isEqualTo(0L);
        }

        @Test
        @DisplayName("성공: 수신 인수인계 페이지네이션 정보가 올바르게 반환된다")
        void getHandoverRecipientList_PaginationInfo() {
            // given
            Long recipientId = 2L;
            Integer page = 2;
            Integer size = 5;

            HandoverRecipientListResponse paginatedResponse = new HandoverRecipientListResponse();
            paginatedResponse.setPage(2);
            paginatedResponse.setSize(5);
            paginatedResponse.setRecipients(List.of(mockRecipientResponse));
            paginatedResponse.setTotalPage(4);
            paginatedResponse.setTotalElement(20L);
            paginatedResponse.setFirstPage(false);
            paginatedResponse.setLastPage(false);

            given(recipientService.getRecipientList(recipientId, page, size))
                    .willReturn(paginatedResponse);

            // when
            ResponseEntity<HandoverRecipientListResponse> response =
                    handoverController.getHandoverRecipientList(recipientId, page, size);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getPage()).isEqualTo(2);
            assertThat(response.getBody().getSize()).isEqualTo(5);
            assertThat(response.getBody().getTotalPage()).isEqualTo(4);
            assertThat(response.getBody().getTotalElement()).isEqualTo(20L);
            assertThat(response.getBody().getFirstPage()).isFalse();
            assertThat(response.getBody().getLastPage()).isFalse();
        }
    }

    @Nested
    @DisplayName("인수인계 단건 조회")
    class GetHandover {

        @Test
        @DisplayName("성공: 인수인계를 단건 조회한다")
        void getHandover_Success() {
            // given
            Long handoverId = 1L;

            given(handoverService.getHandover(handoverId))
                    .willReturn(mockDetailResponse);

            // when
            ResponseEntity<HandoverDetailResponse> response =
                    handoverController.getHandover(handoverId);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getHandoverId()).isEqualTo(1L);
            assertThat(response.getBody().getAuthorName()).isEqualTo("홍길동");
            assertThat(response.getBody().getRecipientNames()).hasSize(2);
            assertThat(response.getBody().getTitle()).isEqualTo("휴가 인수인계");
            verify(handoverService).getHandover(handoverId);
        }

        @Test
        @DisplayName("성공: 여러 수신자가 있는 인수인계를 조회한다")
        void getHandover_MultipleRecipients() {
            // given
            Long handoverId = 1L;

            HandoverDetailResponse multiRecipientResponse = new HandoverDetailResponse();
            multiRecipientResponse.setHandoverId(1L);
            multiRecipientResponse.setAuthorName("홍길동");
            multiRecipientResponse.setRecipientNames(List.of("김철수", "이영희", "박민수", "최지영"));
            multiRecipientResponse.setTitle("팀 전체 인수인계");
            multiRecipientResponse.setContent("팀 전체에게 전달하는 인수인계입니다.");
            multiRecipientResponse.setOccurredAt(LocalDateTime.now());

            given(handoverService.getHandover(handoverId))
                    .willReturn(multiRecipientResponse);

            // when
            ResponseEntity<HandoverDetailResponse> response =
                    handoverController.getHandover(handoverId);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getRecipientNames()).hasSize(4);
            assertThat(response.getBody().getRecipientNames())
                    .containsExactly("김철수", "이영희", "박민수", "최지영");
        }
    }

    @Nested
    @DisplayName("인수인계 수정")
    class UpdateHandover {

        @Test
        @DisplayName("성공: 인수인계를 수정한다")
        void updateHandover_Success() {
            // given
            Long handoverId = 1L;
            HandoverUpdateRequest request = new HandoverUpdateRequest();
            request.setAuthorId(1L);
            request.setRecipientIds(List.of(2L, 3L, 4L));
            request.setTitle("수정된 인수인계");
            request.setContent("수정된 인수인계 내용");

            doNothing().when(handoverModify).updateHandover(handoverId, request);

            // when
            ResponseEntity<?> response = handoverController.updateHandover(handoverId, request);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo("인수인계 수정 완료");
            verify(handoverModify).updateHandover(handoverId, request);
        }
    }

    @Nested
    @DisplayName("인수인계 삭제")
    class DeleteHandover {

        @Test
        @DisplayName("성공: 인수인계를 삭제한다")
        void deleteHandover_Success() {
            // given
            Long handoverId = 1L;

            doNothing().when(handoverModify).deleteHandover(handoverId);

            // when
            ResponseEntity<?> response = handoverController.deleteHandover(handoverId);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo("인수인계 삭제 성공");
            verify(handoverModify).deleteHandover(handoverId);
        }
    }
}
