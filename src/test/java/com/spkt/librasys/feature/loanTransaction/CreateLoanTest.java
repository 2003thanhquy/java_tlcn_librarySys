package com.spkt.librasys.feature.loanTransaction;
import com.spkt.librasys.dto.request.loanTransaction.LoanTransactionRequest;
import com.spkt.librasys.dto.response.LoanTransactionResponse;
import com.spkt.librasys.entity.Role;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.LoanTransaction;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.LoanTransactionMapper;
import com.spkt.librasys.repository.LoanTransactionRepository;
import com.spkt.librasys.repository.DocumentRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.service.impl.LoanTransactionServiceImpl;
import com.spkt.librasys.service.NotificationService;
import com.spkt.librasys.service.WebSocketService;
import com.spkt.librasys.service.SecurityContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CreateLoanTest{

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
    void testCreateLoanTransaction_Success() {
        // Gọi phương thức createLoanTransaction
        LoanTransactionResponse response = loanTransactionService.createLoanTransaction(loanTransactionRequest);

        // Kiểm tra kết quả
        assertNotNull(response); // Đảm bảo phản hồi không phải null
        assertEquals(mockDocument.getDocumentName(), response.getDocumentName());
        assertEquals(LoanTransaction.Status.PENDING, response.getStatus());

        // Kiểm tra các hành vi của các dependency
//        verify(securityContextService).getCurrentUser();
//        verify(documentRepository).findById(1L);
//        verify(loanTransactionRepository).save(any(LoanTransaction.class));
//        verify(webSocketService).sendUpdateStatusLoan(anyString(), any(LoanTransactionResponse.class)); // Kiểm tra xem phương thức đã được gọi
//        verify(notificationService).createNotifications(any());  // Kiểm tra xem thông báo đã được gửi
    }

    @Test
    void testCreateLoanTransaction_UserNotFound() {
        // Giả lập hành vi khi không tìm thấy người dùng
        when(securityContextService.getCurrentUser()).thenReturn(null);

        // Gọi phương thức và kiểm tra ngoại lệ
        AppException exception = assertThrows(AppException.class, () ->
                loanTransactionService.createLoanTransaction(loanTransactionRequest)
        );

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testCreateLoanTransaction_DocumentNotFound() {
        // Giả lập hành vi khi không tìm thấy tài liệu
        when(documentRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // Gọi phương thức và kiểm tra ngoại lệ
        AppException exception = assertThrows(AppException.class, () ->
                loanTransactionService.createLoanTransaction(loanTransactionRequest)
        );

        assertEquals(ErrorCode.DOCUMENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testCreateLoanTransaction_ResourceConflict_ExceededLimit() {
        // Giả lập khi số lượng sách mượn vượt quá giới hạn
        when(loanTransactionRepository.countByUserAndStatus(mockUser, LoanTransaction.Status.PENDING)).thenReturn(5L);

        // Gọi phương thức và kiểm tra ngoại lệ
        AppException exception = assertThrows(AppException.class, () ->
                loanTransactionService.createLoanTransaction(loanTransactionRequest)
        );

        assertEquals(ErrorCode.RESOURCE_CONFLICT, exception.getErrorCode());
    }

    @Test
    void testCreateLoanTransaction_ResourceConflict_PendingRequest() {
        // Giả lập khi đã có yêu cầu mượn sách đang chờ phê duyệt
        when(loanTransactionRepository.existsPendingLoanTransaction(mockUser, mockDocument, LoanTransaction.Status.PENDING)).thenReturn(true);

        // Gọi phương thức và kiểm tra ngoại lệ
        AppException exception = assertThrows(AppException.class, () ->
                loanTransactionService.createLoanTransaction(loanTransactionRequest)
        );

        assertEquals(ErrorCode.RESOURCE_CONFLICT, exception.getErrorCode());
    }
}
