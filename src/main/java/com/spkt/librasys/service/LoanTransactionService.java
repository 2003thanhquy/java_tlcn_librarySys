package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.loanTransaction.LoanTransactionRequest;
import com.spkt.librasys.dto.response.LoanTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoanTransactionService {

    /**
     * Tạo yêu cầu mượn sách từ người dùng.
     *
     * @param request thông tin yêu cầu mượn sách.
     * @return LoanTransactionResponse chứa thông tin giao dịch mượn sách đã tạo.
     */
    LoanTransactionResponse createLoanTransaction(LoanTransactionRequest request);

    /**
     * Phê duyệt hoặc từ chối yêu cầu mượn sách.
     * Chỉ dành cho vai trò MANAGER hoặc ADMIN.
     *
     * @param transactionId ID của giao dịch mượn sách.
     * @param isApproved True để phê duyệt, False để từ chối.
     * @return LoanTransactionResponse chứa thông tin giao dịch đã cập nhật.
     */
    LoanTransactionResponse approveTransaction(Long transactionId, boolean isApproved);

    /**
     * Xác nhận người dùng đã nhận sách sau khi yêu cầu mượn được phê duyệt.
     *
     * @param transactionId ID của giao dịch mượn sách.
     * @return LoanTransactionResponse chứa thông tin giao dịch đã cập nhật.
     */
    LoanTransactionResponse receiveDocument(Long transactionId);

    /**
     * Người dùng trả sách sau khi đã mượn.
     *
     * @param transactionId ID của giao dịch mượn sách.
     * @return LoanTransactionResponse chứa thông tin giao dịch đã cập nhật.
     */
    LoanTransactionResponse returnDocument(Long transactionId);

    /**
     * Lấy thông tin giao dịch mượn sách dựa trên ID.
     * Chỉ dành cho vai trò MANAGER hoặc ADMIN.
     *
     * @param id ID của giao dịch mượn sách.
     * @return LoanTransactionResponse chứa thông tin giao dịch.
     */
    LoanTransactionResponse getLoanTransactionById(Long id);

    /**
     * Người dùng hủy yêu cầu mượn sách trước khi được phê duyệt.
     *
     * @param transactionId ID của giao dịch mượn sách.
     * @return LoanTransactionResponse chứa thông tin giao dịch đã cập nhật.
     */
    LoanTransactionResponse cancelLoanTransactionByUser(Long transactionId);

    /**
     * Tự động hủy các giao dịch đã được phê duyệt nhưng chưa nhận sách trong vòng 24 giờ.
     * Phương thức này chạy định kỳ theo lịch.
     */
    void cancelExpiredApprovedTransactions();

    /**
     * Lấy danh sách tất cả các giao dịch mượn sách với tìm kiếm nâng cao và phân trang.
     * Chỉ dành cho vai trò MANAGER hoặc ADMIN.
     *
     * @param status      Trạng thái giao dịch (APPROVED, PENDING, REJECTED, etc.).
     * @param username    Tên người dùng tham gia giao dịch mượn.
     * @param documentName Tên tài liệu cần tìm kiếm.
     * @param pageable    Đối tượng phân trang.
     * @return Page<LoanTransactionResponse> chứa danh sách giao dịch mượn sách.
     */
    Page<LoanTransactionResponse> getAllLoanTransactions(String status, String username, String documentName, Pageable pageable);
}
