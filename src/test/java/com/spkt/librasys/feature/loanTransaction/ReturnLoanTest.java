package com.spkt.librasys.feature.loanTransaction;

import com.spkt.librasys.dto.request.loanTransaction.LoanTransactionRequest;
import com.spkt.librasys.dto.response.LoanTransactionResponse;
import com.spkt.librasys.entity.LoanTransaction;
import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.entity.Role;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.LoanTransactionMapper;
import com.spkt.librasys.repository.LoanTransactionRepository;
import com.spkt.librasys.repository.DocumentRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.service.NotificationService;
import com.spkt.librasys.service.WebSocketService;
import com.spkt.librasys.service.impl.LoanTransactionServiceImpl;
import com.spkt.librasys.service.SecurityContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ReturnLoanTest {

    @Mock
    private LoanTransactionRepository loanTransactionRepository;
    @Mock
    private DocumentRepository documentRepository;
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
        mockUser.setCurrentBorrowedCount(1);  // Giả lập người dùng đã mượn 1 sách
        Set<Role> roles = new HashSet<>();
        roles.add(Role.builder().name("USER").build());
        mockUser.setRoles(roles);

        mockDocument = new Document();
        mockDocument.setDocumentId(1L);
        mockDocument.setDocumentName("Test Document");
        mockDocument.setAvailableCount(4);  // Giả lập tài liệu có sẵn 4 bản

        // Khởi tạo LoanTransaction
        mockLoanTransaction = LoanTransaction.builder()
                .document(mockDocument)
                .user(mockUser)
                .status(LoanTransaction.Status.RECEIVED)
                .loanDate(LocalDateTime.now())
                .build();

        // Giả lập hành vi của các dependency
        when(securityContextService.getCurrentUser()).thenReturn(mockUser);
        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));
        when(loanTransactionRepository.findById(1L)).thenReturn(Optional.of(mockLoanTransaction));
        when(loanTransactionRepository.save(any(LoanTransaction.class))).thenReturn(mockLoanTransaction);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Mock loanTransactionMapper
        when(loanTransactionMapper.toLoanTransactionResponse(mockLoanTransaction)).thenReturn(
                LoanTransactionResponse.builder()
                        .documentName(mockLoanTransaction.getDocument().getDocumentName())
                        .status(mockLoanTransaction.getStatus()).build());
    }

    @Test
    void testReturnDocument_Success() {
        // Giả lập khi giao dịch có trạng thái RECEIVED và người dùng có quyền trả sách
        when(loanTransactionRepository.findById(1L)).thenReturn(Optional.of(mockLoanTransaction));
        when(documentRepository.findById(mockLoanTransaction.getDocument().getDocumentId())).thenReturn(Optional.of(mockDocument));

        // Gọi phương thức returnDocument
        LoanTransactionResponse response = loanTransactionService.returnDocument(1L, true);

        // Kiểm tra phản hồi
        assertNotNull(response);
        assertEquals(LoanTransaction.Status.RETURN_REQUESTED, mockLoanTransaction.getStatus());
        assertEquals("Test Document", response.getDocumentName());
        assertEquals(5, mockDocument.getAvailableCount());  // Số lượng sách có sẵn phải tăng lên 5
        assertEquals(0, mockUser.getCurrentBorrowedCount());  // Số sách đã mượn của người dùng phải giảm xuống 0

        // Kiểm tra các hành vi của các dependency
//        verify(loanTransactionRepository).save(mockLoanTransaction);  // Kiểm tra xem loanTransaction đã được lưu
//        verify(documentRepository).save(mockDocument);  // Kiểm tra xem document đã được lưu
//        verify(userRepository).save(mockUser);  // Kiểm tra xem user đã được lưu
//        verify(notificationService).createNotifications(any());  // Kiểm tra xem thông báo đã được gửi cho người dùng
//        verify(webSocketService).sendLoanStatusUpdate(any(), any());  // Kiểm tra xem thông báo WebSocket đã được gửi
    }

    @Test
    void testReturnDocument_TransactionNotFound() {
        // Giả lập không tìm thấy giao dịch mượn sách
        when(loanTransactionRepository.findById(1L)).thenReturn(Optional.empty());

        // Kiểm tra ngoại lệ khi không tìm thấy giao dịch
        AppException exception = assertThrows(AppException.class, () -> loanTransactionService.returnDocument(1L, true));
        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testReturnDocument_InvalidStatus() {
        // Giả lập trạng thái giao dịch không phải RECEIVED
        mockLoanTransaction.setStatus(LoanTransaction.Status.PENDING);
        when(loanTransactionRepository.findById(1L)).thenReturn(Optional.of(mockLoanTransaction));

        // Kiểm tra ngoại lệ khi trạng thái giao dịch không phải RECEIVED
        AppException exception = assertThrows(AppException.class, () -> loanTransactionService.returnDocument(1L, true));
        assertEquals(ErrorCode.INVALID_REQUEST, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Giao dịch không ở trạng thái RECEIVED"));
    }
}
