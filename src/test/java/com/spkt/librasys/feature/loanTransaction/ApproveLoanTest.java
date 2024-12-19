package com.spkt.librasys.feature.loanTransaction;

import com.spkt.librasys.dto.request.loanTransaction.LoanTransactionRequest;
import com.spkt.librasys.dto.response.LoanTransactionResponse;
import com.spkt.librasys.entity.*;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.LoanTransactionMapper;
import com.spkt.librasys.repository.DocumentRepository;
import com.spkt.librasys.repository.LoanTransactionRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.service.NotificationService;
import com.spkt.librasys.service.SecurityContextService;
import com.spkt.librasys.service.WebSocketService;
import com.spkt.librasys.service.impl.DocumentMoveServiceImpl;
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

public class ApproveLoanTest {


    @Mock
    private LoanTransactionRepository loanTransactionRepository;
    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private SecurityContextService securityContextService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private WebSocketService webSocketService;
    @Mock
    private LoanTransactionMapper loanTransactionMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DocumentMoveServiceImpl documentMoveService;
    @InjectMocks
    private LoanTransactionServiceImpl loanTransactionService;

    private LoanTransactionRequest loanTransactionRequest;
    private User mockUser;
    private Document mockDocument;
    private LoanTransaction mockLoanTransaction;

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
        roles.add(Role.builder().name("ADMIN").build());
        mockUser.setRoles(roles);

        mockDocument = new Document();
        mockDocument.setDocumentId(1L);
        mockDocument.setDocumentName("Test Document");
        mockDocument.setAvailableCount(10);

        // Khởi tạo LoanTransaction
        mockLoanTransaction = LoanTransaction.builder()
                .document(mockDocument)
                .user(mockUser)
                .status(LoanTransaction.Status.PENDING)
                .loanDate(LocalDateTime.now())
                .build();

        // Giả lập hành vi của các dependency
        when(securityContextService.getCurrentUser()).thenReturn(mockUser);
        when(documentRepository.findById(1L)).thenReturn(java.util.Optional.of(mockDocument));
        when(loanTransactionRepository.existsPendingLoanTransaction(mockUser, mockDocument, LoanTransaction.Status.PENDING)).thenReturn(false);
        when(loanTransactionRepository.countByUserAndStatus(mockUser, LoanTransaction.Status.PENDING)).thenReturn(0L);
        when(loanTransactionRepository.save(any(LoanTransaction.class))).thenReturn(mockLoanTransaction);
        when(userRepository.findAll()).thenReturn(List.of(mockUser));  // Mocking findAll() on userRepository

        // Mock loanTransactionMapper
        LoanTransactionResponse loanTransactionResponse = new LoanTransactionResponse();
        loanTransactionResponse.setDocumentName("Test Document");
        loanTransactionResponse.setUsername("testUser");
        loanTransactionResponse.setStatus(LoanTransaction.Status.PENDING);
        when(loanTransactionMapper.toLoanTransactionResponse(mockLoanTransaction)).thenReturn(LoanTransactionResponse.builder()
                .documentName(mockLoanTransaction.getDocument().getDocumentName())
                .status(mockLoanTransaction.getStatus()).build());
    }

    @Test
    void testApproveTransaction_Success() {
        // Giả lập hành vi của các dependency
        when(loanTransactionRepository.findById(1L)).thenReturn(Optional.of(mockLoanTransaction));
        when(documentRepository.findById(mockLoanTransaction.getDocument().getDocumentId())).thenReturn(Optional.of(mockDocument));
        when(documentMoveService.approveAndMoveDocument(mockDocument.getDocumentId(), 1)).thenReturn(new DocumentLocation());

        // Gọi phương thức approveTransaction
        LoanTransactionResponse response = loanTransactionService.approveTransaction(1L);

        // Kiểm tra phản hồi
        assertNotNull(response);
        assertEquals(LoanTransaction.Status.APPROVED, mockLoanTransaction.getStatus());

         // Kiểm tra các hành vi của các dependency
//        verify(loanTransactionRepository).save(mockLoanTransaction);  // Kiểm tra xem loanTransaction đã được lưu
//        verify(notificationService, times(2)).createNotifications(any());  // Kiểm tra xem thông báo đã được gửi
//        verify(webSocketService).sendUpdateStatusLoan(any(), any());  // Kiểm tra xem thông báo WebSocket đã được gửi
    }

    @Test
    void testApproveTransaction_TransactionNotFound() {
        // Giả lập không tìm thấy giao dịch mượn sách
        when(loanTransactionRepository.findById(1L)).thenReturn(Optional.empty());

        // Kiểm tra ngoại lệ khi không tìm thấy giao dịch
        AppException exception = assertThrows(AppException.class, () -> loanTransactionService.approveTransaction(1L));
        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND, exception.getErrorCode());
    }
    @Test
    void testApproveTransaction_InsufficientStock() {
        // Giả lập không có đủ sách để mượn
        mockDocument.setAvailableCount(0);
        when(loanTransactionRepository.findById(1L)).thenReturn(Optional.of(mockLoanTransaction));
        // Kiểm tra ngoại lệ khi số lượng sách không đủ
        AppException exception = assertThrows(AppException.class, () -> loanTransactionService.approveTransaction(1L));
        assertEquals(ErrorCode.RESOURCE_CONFLICT, exception.getErrorCode());
    }
}
