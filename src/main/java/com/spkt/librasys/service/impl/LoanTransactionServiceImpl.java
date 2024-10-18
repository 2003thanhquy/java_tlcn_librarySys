package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.request.loanTransaction.LoanTransactionRequest;
import com.spkt.librasys.dto.response.LoanTransactionResponse;
import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.Email;
import com.spkt.librasys.entity.LoanTransaction;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.entity.enums.LoanTransactionStatus;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.LoanTransactionMapper;
import com.spkt.librasys.repository.EmailRepository;
import com.spkt.librasys.repository.LoanTransactionRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.repository.document.DocumentRepository;
import com.spkt.librasys.repository.specification.LoanTransactionSpecification;
import com.spkt.librasys.service.EmailService;
import com.spkt.librasys.service.LoanTransactionService;
import com.spkt.librasys.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
    EmailService emailService;
    EmailRepository emailRepository;
    NotificationService notificationService;

    @Override
    @PreAuthorize("hasRole('USER')")
    public LoanTransactionResponse createLoanTransaction(LoanTransactionRequest request) {
        User user = getCurrentUser();

        Document document = documentRepository.findById(request.getDocumentId())
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        if (document.getAvailableCount() <= 0) {
            throw new AppException(ErrorCode.RESOURCE_CONFLICT, "Số lượng sách không đủ để mượn");
        }

        // Tạo yêu cầu mượn sách với trạng thái mặc định là PENDING
        LoanTransaction loanTransaction = LoanTransaction.builder()
                .document(document)
                .user(user)
                .dueDate(request.getDueDate())
                .status(LoanTransactionStatus.PENDING)
                .build();

        LoanTransaction savedTransaction = loanTransactionRepository.save(loanTransaction);
        return loanTransactionMapper.toLoanTransactionResponse(savedTransaction);
    }

    @Override
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public LoanTransactionResponse approveTransaction(Long transactionId, boolean isApproved) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        // Kiểm tra nếu trạng thái hiện tại đã giống với yêu cầu
        if ((isApproved && loanTransaction.getStatus() == LoanTransactionStatus.APPROVED) ||
                (!isApproved && loanTransaction.getStatus() == LoanTransactionStatus.REJECTED)) {
            throw new AppException(ErrorCode.RESOURCE_CONFLICT, "Trạng thái hiện tại đã giống với yêu cầu.");
        }

        if (isApproved) {
            loanTransaction.setStatus(LoanTransactionStatus.APPROVED);
            loanTransaction.setCreatedAt(LocalDateTime.now());

            // Giảm số lượng sách có sẵn
            Document document = loanTransaction.getDocument();
            document.setAvailableCount(document.getAvailableCount() - 1);
            documentRepository.save(document);
        } else {
            loanTransaction.setStatus(LoanTransactionStatus.REJECTED);
        }

        LoanTransaction updatedTransaction = loanTransactionRepository.save(loanTransaction);
        sendNotificationEmail(loanTransaction, isApproved);
        //notification
        String notificationTitle = isApproved ? "Yêu cầu mượn sách đã được phê duyệt" : "Yêu cầu mượn sách đã bị từ chối";
        String notificationContent = String.format("Yêu cầu mượn sách với tiêu đề '%s' của bạn đã %s.",
                loanTransaction.getDocument().getDocumentName(),
                isApproved ? "được phê duyệt" : "bị từ chối");
       // notificationService.createNotification(loanTransaction.getUser().getUserId(), notificationTitle, notificationContent);

        return loanTransactionMapper.toLoanTransactionResponse(updatedTransaction);
    }

    @Override
    public LoanTransactionResponse receiveDocument(Long transactionId) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (!loanTransaction.getStatus().equals(LoanTransactionStatus.APPROVED)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Giao dịch chưa được phê duyệt.");
        }

        loanTransaction.setLoanDate(LocalDate.now());
        LoanTransaction updatedTransaction = loanTransactionRepository.save(loanTransaction);
        return loanTransactionMapper.toLoanTransactionResponse(updatedTransaction);
    }

    @Override
    public LoanTransactionResponse returnDocument(Long transactionId) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (loanTransaction.getStatus() != LoanTransactionStatus.APPROVED) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Giao dịch không ở trạng thái mượn.");
        }

        loanTransaction.setReturnDate(LocalDate.now());
        loanTransaction.getDocument().setAvailableCount(loanTransaction.getDocument().getAvailableCount() + 1);
        Document updatedDocument = documentRepository.save(loanTransaction.getDocument());
        LoanTransaction updatedTransaction = loanTransactionRepository.save(loanTransaction);

        return loanTransactionMapper.toLoanTransactionResponse(updatedTransaction);
    }

    @Override
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public LoanTransactionResponse getLoanTransactionById(Long id) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));
        return loanTransactionMapper.toLoanTransactionResponse(loanTransaction);
    }

    @Override
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public Page<LoanTransactionResponse> getAllLoanTransactions(String status, String username, String documentName, Pageable pageable) {
        LoanTransactionStatus transactionStatus = null;

        if (status != null) {
            try {
                transactionStatus = LoanTransactionStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Trạng thái không hợp lệ.");
            }
        }

        Specification<LoanTransaction> specification = Specification.where(LoanTransactionSpecification.hasStatus(transactionStatus))
                .and(LoanTransactionSpecification.hasUsername(username))
                .and(LoanTransactionSpecification.hasDocumentName(documentName));

        Page<LoanTransaction> loanTransactions = loanTransactionRepository.findAll(specification, pageable);
        return loanTransactions.map(loanTransactionMapper::toLoanTransactionResponse);
    }

    @Override
    public void cancelExpiredApprovedTransactions() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        List<LoanTransaction> expiredTransactions = loanTransactionRepository
                .findAllByStatusAndCreatedAtBefore(LoanTransactionStatus.APPROVED, cutoffTime);

        for (LoanTransaction transaction : expiredTransactions) {
            transaction.setStatus(LoanTransactionStatus.USER_CANCELLED);
            loanTransactionRepository.save(transaction);
            sendCancellationEmail(transaction, "Yêu cầu mượn sách đã bị tự động hủy vì không được nhận trong vòng 24 giờ.");
        }
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public LoanTransactionResponse cancelLoanTransactionByUser(Long transactionId) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (loanTransaction.getStatus() == LoanTransactionStatus.APPROVED) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Không thể hủy yêu cầu đã được phê duyệt.");
        }

        loanTransaction.setStatus(LoanTransactionStatus.USER_CANCELLED);
        LoanTransaction updatedTransaction = loanTransactionRepository.save(loanTransaction);

        // Gửi email thông báo người dùng hủy thành công
        sendCancellationEmail(loanTransaction, "Yêu cầu mượn sách đã bị hủy thành công bởi người dùng.");

        return loanTransactionMapper.toLoanTransactionResponse(updatedTransaction);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private void sendNotificationEmail(LoanTransaction loanTransaction, boolean isApproved) {
        String email = loanTransaction.getUser().getUsername();
        String subject = isApproved ? "Yêu cầu mượn sách đã được phê duyệt" : "Yêu cầu mượn sách đã bị từ chối";
        String content = String.format("Xin chào, %s,\n\nYêu cầu mượn sách với tiêu đề '%s' của bạn đã %s.\n\nTrân trọng,\nThư viện",
                loanTransaction.getUser().getFirstName(),
                loanTransaction.getDocument().getDocumentName(),
                isApproved ? "được phê duyệt" : "bị từ chối");

        Email notificationEmail = Email.builder()
                .toEmail(email)
                .subject(subject)
                .body(content)
                .createdAt(LocalDateTime.now())
                .build();

        emailService.sendTextEmail(notificationEmail).thenAccept(isSuccess -> {
            if (!isSuccess) {
                notificationEmail.setStatus("FAILED");
            }
            emailRepository.save(notificationEmail);
        });
    }

    private void sendCancellationEmail(LoanTransaction loanTransaction, String message) {
        String email = loanTransaction.getUser().getUsername();
        String subject = "Thông báo hủy yêu cầu mượn sách";
        String content = String.format("Xin chào, %s,\n\n%s với tiêu đề '%s'.\n\nTrân trọng,\nThư viện",
                loanTransaction.getUser().getFirstName(), message, loanTransaction.getDocument().getDocumentName());

        Email notificationEmail = Email.builder()
                .toEmail(email)
                .subject(subject)
                .body(content)
                .createdAt(LocalDateTime.now())
                .build();

        emailService.sendTextEmail(notificationEmail).thenAccept(isSuccess -> {
            if (!isSuccess) {
                notificationEmail.setStatus("FAILED");
            }
            emailRepository.save(notificationEmail);
        });
    }
}
