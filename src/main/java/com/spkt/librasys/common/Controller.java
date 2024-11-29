package com.spkt.librasys.common;

import com.spkt.librasys.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vnpay")
public class Controller {
    @Autowired
    private VNPayService vnPayService;


    @GetMapping("")
    public String home(){
        return "index";
    }

    @PostMapping("/submitOrder/{loanTransactionId}")
    public ApiResponse<String> submitOrder(@PathVariable Long loanTransactionId){
        //String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(loanTransactionId);
        return ApiResponse.<String>builder()
                .message("Redirect")
                .result(vnpayUrl)
                .build();

    }

    @GetMapping("/return-payment")
    public ApiResponse<String> GetMapping(HttpServletRequest request){
        int paymentStatus =vnPayService.orderReturn(request);
        String message = paymentStatus == 1 ? "ordersuccess" : "orderfail";
        return ApiResponse.<String>builder()
                .message(message)
                .build();
    }
}