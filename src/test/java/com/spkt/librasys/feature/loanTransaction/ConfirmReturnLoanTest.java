package com.spkt.librasys.feature.loanTransaction;

import com.spkt.librasys.dto.request.loanTransaction.LoanTransactionReturnRequest;
import com.spkt.librasys.dto.response.LoanTransactionResponse;
import com.spkt.librasys.entity.*;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.LoanTransactionMapper;
import com.spkt.librasys.repository.*;
import com.spkt.librasys.service.NotificationService;
import com.spkt.librasys.service.WebSocketService;
import com.spkt.librasys.service.impl.LoanTransactionServiceImpl;
import com.spkt.librasys.service.SecurityContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ConfirmReturnLoanTest {

    @Mock
    private LoanTransactionRepository loanTransactionRepository;
    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private FineRepository fineRepository;
    @Mock
    private RackRepository rackRepository;
    @Mock
    private WarehouseRepository warehouseRepository;
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

    private LoanTransactionReturnRequest returnRequest;
    private User mockUser;
    private Document mockDocument;
    private LoanTransaction mockLoanTransaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Khởi tạo đối tượng mock cho yêu cầu trả sách
        returnRequest = new LoanTransactionReturnRequest();
        returnRequest.setTransactionId(1L);
        returnRequest.setIsBookDamaged(false);
        returnRequest.setFineAmount(10000.0);
        returnRequest.setFineReason("None");

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

        mockDocument.setLocations(List.of(DocumentLocation.builder()
                .quantity(10)
                .warehouseId(1L)
                .build()));

        // Khởi tạo LoanTransaction
        mockLoanTransaction = LoanTransaction.builder()
                .originalWarehouseId(1L)
                .document(mockDocument)
                .user(mockUser)
                .status(LoanTransaction.Status.RETURN_REQUESTED)
                .loanDate(LocalDateTime.now())
                .build();

        // Giả lập hành vi của các dependency
        when(securityContextService.getCurrentUser()).thenReturn(mockUser);
        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));
        when(loanTransactionRepository.findById(1L)).thenReturn(Optional.of(mockLoanTransaction));
        when(loanTransactionRepository.save(any(LoanTransaction.class))).thenReturn(mockLoanTransaction);
        when(fineRepository.save(any(Fine.class))).thenReturn(mock(Fine.class));
        when(loanTransactionMapper.toLoanTransactionResponse(mockLoanTransaction)).thenReturn(
                LoanTransactionResponse.builder()
                        .documentName(mockLoanTransaction.getDocument().getDocumentName())
                        .status(mockLoanTransaction.getStatus()).build());
    }

    @Test
    void testConfirmReturnDocument_Success() {
        // Giả lập khi giao dịch ở trạng thái RETURN_REQUESTED và sách không bị hư hỏng
        Warehouse warehouse = Warehouse.builder()
                .warehouseId(1L)
                .warehouseName("warehouse")
                .location("A1 ,A2").build();
        when(loanTransactionRepository.findById(1L)).thenReturn(Optional.of(mockLoanTransaction));
        when(documentRepository.findById(mockLoanTransaction.getDocument().getDocumentId())).thenReturn(Optional.of(mockDocument));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        // Gọi phương thức confirmReturnDocument
        LoanTransactionResponse response = loanTransactionService.confirmReturnDocument(returnRequest);

        // Kiểm tra phản hồi
        assertNotNull(response);
        assertEquals(LoanTransaction.Status.RETURNED, mockLoanTransaction.getStatus());
        assertEquals(5, mockDocument.getAvailableCount());  // Số lượng sách có sẵn phải tăng lên 5
        assertEquals(0, mockUser.getCurrentBorrowedCount());  // Số sách đã mượn của người dùng phải giảm xuống 0

   }

    @Test
    void testConfirmReturnDocument_TransactionNotFound() {
        // Giả lập không tìm thấy giao dịch mượn sách
        when(loanTransactionRepository.findById(1L)).thenReturn(Optional.empty());

        // Kiểm tra ngoại lệ khi không tìm thấy giao dịch
        AppException exception = assertThrows(AppException.class, () -> loanTransactionService.confirmReturnDocument(returnRequest));
        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testConfirmReturnDocument_InvalidStatus() {
        // Giả lập trạng thái giao dịch không phải RETURN_REQUESTED
        mockLoanTransaction.setStatus(LoanTransaction.Status.PENDING);
        when(loanTransactionRepository.findById(1L)).thenReturn(Optional.of(mockLoanTransaction));

        // Kiểm tra ngoại lệ khi trạng thái giao dịch không phải RETURN_REQUESTED
        AppException exception = assertThrows(AppException.class, () -> loanTransactionService.confirmReturnDocument(returnRequest));
        assertEquals(ErrorCode.INVALID_REQUEST, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Giao dịch không ở trạng thái yêu cầu trả sách"));
    }

    @Test
    void testConfirmReturnDocument_BookDamaged() {
        // Giả lập khi sách bị hư hỏng
        returnRequest.setIsBookDamaged(true);
        returnRequest.setFineAmount(10000.0);

        when(loanTransactionRepository.findById(1L)).thenReturn(Optional.of(mockLoanTransaction));
        when(documentRepository.findById(mockLoanTransaction.getDocument().getDocumentId())).thenReturn(Optional.of(mockDocument));

        // Gọi phương thức confirmReturnDocument
        LoanTransactionResponse response = loanTransactionService.confirmReturnDocument(returnRequest);

        // Kiểm tra phản hồi
        assertNotNull(response);
        assertEquals(LoanTransaction.Status.RETURNED, mockLoanTransaction.getStatus());

        // Kiểm tra các hành vi của các dependency
//        verify(fineRepository).save(any(Fine.class));  // Kiểm tra xem phạt đã được tạo
    }
}
