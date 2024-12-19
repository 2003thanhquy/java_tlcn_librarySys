package com.spkt.librasys.feature.loanTransaction;

import com.spkt.librasys.dto.request.loanTransaction.LoanTransactionRequest;
import com.spkt.librasys.dto.response.LoanTransactionResponse;
import com.spkt.librasys.entity.*;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.LoanTransactionMapper;
import com.spkt.librasys.repository.DocumentRepository;
import com.spkt.librasys.repository.LoanPolicyRepository;
import com.spkt.librasys.repository.LoanTransactionRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.service.NotificationService;
import com.spkt.librasys.service.SecurityContextService;
import com.spkt.librasys.service.WebSocketService;
import com.spkt.librasys.service.impl.LoanTransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
public class ReceiveLoanTest {
    @Mock
    private LoanTransactionRepository loanTransactionRepository;
    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private LoanPolicyRepository loanPolicyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private WebSocketService webSocketService;
    @Mock
    private LoanTransactionMapper loanTransactionMapper;
    @Mock
    private SecurityContextService securityContextService;

    @InjectMocks
    private LoanTransactionServiceImpl loanTransactionService;

    private LoanTransactionRequest loanTransactionRequest;
    private User mockUser;
    private Document mockDocument;
    private LoanTransaction mockLoanTransaction;
    private LoanPolicy mockLoanPolicy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Khởi tạo đối tượng mock cho yêu cầu mượn sách
        loanTransactionRequest = LoanTransactionRequest.builder()
                .documentId(1L)
                .build();

        // Khởi tạo đối tượng mock cho User và Document
        mockUser = new User();
        mockUser.setUserId("1L");
        mockUser.setMaxBorrowLimit(5);
        mockUser.setCurrentBorrowedCount(0);
        Set<Role> roles = new HashSet<>();
        roles.add(Role.builder().name("USER").build());
        mockUser.setRoles(roles);

        mockDocument = new Document();
        mockDocument.setDocumentId(1L);
        mockDocument.setDocumentName("Test Document");

        mockLoanPolicy = new LoanPolicy();
        mockLoanPolicy.setMaxLoanDays(7);

        // Khởi tạo LoanTransaction
        mockLoanTransaction = LoanTransaction.builder()
                .document(mockDocument)
                .user(mockUser)
                .status(LoanTransaction.Status.APPROVED)
                .loanDate(LocalDateTime.now())
                .build();

        // Giả lập hành vi của các dependency
        when(securityContextService.getCurrentUser()).thenReturn(mockUser);
        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));
        when(loanTransactionRepository.findById(1L)).thenReturn(Optional.of(mockLoanTransaction));
        when(loanPolicyRepository.findByDocumentTypeIn(any())).thenReturn(List.of(mockLoanPolicy));
        when(loanTransactionRepository.save(any(LoanTransaction.class))).thenReturn(mockLoanTransaction);

        // Mock loanTransactionMapper
        when(loanTransactionMapper.toLoanTransactionResponse(mockLoanTransaction)).thenReturn(
                LoanTransactionResponse.builder()
                        .documentName(mockLoanTransaction.getDocument().getDocumentName())
                        .status(mockLoanTransaction.getStatus()).build());
    }

    @Test
    void testReceiveDocument_Success() {
        // Giả lập khi người dùng có quyền và giao dịch ở trạng thái APPROVED
        when(loanTransactionRepository.findById(1L)).thenReturn(Optional.of(mockLoanTransaction));
        when(documentRepository.findById(mockLoanTransaction.getDocument().getDocumentId())).thenReturn(Optional.of(mockDocument));
        when(loanPolicyRepository.findByDocumentTypeIn(any())).thenReturn(List.of(mockLoanPolicy));

        // Gọi phương thức receiveDocument
        LoanTransactionResponse response = loanTransactionService.receiveDocument(1L, true);

        // Kiểm tra kết quả
        assertNotNull(response);
        assertEquals(LoanTransaction.Status.RECEIVED, mockLoanTransaction.getStatus());
        assertEquals("Test Document", response.getDocumentName());

        // Kiểm tra các hành vi của các dependency
//        verify(loanTransactionRepository).save(mockLoanTransaction);  // Kiểm tra xem loanTransaction đã được lưu
//        verify(notificationService).createNotifications(any());  // Kiểm tra xem thông báo đã được gửi cho người dùng
//        verify(webSocketService).sendLoanStatusUpdate(any(), any());  // Kiểm tra xem thông báo WebSocket đã được gửi
    }

    @Test
    void testReceiveDocument_TransactionNotFound() {
        // Giả lập không tìm thấy giao dịch mượn sách
        when(loanTransactionRepository.findById(1L)).thenReturn(Optional.empty());

        // Kiểm tra ngoại lệ khi không tìm thấy giao dịch
        AppException exception = assertThrows(AppException.class, () -> loanTransactionService.receiveDocument(1L, true));
        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testReceiveDocument_PolicyNotFound() {
        // Giả lập không tìm thấy chính sách mượn
        when(loanTransactionRepository.findById(1L)).thenReturn(Optional.of(mockLoanTransaction));
        when(loanPolicyRepository.findByDocumentTypeIn(any())).thenReturn(List.of());

        // Kiểm tra ngoại lệ khi không tìm thấy chính sách mượn
        AppException exception = assertThrows(AppException.class, () -> loanTransactionService.receiveDocument(1L, true));
        assertEquals(ErrorCode.POLICY_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testReceiveDocument_InvalidStatus() {
        // Giả lập trạng thái giao dịch không phải APPROVED
        mockLoanTransaction.setStatus(LoanTransaction.Status.PENDING);
        when(loanTransactionRepository.findById(1L)).thenReturn(Optional.of(mockLoanTransaction));

        // Kiểm tra ngoại lệ khi trạng thái giao dịch không phải APPROVED
        AppException exception = assertThrows(AppException.class, () -> loanTransactionService.receiveDocument(1L, true));
        assertEquals(ErrorCode.INVALID_REQUEST, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Giao dịch không ở trạng thái APPROVED"));
    }
}
