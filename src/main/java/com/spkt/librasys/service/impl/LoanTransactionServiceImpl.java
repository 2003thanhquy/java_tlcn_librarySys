package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.request.loanTransaction.*;
import com.spkt.librasys.dto.request.notification.NotificationCreateRequest;
import com.spkt.librasys.dto.response.LoanTransactionResponse;
import com.spkt.librasys.entity.*;
import com.spkt.librasys.entity.enums.LoanTransactionStatus;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.LoanTransactionMapper;
import com.spkt.librasys.repository.FineRepository;
import com.spkt.librasys.repository.LoanTransactionRepository;
import com.spkt.librasys.repository.document.DocumentRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.repository.specification.LoanTransactionSpecification;
import com.spkt.librasys.service.AuthenticationService;
import com.spkt.librasys.service.LoanTransactionService;
import com.spkt.librasys.service.NotificationService;
import jakarta.annotation.security.RolesAllowed;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    AuthenticationService authenticationService;
    FineRepository fineRepository;
    NotificationService notificationService;

    @Override
    @Transactional
    public LoanTransactionResponse createLoanTransaction(LoanTransactionRequest request) {
        User user = authenticationService.getCurrentUser();
        if(user == null)
            throw new AppException(ErrorCode.USER_NOT_FOUND);

        Document document = documentRepository.findById(request.getDocumentId())
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        if (document.getAvailableCount() <= 0) {
            throw new AppException(ErrorCode.RESOURCE_CONFLICT, "Số lượng sách không đủ để mượn");
        }
        // Trừ số lượng sách có sẵn khi yêu cầu mượn được tạo (PENDING)
        document.setAvailableCount(document.getAvailableCount() - 1);
        documentRepository.save(document);

        LoanTransaction loanTransaction = LoanTransaction.builder()
                .document(document)
                .user(user)
                .status(LoanTransactionStatus.PENDING)
                .build();

        // Gửi thông báo về việc tạo giao dịch
        NotificationCreateRequest notificationCreateRequest = NotificationCreateRequest.builder()
                .userId(user.getUserId())
                .title("Yêu cầu mượn sách được tạo")
                .content(String.format("Yêu cầu mượn sách '%s' của bạn đã được tạo và đang chờ phê duyệt.", document.getDocumentName()))
                .build();
        notificationService.createNotification(notificationCreateRequest);

        LoanTransaction savedTransaction = loanTransactionRepository.save(loanTransaction);
        return loanTransactionMapper.toLoanTransactionResponse(savedTransaction);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public LoanTransactionResponse approveTransaction(Long transactionId) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (!loanTransaction.getStatus().equals(LoanTransactionStatus.PENDING)) {
            throw new AppException(ErrorCode.RESOURCE_CONFLICT, "Giao dịch không ở trạng thái PENDING.");
        }

        loanTransaction.setStatus(LoanTransactionStatus.APPROVED);
        loanTransaction.setCreatedAt(LocalDateTime.now());
        LoanTransaction updatedTransaction = loanTransactionRepository.save(loanTransaction);

        // Gửi thông báo phê duyệt giao dịch
        NotificationCreateRequest notificationCreateRequest = NotificationCreateRequest.builder()
                .userId(loanTransaction.getUser().getUserId())
                .title("Yêu cầu mượn sách được phê duyệt")
                .content(String.format("Yêu cầu mượn sách '%s' của bạn đã được phê duyệt.", loanTransaction.getDocument().getDocumentName()))
                .build();
        notificationService.createNotification(notificationCreateRequest);


        return loanTransactionMapper.toLoanTransactionResponse(updatedTransaction);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public LoanTransactionResponse rejectTransaction(Long transactionId) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (!loanTransaction.getStatus().equals(LoanTransactionStatus.PENDING)) {
            throw new AppException(ErrorCode.RESOURCE_CONFLICT, "Giao dịch không ở trạng thái PENDING.");
        }

        loanTransaction.setStatus(LoanTransactionStatus.REJECTED);
        // Tăng lại số lượng sách có sẵn
        Document document = loanTransaction.getDocument();
        document.setAvailableCount(document.getAvailableCount() + 1);
        documentRepository.save(document);

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
    public LoanTransactionResponse receiveDocument(Long transactionId) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        User currentUser = authenticationService.getCurrentUser();

        // Kiểm tra xem người dùng có phải là người đã thực hiện giao dịch không
        if (!loanTransaction.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Bạn không có quyền nhận sách này.");
        }

        if (!loanTransaction.getStatus().equals(LoanTransactionStatus.APPROVED)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Giao dịch chưa được phê duyệt.");
        }
        // Thiết lập ngày mượn sách và ngày dự kiến trả dựa trên maxLoanDays của tài liệu
        LocalDateTime currentDate = LocalDateTime.now();
        loanTransaction.setLoanDate(currentDate);
        loanTransaction.setDueDate(currentDate.toLocalDate().plusDays(loanTransaction.getDocument().getMaxLoanDays()));

        // Cập nhật trạng thái của giao dịch thành RECEIVED
        loanTransaction.setStatus(LoanTransactionStatus.RECEIVED);

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
        User currentUser = authenticationService.getCurrentUser();

        // Kiểm tra xem người dùng có phải là người đã thực hiện giao dịch không
        if (!loanTransaction.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Bạn không có quyền trả sách này.");
        }

        if (!loanTransaction.getStatus().equals(LoanTransactionStatus.RECEIVED)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Giao dịch không ở trạng thái nhận sách.");
        }

//        loanTransaction.setStatus(LoanTransactionStatus.RETURNED);
//        loanTransaction.setReturnDate(LocalDateTime.now());
//        Document document = loanTransaction.getDocument();
//        document.setAvailableCount(document.getAvailableCount() + 1);
//        documentRepository.save(document);
        loanTransaction.setStatus(LoanTransactionStatus.RETURN_REQUESTED);
        LoanTransaction updatedTransaction = loanTransactionRepository.save(loanTransaction);

        // Gửi thông báo yêu cầu trả sách
        NotificationCreateRequest notificationCreateRequest = NotificationCreateRequest.builder()
                .userId(currentUser.getUserId())
                .title("Yêu cầu trả sách")
                .content(String.format("Bạn đã yêu cầu trả sách '%s'.", loanTransaction.getDocument().getDocumentName()))
                .build();
        notificationService.createNotification(notificationCreateRequest);

        return loanTransactionMapper.toLoanTransactionResponse(updatedTransaction);
    }

    @Override
    @Transactional
    public LoanTransactionResponse confirmReturnDocument(LoanTransactionReturnRequest request) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (!loanTransaction.getStatus().equals(LoanTransactionStatus.RETURN_REQUESTED)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Giao dịch không ở trạng thái yêu cầu trả sách.");
        }

        // Cập nhật trạng thái trả sách và ngày trả sách
        loanTransaction.setStatus(LoanTransactionStatus.RETURNED);
        loanTransaction.setReturnDate(LocalDateTime.now());

        // Xử lý nếu sách bị hư hỏng hoặc áp dụng khoản phạt
        if (request.getIsBookDamaged()) {
            // Nếu sách bị hư hỏng, áp dụng khoản phạt và không cập nhật availableCount
            Fine fine = Fine.builder()
                    .amount(request.getFineAmount())
                    .status(request.getFineStatus())
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
                        .status(request.getFineStatus())
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
    public LoanTransactionResponse cancelLoanTransactionByUser(Long transactionId) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (loanTransaction.getStatus() != LoanTransactionStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Chỉ có thể hủy yêu cầu ở trạng thái PENDING.");
        }

        loanTransaction.setStatus(LoanTransactionStatus.CANCELLED);
        // Tăng lại số lượng sách có sẵn
        Document document = loanTransaction.getDocument();
        document.setAvailableCount(document.getAvailableCount() + 1);
        documentRepository.save(document);

        LoanTransaction updatedTransaction = loanTransactionRepository.save(loanTransaction);
        // Gửi thông báo hủy yêu cầu
        NotificationCreateRequest notificationCreateRequest = NotificationCreateRequest.builder()
                .userId(loanTransaction.getUser().getUserId())
                .title("Yêu cầu mượn sách bị hủy")
                .content(String.format("Yêu cầu mượn sách '%s' của bạn đã bị hủy.", loanTransaction.getDocument().getDocumentName()))
                .build();
        notificationService.createNotification(notificationCreateRequest);

        return loanTransactionMapper.toLoanTransactionResponse(updatedTransaction);
    }

    @Override
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public LoanTransactionResponse getLoanTransactionById(Long id) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));
        return loanTransactionMapper.toLoanTransactionResponse(loanTransaction);
    }

    @Override
    public void cancelExpiredApprovedTransactions() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        List<LoanTransaction> expiredTransactions = loanTransactionRepository
                .findAllByStatusAndCreatedAtBefore(LoanTransactionStatus.APPROVED, cutoffTime);

        for (LoanTransaction transaction : expiredTransactions) {
            transaction.setStatus(LoanTransactionStatus.CANCELLED);
            Document document = transaction.getDocument();
            document.setAvailableCount(document.getAvailableCount() + 1);
            documentRepository.save(document);
            loanTransactionRepository.save(transaction);
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
}
