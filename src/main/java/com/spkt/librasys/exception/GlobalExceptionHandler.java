package com.spkt.librasys.exception;

import com.spkt.librasys.dto.response.ApiResponse;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min";

    /**
     * Xử lý ngoại lệ chung (RuntimeException)
     */
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
        log.error("Exception: ", exception);
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErrorCode.UNKNOWN_ERROR.getCode());
        apiResponse.setMessage(ErrorCode.UNKNOWN_ERROR.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    /**
     * Xử lý ngoại lệ khi JSON không đúng format (HttpMessageNotReadableException)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErrorCode.INVALID_REQUEST.getCode());
        apiResponse.setMessage("Chuyển đổi JSON không đúng format.");
        return ResponseEntity.badRequest().body(apiResponse);
    }

    /**
     * Xử lý ngoại lệ ứng dụng (AppException)
     */
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();

        // Sử dụng thông điệp tùy chỉnh nếu có
        String message = (exception.getMessageCustom() != null && !exception.getMessageCustom().isEmpty())
                ? exception.getMessageCustom()
                : errorCode.getMessage();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(message);

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    /**
     * Xử lý ngoại lệ khi không đủ quyền truy cập (AccessDeniedException)
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    /**
     * Xử lý lỗi xác thực trong request (MethodArgumentNotValidException)
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handlingValidation(MethodArgumentNotValidException exception) {
        // Khởi tạo mã lỗi mặc định
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;

        // Tạo Map để lưu trữ các lỗi xác thực
        Map<String, String> errors = new HashMap<>();

        // Duyệt qua tất cả các lỗi xác thực
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            if (error instanceof FieldError) {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            }
        });

        // Tạo ApiResponse với mã lỗi và chi tiết lỗi
        ApiResponse<Map<String, String>> apiResponse = ApiResponse.<Map<String, String>>builder()
                .code(errorCode.getCode())
                .message("Validation errors occurred")
                .result(errors)
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    /**
     * Hàm hỗ trợ để thay thế thuộc tính trong thông báo lỗi
     */
    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));
        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }

    /**
     * Xử lý ngoại lệ cho lỗi không tìm thấy dữ liệu (EntityNotFoundException)
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErrorCode.RESOURCE_NOT_FOUND.getCode());
        apiResponse.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    /**
     * Xử lý ngoại lệ liên quan đến dữ liệu (IllegalArgumentException)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErrorCode.INVALID_REQUEST.getCode());
        apiResponse.setMessage("Dữ liệu không hợp lệ: " + ex.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ApiResponse<String>> handleRequestNotPermitted(RequestNotPermitted e) {
        log.error("Rate limit exceeded: {}", e.getMessage());
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(429)
                .message("Yêu cầu đã vượt quá giới hạn cho phép. Vui lòng thử lại sau.")
                .build();
        return ResponseEntity.status(429).body(response);
    }

}
