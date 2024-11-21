package com.spkt.librasys.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.spkt.librasys.constant.PredefinedRole;
import com.spkt.librasys.dto.request.loanTransaction.*;
import com.spkt.librasys.dto.request.notification.NotificationCreateRequest;
import com.spkt.librasys.dto.response.LoanTransactionResponse;
import com.spkt.librasys.entity.*;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.LoanTransactionMapper;
import com.spkt.librasys.repository.*;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.repository.specification.LoanTransactionSpecification;
import com.spkt.librasys.service.DocumentMoveService;
import com.spkt.librasys.service.LoanTransactionService;
import com.spkt.librasys.service.NotificationService;
import com.spkt.librasys.service.SecurityContextService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
    DocumentMoveService documentMoveService;
    RackRepository rackRepository;
    WarehouseRepository warehouseRepository;

    @NonFinal
    @Value("${jwt.signer-key}")
    protected String SIGNER_KEY;

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
                .userIds(List.of(user.getUserId()))
                .title("Yêu cầu mượn sách được tạo")
                .content(String.format("Yêu cầu mượn sách '%s' của bạn đã được tạo và đang chờ phê duyệt.", document.getDocumentName()))
                .build();
        notificationService.createNotifications(notificationCreateRequest);

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
        System.out.println("document"+document.getAvailableCount());
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
        // thuc hien lay sach tu Rack , WareHouse
        DocumentLocation location = documentMoveService.approveAndMoveDocument(document.getDocumentId(),1);
        if(location.getRackId() != null){
            loanTransaction.setOriginalRackId(location.getRackId());
        }else loanTransaction.setOriginalWarehouseId(location.getWarehouseId());
        // 12. Gửi thông báo cho người duyệt (Manager/Admin) về vị trí lấy sách
        User userCurrent = securityContextService.getCurrentUser();
        String locationDetails = "";
        if (loanTransaction.getOriginalRackId() != null) {
            Rack rack = rackRepository.findById(loanTransaction.getOriginalRackId())
                    .orElseThrow(() -> new AppException(ErrorCode.RACK_NOT_FOUND, "Rack gốc không tồn tại"));
            locationDetails = String.format("Kệ ID: %d, Số kệ: %s", rack.getRackId(), rack.getRackNumber());
        } else if (loanTransaction.getOriginalWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(loanTransaction.getOriginalWarehouseId())
                    .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND, "Kho gốc không tồn tại"));
            locationDetails = String.format("Kho ID: %d, Địa điểm: %s", warehouse.getWarehouseId(), warehouse.getLocation());
        }
        String managerMessage = String.format("Bạn đã phê duyệt yêu cầu mượn sách '%s'. Vị trí lấy sách: %s. Vui lòng chuẩn bị sách để giao cho người dùng.",
                document.getDocumentName(), locationDetails);

        loanTransaction =  loanTransactionRepository.save(loanTransaction);

        NotificationCreateRequest managerNotification = NotificationCreateRequest.builder()
                .userIds(List.of(userCurrent.getUserId()))
                .title("Phê duyệt yêu cầu mượn sách")
                .content(managerMessage)
                .build();
        notificationService.createNotifications(managerNotification);


        // Gửi thông báo User
        LocalDateTime expiryTime = loanTransaction.getUpdatedAt().plusDays(2).withHour(0).withMinute(0).withSecond(0).withNano(0);
        NotificationCreateRequest notificationCreateRequest = NotificationCreateRequest.builder()
                .userIds(List.of(user.getUserId()))
                .title("Yêu cầu mượn sách được phê duyệt")
                .content(String.format("Yêu cầu mượn sách '%s' của bạn đã được phê duyệt. " +
                        "Bạn có 48 giờ để nhận sách. Nếu không nhận trong thời gian này, yêu cầu sẽ bị hủy tự động vào lúc 00:00 ngày %s.",
                        loanTransaction.getDocument().getDocumentName(), expiryTime.toLocalDate()))
                .build();
        notificationService.createNotifications(notificationCreateRequest);

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
                .userIds(List.of( loanTransaction.getUser().getUserId()))
                .title("Yêu cầu mượn sách bị từ chối")
                .content(String.format("Yêu cầu mượn sách '%s' của bạn đã bị từ chối.", loanTransaction.getDocument().getDocumentName()))
                .build();
        notificationService.createNotifications(notificationCreateRequest);

        return loanTransactionMapper.toLoanTransactionResponse(updatedTransaction);
    }
    @Override
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public LoanTransactionResponse receiveDocument(Long transactionId, boolean isUser) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        if(isUser){
            validateUserPermissions(loanTransaction,LoanTransaction.Status.APPROVED);
        }
        // Thiết lập ngày mượn sách và ngày dự kiến trả dựa trên maxLoanDays của tài liệu

        // Lấy các DocumentType của Document
        Document document = loanTransaction.getDocument();
        Set<DocumentType> documentTypes = document.getDocumentTypes();

        // Lấy tất cả LoanPolicy cho các DocumentType này và chọn cái có maxLoanDays thấp nhất
        LoanPolicy loanPolicy = loanPolicyRepository.findByDocumentTypeIn(documentTypes)
                .stream()
                .min(Comparator.comparingInt(LoanPolicy::getMaxLoanDays))
                .orElseThrow(() -> new AppException(ErrorCode.POLICY_NOT_FOUND, "Không tìm thấy chính sách phù hợp"));



        LocalDateTime currentDate = LocalDateTime.now();
        loanTransaction.setLoanDate(currentDate);
        loanTransaction.setDueDate(currentDate.toLocalDate().plusDays(loanPolicy.getMaxLoanDays()));

        // Cập nhật trạng thái của giao dịch thành RECEIVED
        loanTransaction.setStatus(LoanTransaction.Status.RECEIVED);

        // Lưu lại giao dịch
        LoanTransaction updatedTransaction = loanTransactionRepository.save(loanTransaction);

        // Gửi thông báo nhận sách
        NotificationCreateRequest notificationCreateRequest = NotificationCreateRequest.builder()
                .userIds(List.of(loanTransaction.getUser().getUserId()))
                .title("Sách đã được nhận")
                .content(String.format("Bạn đã nhận sách '%s' thành công.", loanTransaction.getDocument().getDocumentName()))
                .build();
        notificationService.createNotifications(notificationCreateRequest);

        return loanTransactionMapper.toLoanTransactionResponse(updatedTransaction);
    }

    @Override
    @Transactional
    public LoanTransactionResponse returnDocument(Long transactionId, boolean isUser) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));
        if (isUser) {
            validateUserPermissions(loanTransaction, LoanTransaction.Status.RECEIVED);
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
                .userIds(List.of(user.getUserId()))
                .title("Sách đã được trả")
                .content(String.format("Bạn đã trả sách '%s' thành công.", loanTransaction.getDocument().getDocumentName()))
                .build();
        notificationService.createNotifications(notificationCreateRequest);

        return loanTransactionMapper.toLoanTransactionResponse(loanTransaction);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public LoanTransactionResponse confirmReturnDocument(LoanTransactionReturnRequest request) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (!loanTransaction.getStatus().equals(LoanTransaction.Status.RETURN_REQUESTED)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Giao dịch không ở trạng thái yêu cầu trả sách.");
        }

        // Cập nhật trạng thái trả sách và ngày trả sách
        loanTransaction.setStatus(LoanTransaction.Status.RETURNED);
        loanTransaction.setReturnDate(LocalDateTime.now());
        loanTransaction.setReturnCondition(request.getIsBookDamaged() ? LoanTransaction.Condition.DAMAGED : LoanTransaction.Condition.NORMAL);

        Document document = loanTransaction.getDocument();
        boolean isDocumentDamaged = request.getIsBookDamaged();

        if (isDocumentDamaged) {
            // Xử lý sách bị hư hỏng
            Fine fine = Fine.builder()
                    .amount(request.getFineAmount())
                    .status(Fine.Status.UNPAID)
                    .issuedDate(LocalDateTime.now())
                    .reason(request.getFineReason())
                    .transactionLoan(loanTransaction)
                    .user(loanTransaction.getUser())
                    .build();
            fineRepository.save(fine);
        } else {
            // Cập nhật số lượng sách có sẵn
            document.setAvailableCount(document.getAvailableCount() + 1);

            boolean returnedToOriginalLocation = false;
            User userCurrent = securityContextService.getCurrentUser();
            String title = "";
            String message = "";
            if (loanTransaction.getOriginalRackId() != null) {
                // Trả sách về kệ gốc
                Rack originalRack = rackRepository.findById(loanTransaction.getOriginalRackId())
                        .orElseThrow(() -> new AppException(ErrorCode.RACK_NOT_FOUND, "Kệ gốc không tồn tại."));

                double currentRackSize = document.getLocations().stream()
                        .filter(loc -> loc.getRackId() != null && loc.getRackId().equals(originalRack.getRackId()))
                        .mapToDouble(loc -> loc.getSize().getSizeValue() * loc.getQuantity())
                        .sum();
                double documentSize = document.getSize().getSizeValue();

                if (currentRackSize + documentSize <= originalRack.getCapacity()) {
                    // Có đủ chỗ, trả sách về kệ
                    DocumentLocation rackLocation = document.getLocations().stream()
                            .filter(loc -> loc.getRackId() != null && loc.getRackId().equals(originalRack.getRackId()))
                            .findFirst()
                            .orElseGet(() -> {
                                DocumentLocation newLocation = DocumentLocation.builder()
                                        .rackId(originalRack.getRackId())
                                        .quantity(0)
                                        .size(document.getSize())
                                        .build();
                                document.getLocations().add(newLocation);
                                return newLocation;
                            });
                    rackLocation.setQuantity(rackLocation.getQuantity() + 1);
                    rackLocation.updateTotalSize();
                    returnedToOriginalLocation = true;
                    // Thiết lập tiêu đề và nội dung thông báo khi trả sách về kệ gốc
                    title = "Sách được trả về kệ";
                    message = String.format("Sách '%s' đã được trả về kệ '%s'.", document.getDocumentName(), originalRack.getRackNumber());

                }
            }

            if (!returnedToOriginalLocation) {
                // Trả sách vào kho
                Long warehouseId = loanTransaction.getOriginalWarehouseId();
                if (warehouseId == null) {
                    // Nếu không có kho gốc, chọn kho mặc định
                    Warehouse defaultWarehouse = warehouseRepository.findAll().stream().findFirst()
                            .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND, "Không tìm thấy kho lưu trữ."));
                    warehouseId = defaultWarehouse.getWarehouseId();
                }

                Warehouse warehouse = warehouseRepository.findById(warehouseId)
                        .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND, "Kho không tồn tại."));

                DocumentLocation warehouseLocation = document.getLocations().stream()
                        .filter(loc -> loc.getWarehouseId() != null && loc.getWarehouseId().equals(warehouse.getWarehouseId()))
                        .findFirst()
                        .orElseGet(() -> {
                            DocumentLocation newLocation = DocumentLocation.builder()
                                    .warehouseId(warehouse.getWarehouseId())
                                    .quantity(0)
                                    .size(document.getSize())
                                    .build();
                            document.getLocations().add(newLocation);
                            return newLocation;
                        });
                warehouseLocation.setQuantity(warehouseLocation.getQuantity() + 1);
                warehouseLocation.updateTotalSize();
                // Gửi thông báo cho quản lý
               message = String.format("Sách '%s' đã được trả về kho '%s' vì kệ gốc đã đầy.",
                        document.getDocumentName(), warehouse.getWarehouseName());
               title = "Sách trả về kho";
            }
            documentRepository.save(document);

            NotificationCreateRequest managerNotification = NotificationCreateRequest.builder()
                    .userIds(List.of(userCurrent.getUserId()))
                    .title(title)
                    .content(message)
                    .build();
            notificationService.createNotifications(managerNotification);

            // Xử lý phạt trả muộn nếu có
            if (request.getFineAmount() > 0) {
                Fine fine = Fine.builder()
                        .amount(request.getFineAmount())
                        .status(Fine.Status.UNPAID)
                        .issuedDate(LocalDateTime.now())
                        .reason(request.getFineReason())
                        .transactionLoan(loanTransaction)
                        .user(loanTransaction.getUser())
                        .build();
                fineRepository.save(fine);
            }
        }

        LoanTransaction updatedTransaction = loanTransactionRepository.save(loanTransaction);

        // Gửi thông báo xác nhận trả sách cho người dùng
        NotificationCreateRequest userNotification = NotificationCreateRequest.builder()
                .userIds(List.of(loanTransaction.getUser().getUserId()))
                .title("Xác nhận trả sách")
                .content(String.format("Sách '%s' đã được trả và xác nhận thành công.", document.getDocumentName()))
                .build();
        notificationService.createNotifications(userNotification);

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
        Set<DocumentType> documentTypes = document.getDocumentTypes();

        // Lấy tất cả LoanPolicy cho các DocumentType này và chọn cái có maxLoanDays thấp nhất
        LoanPolicy loanPolicy = loanPolicyRepository.findByDocumentTypeIn(documentTypes)
                .stream()
                .min(Comparator.comparingInt(LoanPolicy::getMaxLoanDays))
                .orElseThrow(() -> new AppException(ErrorCode.POLICY_NOT_FOUND, "Không tìm thấy chính sách phù hợp"));


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
                .userIds(List.of(loanTransaction.getUser().getUserId()))
                .title("Gia hạn sách thành công")
                .content(String.format("Yêu cầu gia hạn sách '%s' đã thành công. Ngày trả mới là: %s", document.getDocumentName(), newDueDate))
                .build();
        notificationService.createNotifications(notificationCreateRequest);

        return loanTransactionMapper.toLoanTransactionResponse(updatedTransaction);
    }
    @Override
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public LoanTransactionResponse cancelLoanTransactionByUser(Long transactionId) {
        LoanTransaction loanTransaction = loanTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        User currentUser = securityContextService.getCurrentUser();
        if (!loanTransaction.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Bạn không có quyền hủy giao dịch này.");
        }

        // Cho phép hủy ở trạng thái PENDING hoặc APPROVED trong vòng 24 giờ sau khi được phê duyệt
        if (loanTransaction.getStatus() == LoanTransaction.Status.PENDING ||
                (loanTransaction.getStatus() == LoanTransaction.Status.APPROVED &&
                        loanTransaction.getUpdatedAt().isAfter(LocalDateTime.now().minusHours(24)))) {

            boolean wasApproved = loanTransaction.getStatus() == LoanTransaction.Status.APPROVED;
            loanTransaction.setStatus(LoanTransaction.Status.CANCELLED_BY_USER);

            Document document = loanTransaction.getDocument();

            if (wasApproved) {
                // Tăng lại số lượng sách có sẵn
                document.setAvailableCount(document.getAvailableCount() + 1);

                // Trả sách về vị trí cũ
                boolean returnedToOriginalLocation = false;
                String title = "";
                String message = "";
                User managerUser = securityContextService.getCurrentUser(); // Lấy thông tin người quản lý hiện tại

                if (loanTransaction.getOriginalRackId() != null) {
                    // Trả sách về kệ gốc
                    Rack originalRack = rackRepository.findById(loanTransaction.getOriginalRackId())
                            .orElseThrow(() -> new AppException(ErrorCode.RACK_NOT_FOUND, "Kệ gốc không tồn tại."));

                    double currentRackSize = document.getLocations().stream()
                            .filter(loc -> loc.getRackId() != null && loc.getRackId().equals(originalRack.getRackId()))
                            .mapToDouble(loc -> loc.getSize().getSizeValue() * loc.getQuantity())
                            .sum();
                    double documentSize = document.getSize().getSizeValue();

                    if (currentRackSize + documentSize <= originalRack.getCapacity()) {
                        // Có đủ chỗ, trả sách về kệ
                        DocumentLocation rackLocation = document.getLocations().stream()
                                .filter(loc -> loc.getRackId() != null && loc.getRackId().equals(originalRack.getRackId()))
                                .findFirst()
                                .orElseGet(() -> {
                                    DocumentLocation newLocation = DocumentLocation.builder()
                                            .rackId(originalRack.getRackId())
                                            .quantity(0)
                                            .size(document.getSize())
                                            .build();
                                    document.getLocations().add(newLocation);
                                    return newLocation;
                                });
                        rackLocation.setQuantity(rackLocation.getQuantity() + 1);
                        rackLocation.updateTotalSize(); // Cập nhật tổng kích thước

                        returnedToOriginalLocation = true;

                        // Thiết lập thông báo
                        title = "Sách được trả về kệ";
                        message = String.format("Sách '%s' đã được trả về kệ '%s'.", document.getDocumentName(), originalRack.getRackNumber());
                    }
                }

                if (!returnedToOriginalLocation) {
                    // Trả sách vào kho
                    Long warehouseId = loanTransaction.getOriginalWarehouseId();
                    if (warehouseId == null) {
                        // Nếu không có kho gốc, chọn kho mặc định
                        Warehouse defaultWarehouse = warehouseRepository.findAll().stream().findFirst()
                                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND, "Không tìm thấy kho lưu trữ."));
                        warehouseId = defaultWarehouse.getWarehouseId();
                    }

                    Warehouse warehouse = warehouseRepository.findById(warehouseId)
                            .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND, "Kho không tồn tại."));

                    DocumentLocation warehouseLocation = document.getLocations().stream()
                            .filter(loc -> loc.getWarehouseId() != null && loc.getWarehouseId().equals(warehouse.getWarehouseId()))
                            .findFirst()
                            .orElseGet(() -> {
                                DocumentLocation newLocation = DocumentLocation.builder()
                                        .warehouseId(warehouse.getWarehouseId())
                                        .quantity(0)
                                        .size(document.getSize())
                                        .build();
                                document.getLocations().add(newLocation);
                                return newLocation;
                            });
                    warehouseLocation.setQuantity(warehouseLocation.getQuantity() + 1);
                    warehouseLocation.updateTotalSize(); // Cập nhật tổng kích thước

                    // Gửi thông báo cho quản lý
                    title = "Sách trả về kho";
                    message = String.format("Sách '%s' đã được trả về kho '%s' do kệ gốc đã đầy.", document.getDocumentName(), warehouse.getWarehouseName());
                }

                // Lưu lại tài liệu
                documentRepository.save(document);

                // Gửi thông báo cho quản lý
                List<String> managerUserIds = getManagerUserIds();
                NotificationCreateRequest managerNotification = NotificationCreateRequest.builder()
                        .userIds(managerUserIds)
                        .title(title)
                        .content(message)
                        .build();
                notificationService.createNotifications(managerNotification);
            }

            // Lưu lại giao dịch
            LoanTransaction updatedTransaction = loanTransactionRepository.save(loanTransaction);

            // Gửi thông báo hủy yêu cầu cho người dùng
            NotificationCreateRequest userNotification = NotificationCreateRequest.builder()
                    .userIds(List.of(loanTransaction.getUser().getUserId()))
                    .title("Yêu cầu mượn sách bị hủy")
                    .content(String.format("Yêu cầu mượn sách '%s' của bạn đã bị hủy.", document.getDocumentName()))
                    .build();
            notificationService.createNotifications(userNotification);

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

                // Bắt đầu xử lý trả sách về kệ hoặc kho
                boolean returnedToOriginalLocation = false;
                String title = "";
                String message = "";

                if (transaction.getOriginalRackId() != null) {
                    // Trả sách về kệ gốc
                    Rack originalRack = rackRepository.findById(transaction.getOriginalRackId())
                            .orElseThrow(() -> new AppException(ErrorCode.RACK_NOT_FOUND, "Kệ gốc không tồn tại."));

                    double currentRackSize = document.getLocations().stream()
                            .filter(loc -> loc.getRackId() != null && loc.getRackId().equals(originalRack.getRackId()))
                            .mapToDouble(loc -> loc.getSize().getSizeValue() * loc.getQuantity())
                            .sum();
                    double documentSize = document.getSize().getSizeValue();

                    if (currentRackSize + documentSize <= originalRack.getCapacity()) {
                        // Có đủ chỗ, trả sách về kệ
                        DocumentLocation rackLocation = document.getLocations().stream()
                                .filter(loc -> loc.getRackId() != null && loc.getRackId().equals(originalRack.getRackId()))
                                .findFirst()
                                .orElseGet(() -> {
                                    DocumentLocation newLocation = DocumentLocation.builder()
                                            .rackId(originalRack.getRackId())
                                            .quantity(0)
                                            .size(document.getSize())
                                            .build();
                                    document.getLocations().add(newLocation);
                                    return newLocation;
                                });
                        rackLocation.setQuantity(rackLocation.getQuantity() + 1);
                        rackLocation.updateTotalSize();
                        returnedToOriginalLocation = true;

                        // Thiết lập thông báo
                        title = "Sách được trả về kệ";
                        message = String.format("Sách '%s' đã được trả về kệ '%s' sau khi hủy yêu cầu mượn sách.", document.getDocumentName(), originalRack.getRackNumber());
                    }
                }

                if (!returnedToOriginalLocation) {
                    // Trả sách vào kho
                    Long warehouseId = transaction.getOriginalWarehouseId();
                    if (warehouseId == null) {
                        // Nếu không có kho gốc, chọn kho mặc định
                        Warehouse defaultWarehouse = warehouseRepository.findAll().stream().findFirst()
                                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND, "Không tìm thấy kho lưu trữ."));
                        warehouseId = defaultWarehouse.getWarehouseId();
                    }

                    Warehouse warehouse = warehouseRepository.findById(warehouseId)
                            .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND, "Kho không tồn tại."));

                    DocumentLocation warehouseLocation = document.getLocations().stream()
                            .filter(loc -> loc.getWarehouseId() != null && loc.getWarehouseId().equals(warehouse.getWarehouseId()))
                            .findFirst()
                            .orElseGet(() -> {
                                DocumentLocation newLocation = DocumentLocation.builder()
                                        .warehouseId(warehouse.getWarehouseId())
                                        .quantity(0)
                                        .size(document.getSize())
                                        .build();
                                document.getLocations().add(newLocation);
                                return newLocation;
                            });
                    warehouseLocation.setQuantity(warehouseLocation.getQuantity() + 1);
                    warehouseLocation.updateTotalSize();

                    // Thiết lập thông báo
                    title = "Sách trả về kho";
                    message = String.format("Sách '%s' đã được trả về kho '%s' do kệ gốc đã đầy.", document.getDocumentName(), warehouse.getWarehouseName());
                }

                // Lưu lại tài liệu và giao dịch
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

                // Gửi thông báo cho người dùng về việc hủy yêu cầu
                NotificationCreateRequest userNotification = NotificationCreateRequest.builder()
                        .userIds(List.of(transaction.getUser().getUserId()))
                        .title("Yêu cầu mượn sách đã bị hủy tự động")
                        .content(String.format("Yêu cầu mượn sách '%s' của bạn đã bị hủy do không nhận sách trong thời hạn quy định. Hạn cuối để nhận sách là 00:00 ngày %s. Bạn đã bị phạt với số tiền là 10 đơn vị tiền tệ.",
                                document.getDocumentName(), cutoffTime.toLocalDate()))
                        .build();
                notificationService.createNotifications(userNotification);

                // Gửi thông báo cho quản lý về vị trí sách
                List<String> managerUserIds = getManagerUserIds();
                NotificationCreateRequest managerNotification = NotificationCreateRequest.builder()
                        .userIds(managerUserIds)
                        .title(title)
                        .content(message)
                        .build();
                notificationService.createNotifications(managerNotification);
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

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public LoanTransactionResponse handleQrcodeScan(String barcodeData) {
        // Giải mã mã vạch để lấy LoanTransactionId
        Long transactionId = parseQrcodeToken(barcodeData);

        // Lấy giao dịch mượn sách dựa trên LoanTransactionId
        LoanTransaction loanTransaction = loanTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        // Kiểm tra quyền truy cập
        User currentUser = securityContextService.getCurrentUser();
        if (currentUser == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        // Cập nhật trạng thái giao dịch dựa trên trạng thái hiện tại
        if (loanTransaction.getStatus() == LoanTransaction.Status.APPROVED) {
            // Người dùng đang nhận sách
            return receiveDocument(loanTransaction.getTransactionId(),false);
        } else if (loanTransaction.getStatus() == LoanTransaction.Status.RECEIVED) {
            return returnDocument(loanTransaction.getTransactionId(),false);
        } else {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Không thể xử lý quét mã vạch cho trạng thái giao dịch hiện tại.");
        }
    }
    private Long parseQrcodeToken(String barcodeToken) {
        try {
            // Phân tích JWT từ chuỗi barcodeToken
            SignedJWT signedJWT = SignedJWT.parse(barcodeToken);

            // Tạo đối tượng xác thực chữ ký số với khóa bí mật
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

            // Xác thực chữ ký số
            if (!signedJWT.verify(verifier)) {
                throw new AppException(ErrorCode.UNAUTHORIZED, "QR code không hợp lệ.");
            }

            // Kiểm tra thời gian hết hạn
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expirationTime.before(new Date())) {
                throw new AppException(ErrorCode.UNAUTHORIZED, "QR code đã hết hạn.");
            }

            // Trích xuất transactionId từ claims
            Long transactionId = signedJWT.getJWTClaimsSet().getLongClaim("transactionId");
            return transactionId;
        } catch (JOSEException | java.text.ParseException e) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Dữ liệu QR code không hợp lệ.");
        }
    }
    public String generateQrcodeToken(Long transactionId) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .claim("transactionId", transactionId)
                .issuer("")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(15, ChronoUnit.MINUTES).toEpochMilli())) // Thời gian hết hạn
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create barcode token", e);
            throw new AppException(ErrorCode.SERVER_ERROR);
        }
    }
    public byte[] generateQRCodeImage(String barcodeToken) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 300;
        int height = 300;
        BitMatrix bitMatrix = qrCodeWriter.encode(barcodeToken, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }


    @Override
    public byte[] getQrcodeImage(Long transactionId) {
        // Kiểm tra quyền truy cập
        LoanTransaction loanTransaction = loanTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        User currentUser = securityContextService.getCurrentUser();
        if (!loanTransaction.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Bạn không có quyền lấy QR code cho giao dịch này.");
        }

        // Tạo barcode token
        String barcodeToken = generateQrcodeToken(transactionId);

        try {
            // Tạo hình ảnh QR code
            return generateQRCodeImage(barcodeToken);
        } catch (WriterException | IOException e) {
            throw new AppException(ErrorCode.SERVER_ERROR, "Lỗi khi tạo QR code");
        }
    }



    // Phương thức lấy danh sách ID của các người dùng có vai trò là MANAGER
    private List<String> getManagerUserIds() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> PredefinedRole.MANAGER_ROLE.equals(role.getName())))
                .map(User::getUserId)
                .collect(Collectors.toList());
    }

    /**
     * Kiểm tra quyền và trạng thái của giao dịch nếu người dùng là user.
     */
    private void validateUserPermissions(LoanTransaction loanTransaction,LoanTransaction.Status requiredStatus){
        User currentUser = securityContextService.getCurrentUser();

        // Kiểm tra xem người dùng có phải là người đã thực hiện giao dịch không
        if (!loanTransaction.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Bạn không có quyền thực hiện giao dịch này.");
        }

        // Kiểm tra trạng thái giao dịch
        if (!loanTransaction.getStatus().equals(requiredStatus)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, String.format("Giao dịch không ở trạng thái %s.", requiredStatus));
        }
    }


}
