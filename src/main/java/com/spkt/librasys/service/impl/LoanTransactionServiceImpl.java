package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.request.loanTransaction.*;
import com.spkt.librasys.dto.request.notification.NotificationCreateRequest;
import com.spkt.librasys.dto.response.LoanTransactionResponse;
import com.spkt.librasys.entity.*;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.LoanTransactionMapper;
import com.spkt.librasys.repository.FineRepository;
import com.spkt.librasys.repository.LoanPolicyRepository;
import com.spkt.librasys.repository.LoanTransactionRepository;
import com.spkt.librasys.repository.RenewalHistoryRepository;
import com.spkt.librasys.repository.document.DocumentRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.repository.specification.LoanTransactionSpecification;
import com.spkt.librasys.service.AuthenticationService;
import com.spkt.librasys.service.LoanTransactionService;
import com.spkt.librasys.service.NotificationService;
import com.spkt.librasys.service.SecurityContextService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoanTransactionServiceImpl implements LoanTransactionService {

    LoanTransactionRepository loanTransactionRepository;
    DocumentRepository documentRepository;
    UserRepository userRepository;
    LoanTransactionMapper loanTransactionMapper;
    SecurityContextService securityContextService;
    FineRepository fineRepository;
    NotificationService notificationService;
    LoanPolicyRepository loanPolicyRepository;
    RenewalHistoryRepository renewalHistoryRepository;

    @Override
    @Transactional
    public LoanTransactionResponse createLoanTransaction(LoanTransactionRequest request) {
        User user =  securityContextService.getCurrentUser();
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        Document document = documentRepository.findById(request.getDocumentId())
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        // Kiểm tra nếu người dùng có bất kỳ giao dịch PENDING nào vượt quá giới hạn số sách có thể mượn
        long pendingTransactionsCount = loanTransactionRepository.countByUserAndStatus(user, LoanTransaction.Status.PENDING);
        if (user.getCurrentBorrowedCount() + pendingTransactionsCount >= user.getMaxBorrowLimit()) {
            throw new AppException(ErrorCode.RESOURCE_CONFLICT, "Bạn không thể mượn thêm vì đã đạt giới hạn số lượng sách tối đa.");
        }

        // Tạo giao dịch mượn sách mới
        LoanTransaction loanTransaction = LoanTransaction.builder()
                .document(document)
                .user(user)
                .status(LoanTransaction.Status.PENDING)
                .loanDate(LocalDateTime.now())
                //.dueDate(LocalDate.now().plusDays(getMaxLoanDays(user, document)))
                .build();

        // Tạo thông báo cho người dùng
        NotificationCreateRequest notificationCreateRequest = NotificationCreateRequest.builder()
                .userId(user.getUserId())
                .title("Yêu cầu mượn sách được tạo")
                .content(String.format("Yêu cầu mượn sách '%s' của bạn đã được tạo và đang chờ phê duyệt.", document.getDocumentName()))
                .build();
        notificationService.createNotification(notificationCreateRequest);

        // Lưu giao dịch vào cơ sở dữ liệu
        LoanTransaction savedTransaction = loanTransactionRepository.save(loanTransaction);
        return loanTransactionMapper.toLoanTransactionResponse(savedTransaction);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public LoanTransactionResponse approveTransaction(Long transactionId) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (!loanTransaction.getStatus().equals(LoanTransaction.Status.PENDING)) {
            throw new AppException(ErrorCode.RESOURCE_CONFLICT, "Giao dịch không ở trạng thái PENDING.");
        }

        Document document = documentRepository.findById(loanTransaction.getDocument().getDocumentId())
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        if (document.getAvailableCount() <= 0) {
            throw new AppException(ErrorCode.RESOURCE_CONFLICT, "Số lượng sách không đủ để mượn.");
        }

        // Giảm số lượng sách có sẵn
        document.setAvailableCount(document.getAvailableCount() - 1);
        documentRepository.save(document);

        // Cập nhật trạng thái giao dịch
        loanTransaction.setStatus(LoanTransaction.Status.APPROVED);
        loanTransaction.setUpdatedAt(LocalDateTime.now());

        // Cập nhật số sách đã mượn của người dùng
        User user = loanTransaction.getUser();
        user.setCurrentBorrowedCount(user.getCurrentBorrowedCount() + 1);
        userRepository.save(user);

        loanTransaction =  loanTransactionRepository.save(loanTransaction);

        // Gửi thông báo
        LocalDateTime expiryTime = loanTransaction.getUpdatedAt().plusDays(2).withHour(0).withMinute(0).withSecond(0).withNano(0);
        NotificationCreateRequest notificationCreateRequest = NotificationCreateRequest.builder()
                .userId(user.getUserId())
                .title("Yêu cầu mượn sách được phê duyệt")
                .content(String.format("Yêu cầu mượn sách '%s' của bạn đã được phê duyệt. " +
                        "Bạn có 48 giờ để nhận sách. Nếu không nhận trong thời gian này, yêu cầu sẽ bị hủy tự động vào lúc 00:00 ngày %s.",
                        loanTransaction.getDocument().getDocumentName(), expiryTime.toLocalDate()))
                .build();
        notificationService.createNotification(notificationCreateRequest);
        return loanTransactionMapper.toLoanTransactionResponse(loanTransaction);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public LoanTransactionResponse rejectTransaction(Long transactionId) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (!loanTransaction.getStatus().equals(LoanTransaction.Status.PENDING)) {
            throw new AppException(ErrorCode.RESOURCE_CONFLICT, "Giao dịch không ở trạng thái PENDING.");
        }

        loanTransaction.setStatus(LoanTransaction.Status.REJECTED);


        LoanTransaction updatedTransaction = loanTransactionRepository.save(loanTransaction);
        // Gửi thông báo từ chối giao dịch
        NotificationCreateRequest notificationCreateRequest = NotificationCreateRequest.builder()
                .userId(loanTransaction.getUser().getUserId())
                .title("Yêu cầu mượn sách bị từ chối")
                .content(String.format("Yêu cầu mượn sách '%s' của bạn đã bị từ chối.", loanTransaction.getDocument().getDocumentName()))
                .build();
        notificationService.createNotification(notificationCreateRequest);

        return loanTransactionMapper.toLoanTransactionResponse(updatedTransaction);
    }
    @Override
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public LoanTransactionResponse receiveDocument(Long transactionId) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        User currentUser =  securityContextService.getCurrentUser();

        // Kiểm tra xem người dùng có phải là người đã thực hiện giao dịch không
        if (!loanTransaction.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Bạn không có quyền nhận sách này.");
        }

        if (!loanTransaction.getStatus().equals(LoanTransaction.Status.APPROVED)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Giao dịch chưa được phê duyệt.");
        }
        // Thiết lập ngày mượn sách và ngày dự kiến trả dựa trên maxLoanDays của tài liệu
//        String role = String.valueOf(currentUser.getRoles().stream()
//                .map(Role::getName)
//                .findFirst());
        LoanPolicy loanPolicy = loanPolicyRepository.findByDocumentType( loanTransaction.getDocument().getDocumentType())
                .orElseThrow(() -> new AppException(ErrorCode.POLICY_NOT_FOUND));


        LocalDateTime currentDate = LocalDateTime.now();
        loanTransaction.setLoanDate(currentDate);
        loanTransaction.setDueDate(currentDate.toLocalDate().plusDays(loanPolicy.getMaxLoanDays()));

        // Cập nhật trạng thái của giao dịch thành RECEIVED
        loanTransaction.setStatus(LoanTransaction.Status.RECEIVED);

        // Lưu lại giao dịch
        LoanTransaction updatedTransaction = loanTransactionRepository.save(loanTransaction);

        // Gửi thông báo nhận sách
        NotificationCreateRequest notificationCreateRequest = NotificationCreateRequest.builder()
                .userId(currentUser.getUserId())
                .title("Sách đã được nhận")
                .content(String.format("Bạn đã nhận sách '%s' thành công.", loanTransaction.getDocument().getDocumentName()))
                .build();
        notificationService.createNotification(notificationCreateRequest);

        return loanTransactionMapper.toLoanTransactionResponse(updatedTransaction);
    }

    @Override
    @Transactional
    public LoanTransactionResponse returnDocument(Long transactionId) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        User currentUser =  securityContextService.getCurrentUser();

        if (!loanTransaction.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Bạn không có quyền trả sách này.");
        }

        if (!loanTransaction.getStatus().equals(LoanTransaction.Status.RECEIVED)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Giao dịch không ở trạng thái nhận sách.");
        }

        // Cập nhật trạng thái giao dịch và số lượng sách khả dụng
        loanTransaction.setStatus(LoanTransaction.Status.RETURN_REQUESTED);
        loanTransaction.setReturnDate(LocalDateTime.now());

        Document document = loanTransaction.getDocument();
        document.setAvailableCount(document.getAvailableCount() + 1);
        documentRepository.save(document);

        // Giảm số sách đã mượn của người dùng
        User user = loanTransaction.getUser();
        user.setCurrentBorrowedCount(user.getCurrentBorrowedCount() - 1);
        userRepository.save(user);

        loanTransactionRepository.save(loanTransaction);

        // Gửi thông báo xác nhận trả sách
        NotificationCreateRequest notificationCreateRequest = NotificationCreateRequest.builder()
                .userId(user.getUserId())
                .title("Sách đã được trả")
                .content(String.format("Bạn đã trả sách '%s' thành công.", loanTransaction.getDocument().getDocumentName()))
                .build();
        notificationService.createNotification(notificationCreateRequest);

        return loanTransactionMapper.toLoanTransactionResponse(loanTransaction);
    }

    @Override
    @Transactional
    public LoanTransactionResponse confirmReturnDocument(LoanTransactionReturnRequest request) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (!loanTransaction.getStatus().equals(LoanTransaction.Status.RETURN_REQUESTED)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Giao dịch không ở trạng thái yêu cầu trả sách.");
        }

        // Cập nhật trạng thái trả sách và ngày trả sách
        loanTransaction.setStatus(LoanTransaction.Status.RETURNED);
        loanTransaction.setReturnDate(LocalDateTime.now());
        loanTransaction.setReturnCondition(LoanTransaction.Condition.NORMAL);

        // Xử lý nếu sách bị hư hỏng hoặc áp dụng khoản phạt
        if (request.getIsBookDamaged()) {
            // Nếu sách bị hư hỏng, áp dụng khoản phạt và không cập nhật availableCount
            loanTransaction.setReturnCondition(LoanTransaction.Condition.DAMAGED);
            Fine fine = Fine.builder()
                    .amount(request.getFineAmount())
                    .status(request.getStatus())
                    .issuedDate(LocalDateTime.now())
                    .reason(request.getFineReason())
                    .transactionLoan(loanTransaction)
                    .user(loanTransaction.getUser())
                    .build();

            fineRepository.save(fine);
        } else {
            // Cập nhật số lượng sách có sẵn nếu sách không bị hư hỏng
            Document document = loanTransaction.getDocument();
            document.setAvailableCount(document.getAvailableCount() + 1);
            documentRepository.save(document);

            // Xử lý nếu có khoản phạt nhưng sách không hư hỏng (ví dụ: phạt trả muộn)
            if (request.getFineAmount() > 0) {
                Fine fine = Fine.builder()
                        .amount(request.getFineAmount())
                        .status(request.getStatus())
                        .issuedDate(LocalDateTime.now())
                        .reason(request.getFineReason())
                        .transactionLoan(loanTransaction)
                        .user(loanTransaction.getUser())
                        .build();

                fineRepository.save(fine);
            }
        }

        LoanTransaction updatedTransaction = loanTransactionRepository.save(loanTransaction);
        // Gửi thông báo xác nhận trả sách
        NotificationCreateRequest notificationCreateRequest = NotificationCreateRequest.builder()
                .userId(loanTransaction.getUser().getUserId())
                .title("Xác nhận trả sách")
                .content(String.format("Sách '%s' đã được trả và xác nhận thành công.", loanTransaction.getDocument().getDocumentName()))
                .build();
        notificationService.createNotification(notificationCreateRequest);

        return loanTransactionMapper.toLoanTransactionResponse(updatedTransaction);
    }
    @Override
    @Transactional
    public LoanTransactionResponse renewLoanTransaction(Long transactionId) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        User currentUser =  securityContextService.getCurrentUser();
        if (!loanTransaction.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Bạn không có quyền gia hạn sách này.");
        }

        if (!loanTransaction.getStatus().equals(LoanTransaction.Status.RECEIVED)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Chỉ có thể gia hạn khi sách đã được nhận.");
        }

        Document document = loanTransaction.getDocument();

        // Kiểm tra nếu sách đã được đặt trước bởi người dùng khác và không có đủ số lượng sách để gia hạn
        long pendingReservations = loanTransactionRepository.countByDocumentAndStatus(document, LoanTransaction.Status.PENDING);
        if (pendingReservations > 0 && document.getAvailableCount() <= pendingReservations) {
            throw new AppException(ErrorCode.RESOURCE_CONFLICT, "Không thể gia hạn vì sách đã được người dùng khác đặt trước và không có đủ số lượng để gia hạn.");
        }

        // Kiểm tra số lần gia hạn đã đạt giới hạn dựa trên lịch sử gia hạn
        List<RenewalHistory> renewalHistories = renewalHistoryRepository.findByLoanTransaction(loanTransaction);
        String role = String.valueOf(currentUser.getRoles().stream()
                .map(Role::getName)
                .findFirst());
        LoanPolicy loanPolicy = loanPolicyRepository.findByDocumentType(document.getDocumentType())
                .orElseThrow(() -> new AppException(ErrorCode.POLICY_NOT_FOUND));
        if (renewalHistories.size() >= loanPolicy.getMaxRenewals()) {
            throw new AppException(ErrorCode.RESOURCE_CONFLICT, "Bạn đã đạt số lần gia hạn tối đa.");
        }

        // Gia hạn thêm số ngày mượn
        LocalDate newDueDate = loanTransaction.getDueDate().plusDays(loanPolicy.getMaxLoanDays());
        loanTransaction.setDueDate(newDueDate);

        LoanTransaction updatedTransaction = loanTransactionRepository.save(loanTransaction);

        // Lưu vào lịch sử gia hạn
        RenewalHistory renewalHistory = RenewalHistory.builder()
                .loanTransaction(loanTransaction)
                .renewalDate(LocalDateTime.now())
                .extendedDueDate(newDueDate)
                .renewedBy(currentUser.getUsername())
                .build();
        renewalHistoryRepository.save(renewalHistory);

        // Gửi thông báo gia hạn thành công
        NotificationCreateRequest notificationCreateRequest = NotificationCreateRequest.builder()
                .userId(loanTransaction.getUser().getUserId())
                .title("Gia hạn sách thành công")
                .content(String.format("Yêu cầu gia hạn sách '%s' đã thành công. Ngày trả mới là: %s", document.getDocumentName(), newDueDate))
                .build();
        notificationService.createNotification(notificationCreateRequest);

        return loanTransactionMapper.toLoanTransactionResponse(updatedTransaction);
    }
    @Override
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public LoanTransactionResponse cancelLoanTransactionByUser(Long transactionId) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        User currentUser =  securityContextService.getCurrentUser();
        if (!loanTransaction.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Bạn không có quyền hủy giao dịch này.");
        }

        // Cho phép hủy ở trạng thái PENDING hoặc APPROVED trong vòng 24 giờ sau khi được phê duyệt
        if (loanTransaction.getStatus() == LoanTransaction.Status.PENDING ||
                (loanTransaction.getStatus() == LoanTransaction.Status.APPROVED &&
                        loanTransaction.getUpdatedAt().isAfter(LocalDateTime.now().minusHours(24)))) {
            loanTransaction.setStatus(LoanTransaction.Status.CANCELLED_BY_USER);

            // Tăng lại số lượng sách có sẵn nếu giao dịch đã được duyệt
            if (loanTransaction.getStatus() == LoanTransaction.Status.APPROVED) {
                Document document = loanTransaction.getDocument();
                document.setAvailableCount(document.getAvailableCount() + 1);
                documentRepository.save(document);
            }

            LoanTransaction updatedTransaction = loanTransactionRepository.save(loanTransaction);

            // Gửi thông báo hủy yêu cầu
            NotificationCreateRequest notificationCreateRequest = NotificationCreateRequest.builder()
                    .userId(loanTransaction.getUser().getUserId())
                    .title("Yêu cầu mượn sách bị hủy")
                    .content(String.format("Yêu cầu mượn sách '%s' của bạn đã bị hủy.", loanTransaction.getDocument().getDocumentName()))
                    .build();
            notificationService.createNotification(notificationCreateRequest);

            return loanTransactionMapper.toLoanTransactionResponse(updatedTransaction);
        } else {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Chỉ có thể hủy yêu cầu ở trạng thái PENDING hoặc APPROVED trong vòng 24 giờ.");
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public LoanTransactionResponse getLoanTransactionById(Long id) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));
        return loanTransactionMapper.toLoanTransactionResponse(loanTransaction);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // Run at 00:00 every day
    public void cancelExpiredApprovedTransactions() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime cutoffTime = currentDateTime.minusDays(2).withHour(0).withMinute(0).withSecond(0).withNano(0); // Thời gian cắt là 0 giờ của 2 ngày trước
        List<LoanTransaction> unreceivedTransactions = loanTransactionRepository
                .findAllByStatusAndCreatedAtBefore(LoanTransaction.Status.APPROVED, cutoffTime);

        for (LoanTransaction transaction : unreceivedTransactions) {
            if (transaction.getUpdatedAt().isBefore(currentDateTime.minusHours(48))) {
                transaction.setStatus(LoanTransaction.Status.CANCELLED_AUTO);
                Document document = transaction.getDocument();
                document.setAvailableCount(document.getAvailableCount() + 1);
                documentRepository.save(document);
                loanTransactionRepository.save(transaction);
                // Xử lý khoản phạt khi người dùng không nhận sách
                Fine fine = Fine.builder()
                        .amount(10.0) // Ví dụ: Số tiền phạt cố định 10 đơn vị tiền tệ
                        .status(Fine.Status.UNPAID)
                        .issuedDate(LocalDateTime.now())
                        .reason("Không nhận sách trong thời gian quy định")
                        .transactionLoan(transaction)
                        .user(transaction.getUser())
                        .build();
                fineRepository.save(fine);


                // Send notification to user about the cancellation
                NotificationCreateRequest notificationCreateRequest = NotificationCreateRequest.builder()
                        .userId(transaction.getUser().getUserId())
                        .title("Yêu cầu mượn sách đã bị hủy tự động")
                        .content(String.format("Yêu cầu mượn sách '%s' của bạn đã bị hủy do không nhận sách trong thời hạn quy định. Hạn cuối để nhận sách là 00:00 ngày %s. Bạn đã bị phạt với số tiền là 10 đơn vị tiền tệ.",
                                document.getDocumentName(), cutoffTime.toLocalDate()))
                        .build();
                notificationService.createNotification(notificationCreateRequest);
            }
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public Page<LoanTransactionResponse> getAllLoanTransactions(LoanTransactionSearchRequest request, Pageable pageable) {
        Specification<LoanTransaction> spec = Specification.where(null);

        if (request.getStatus() != null) {
            spec = spec.and(LoanTransactionSpecification.hasStatus(request.getStatus()));
        }

        if (request.getUsername() != null) {
            spec = spec.and(LoanTransactionSpecification.hasUsername(request.getUsername()));
        }

        if (request.getDocumentName() != null) {
            spec = spec.and(LoanTransactionSpecification.hasDocumentName(request.getDocumentName()));
        }

        Page<LoanTransaction> transactions = loanTransactionRepository.findAll(spec, pageable);
        return transactions.map(loanTransactionMapper::toLoanTransactionResponse);
    }

    @Override
    public boolean isUserBorrowingDocument(Long documentId) {
        User user = securityContextService.getCurrentUser();
        if(user == null)
           throw  new AppException(ErrorCode.USER_NOT_FOUND);
        Document document = documentRepository.findById(documentId).orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));
        // Kiểm tra xem có giao dịch nào với documentId và trạng thái không phải RETURNED hoặc REJECTED
        Collection<LoanTransaction.Status> excludedStatuses = List.of(LoanTransaction.Status.RETURNED, LoanTransaction.Status.REJECTED);
        return loanTransactionRepository.existsByUserAndDocumentAndStatusNotIn(user, document, excludedStatuses);

    }

    @Override
    public Page<LoanTransactionResponse> getUserBorrowedBooks(Pageable pageable) {
        User user = securityContextService.getCurrentUser();
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        // Tìm tất cả các giao dịch của người dùng và phân trang
        Page<LoanTransaction> transactions = loanTransactionRepository.findByUser(user, pageable);

        // Chuyển đổi từ Page<LoanTransaction> sang Page<LoanTransactionResponse>
        return transactions.map(loanTransactionMapper::toLoanTransactionResponse);
    }

    //
    private int getMaxLoanDays(User user, Document document) {
//        String role= String.valueOf(user.getRoles().stream()
//                .map(Role::getName)
//                .findFirst());
        LoanPolicy loanPolicy = loanPolicyRepository.findByDocumentType(
                        document.getDocumentType())
                .orElseThrow(() -> new AppException(ErrorCode.POLICY_NOT_FOUND));
        return loanPolicy.getMaxLoanDays();
    }


}
