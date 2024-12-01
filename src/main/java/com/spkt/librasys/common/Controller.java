package com.spkt.librasys.common;

import com.spkt.librasys.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller để xử lý các yêu cầu liên quan đến thanh toán với VNPay.
 * Bao gồm các hành động như gửi đơn hàng và xử lý kết quả trả về từ VNPay.
 */
@RestController
@RequestMapping("/api/v1/vnpay")
public class Controller {

    @Autowired
    private VNPayService vnPayService;

    /**
     * Gửi đơn hàng đến VNPay để tạo đơn thanh toán.
     * Phương thức này sẽ tạo ra URL để chuyển hướng người dùng đến trang thanh toán của VNPay.
     *
     * @param loanTransactionId ID giao dịch khoản vay.
     * @return Phản hồi ApiResponse chứa URL thanh toán của VNPay.
     */
    @PostMapping("/submitOrder/{loanTransactionId}")
    public ApiResponse<Map<String,String>> submitOrder(@PathVariable Long loanTransactionId) {
        // Tạo đơn hàng và lấy URL thanh toán từ VNPay
        String vnpayUrl = vnPayService.createOrder(loanTransactionId);
        Map<String,String> map = new HashMap<>();
        map.put("vnpayUrl",vnpayUrl);
        // Trả về URL thanh toán với thông điệp "Redirect"
        return ApiResponse.<Map<String,String>>builder()
                .message("Redirect")
                .result(map)
                .build();
    }

    /**
     * Xử lý kết quả trả về từ VNPay sau khi người dùng hoàn thành thanh toán.
     * Phương thức này nhận kết quả thanh toán và trả về thông báo thành công hoặc thất bại.
     *
     * @param request Đối tượng HttpServletRequest chứa các tham số trả về từ VNPay.
     * @return Phản hồi ApiResponse với thông báo kết quả thanh toán (thành công hoặc thất bại).
     */
    @GetMapping("/return-payment")
    public ApiResponse<String> returnPayment(HttpServletRequest request) {
        // Kiểm tra trạng thái thanh toán của đơn hàng
        int paymentStatus = vnPayService.orderReturn(request);

        // Xác định thông điệp dựa trên trạng thái thanh toán
        String message = paymentStatus == 1 ? "ordersuccess" : "orderfail";

        // Trả về thông điệp kết quả thanh toán
        return ApiResponse.<String>builder()
                .message(message)
                .build();
    }
}
