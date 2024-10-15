package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.loanTransactionRequest.LoanTransactionRequest;
import com.spkt.librasys.dto.response.LoanTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoanTransactionService {

    // Tạo yêu cầu mượn sách
    LoanTransactionResponse createLoanTransaction(LoanTransactionRequest request);

    // Phê duyệt yêu cầu mượn sách (Manager/Admin)
    LoanTransactionResponse approveTransaction(Long transactionId, boolean isApproved);

    // Nhận sách sau khi yêu cầu được phê duyệt (Manager xác nhận người dùng đã nhận sách)
    LoanTransactionResponse receiveDocument(Long transactionId);

    // Trả sách
    LoanTransactionResponse returnDocument(Long transactionId);

    // Lấy thông tin giao dịch dựa trên ID
    LoanTransactionResponse getLoanTransactionById(Long id);
    // Người dùng hủy yêu cầu mượn sách
    LoanTransactionResponse cancelLoanTransactionByUser(Long transactionId);

    // Tự động hủy các giao dịch đã được phê duyệt nhưng chưa nhận sách trong 24 giờ
    void cancelExpiredApprovedTransactions();
    // Lấy tất cả giao dịch với tìm kiếm nâng cao và phân trang
    Page<LoanTransactionResponse> getAllLoanTransactions(String status, String username, String documentName, Pageable pageable);

}
