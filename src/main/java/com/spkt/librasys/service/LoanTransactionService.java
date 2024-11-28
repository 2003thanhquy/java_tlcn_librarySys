package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.loanTransaction.*;
import com.spkt.librasys.dto.response.LoanTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * @param isUser       thông tin nếu người dùng đã nhận sách hay chưa.
     * @return LoanTransactionResponse chứa thông tin giao dịch đã cập nhật.
     */
    LoanTransactionResponse receiveDocument(Long transactionId, boolean isUser);

    /**
     * Xác nhận việc trả sách sau khi giao dịch mượn sách đã hoàn tất.
     *
     * @param request thông tin yêu cầu trả sách.
     * @return LoanTransactionResponse chứa thông tin giao dịch đã được cập nhật.
     */
    LoanTransactionResponse confirmReturnDocument(LoanTransactionReturnRequest request);

    /**
     * Gia hạn giao dịch mượn sách.
     *
     * @param transactionId ID của giao dịch mượn sách cần gia hạn.
     * @return LoanTransactionResponse chứa thông tin giao dịch đã được gia hạn.
     */
    LoanTransactionResponse renewLoanTransaction(Long transactionId);

    /**
     * Người dùng trả sách sau khi đã mượn.
     *
     * @param transactionId ID của giao dịch mượn sách cần trả.
     * @param isUser       thông tin nếu người dùng trả sách hay không.
     * @return LoanTransactionResponse chứa thông tin giao dịch đã cập nhật.
     */
    LoanTransactionResponse returnDocument(Long transactionId, boolean isUser);

    /**
     * Người dùng hủy yêu cầu mượn sách trước khi được phê duyệt.
     *
     * @param transactionId  ID của giao dịch mượn sách cần hủy.
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

    /**
     * Kiểm tra xem người dùng có đang mượn cuốn sách cụ thể hay không.
     *
     * @param documentId ID của cuốn sách cần kiểm tra.
     * @return true nếu người dùng đang mượn cuốn sách, false nếu không.
     */
    boolean isUserBorrowingDocument(Long documentId);

    /**
     * Lấy danh sách các sách mà người dùng đang mượn với phân trang.
     *
     * @param pageable thông tin phân trang.
     * @return PageDTO chứa danh sách LoanTransactionResponse của các sách đang được mượn.
     */
    Page<LoanTransactionResponse> getUserBorrowedBooks(Pageable pageable);

    /**
     * Xử lý quét mã QR để xác nhận giao dịch mượn sách.
     *
     * @param barcodeData Dữ liệu mã vạch được quét.
     * @return LoanTransactionResponse chứa thông tin giao dịch đã xử lý.
     */
    LoanTransactionResponse handleQrcodeScan(String barcodeData);

    /**
     * Lấy ảnh mã QR của một giao dịch mượn sách.
     *
     * @param transactionId ID của giao dịch mượn sách.
     * @return Mảng byte chứa dữ liệu ảnh mã QR.
     */
    byte[] getQrcodeImage(Long transactionId);
}
