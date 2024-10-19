package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.loanTransaction.*;
import com.spkt.librasys.dto.response.LoanTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * Giao diện LoanTransactionService định nghĩa các hành vi liên quan đến giao dịch mượn sách.
 */
public interface LoanTransactionService {
    /**
     * Tạo yêu cầu mượn sách từ người dùng.
     *
     * @param request thông tin yêu cầu mượn sách, bao gồm ID tài liệu và ngày dự kiến trả.
     * @return LoanTransactionResponse chứa thông tin giao dịch mượn sách đã tạo.
     */
    LoanTransactionResponse createLoanTransaction(LoanTransactionRequest request);

    /**
     * Phê duyệt yêu cầu mượn sách.
     *
     * @param transactionId ID của giao dịch mượn sách cần phê duyệt.
     * @return LoanTransactionResponse chứa thông tin giao dịch đã được phê duyệt.
     */
    LoanTransactionResponse approveTransaction(Long transactionId);

    /**
     * Từ chối yêu cầu mượn sách.
     *
     * @param transactionId ID của giao dịch mượn sách cần từ chối.
     * @return LoanTransactionResponse chứa thông tin giao dịch đã bị từ chối.
     */
    LoanTransactionResponse rejectTransaction(Long transactionId);

    /**
     * Xác nhận người dùng đã nhận sách sau khi yêu cầu mượn được phê duyệt.
     *
     * @param transactionId ID của giao dịch mượn sách cần xác nhận.
     * @return LoanTransactionResponse chứa thông tin giao dịch đã cập nhật.
     */
    LoanTransactionResponse receiveDocument(Long transactionId);
    LoanTransactionResponse confirmReturnDocument(LoanTransactionReturnRequest request);
    /**
     * Người dùng trả sách sau khi đã mượn.
     *
     * @param transactionId ID của giao dịch mượn sách cần trả.
     * @return LoanTransactionResponse chứa thông tin giao dịch đã cập nhật.
     */
    LoanTransactionResponse returnDocument(Long transactionId);

    /**
     * Người dùng hủy yêu cầu mượn sách trước khi được phê duyệt.
     *
     * @param transactionId  ID của giao dịch mượn sách cần huy
     * @return LoanTransactionResponse chứa thông tin giao dịch đã được hủy.
     */
    LoanTransactionResponse cancelLoanTransactionByUser(Long transactionId);

    /**
     * Lấy thông tin chi tiết của giao dịch mượn sách dựa trên ID.
     *
     * @param id ID của giao dịch mượn sách.
     * @return LoanTransactionResponse chứa thông tin giao dịch.
     */
    LoanTransactionResponse getLoanTransactionById(Long id);

    /**
     * Tự động hủy các yêu cầu đã được phê duyệt nhưng chưa nhận sách trong vòng 24 giờ.
     * Phương thức này chạy định kỳ theo lịch.
     */
    void cancelExpiredApprovedTransactions();

    /**
     * Lấy danh sách tất cả các giao dịch mượn sách với tìm kiếm nâng cao và phân trang.
     *
     * @param request Chứa tiêu chí tìm kiếm, bao gồm trạng thái, tên người dùng và tên tài liệu.
     * @param pageable Đối tượng phân trang.
     * @return Page<LoanTransactionResponse> chứa danh sách giao dịch mượn sách.
     */
    Page<LoanTransactionResponse> getAllLoanTransactions(LoanTransactionSearchRequest request, Pageable pageable);
}