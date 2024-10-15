package com.spkt.librasys.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    // Nhóm lỗi người dùng
    USER_NOT_FOUND(1001, "User not found", HttpStatus.NOT_FOUND),
    DUPLICATE_USER(1002, "User already exists", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(1003, "Invalid credentials", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(1004, "Access is denied", HttpStatus.FORBIDDEN),
    UNAUTHORIZED(1005, "Unauthorized access", HttpStatus.UNAUTHORIZED),
    INSUFFICIENT_PERMISSIONS(1006, "User does not have sufficient permissions", HttpStatus.FORBIDDEN),
    USER_ACCOUNT_LOCKED(1007, "User account is locked", HttpStatus.FORBIDDEN),
    USER_ACCOUNT_DISABLED(1008, "User account is disabled", HttpStatus.FORBIDDEN),
    PASSWORD_EXPIRED(1009, "Password has expired", HttpStatus.UNAUTHORIZED),

    // Nhóm lỗi xác thực
    TOKEN_EXPIRED(2001, "Token has expired", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(2002, "Token is invalid", HttpStatus.UNAUTHORIZED),
    TOKEN_MISSING(2003, "Token is missing", HttpStatus.UNAUTHORIZED),
    RATE_LIMIT_EXCEEDED(2004, "Rate limit exceeded", HttpStatus.TOO_MANY_REQUESTS),
    SESSION_EXPIRED(2005, "Session has expired", HttpStatus.UNAUTHORIZED),

    // Nhóm lỗi yêu cầu và dữ liệu đầu vào
    INVALID_REQUEST(3001, "Invalid request data", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_FIELDS(3002, "Missing required fields", HttpStatus.BAD_REQUEST),
    DATA_FORMAT_ERROR(3003, "Invalid data format", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_MEDIA_TYPE(3004, "Unsupported media type", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    RESOURCE_CONFLICT(3005, "Resource conflict", HttpStatus.CONFLICT),

    // Nhóm lỗi tài liệu và tệp
    DOCUMENT_NOT_FOUND(4001, "Document not found", HttpStatus.NOT_FOUND),
    DUPLICATE_DOCUMENT(4002, "Document already exists", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_FAILED(4003, "File upload failed", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_NOT_FOUND(4004, "File not found", HttpStatus.NOT_FOUND),
    FILE_TOO_LARGE(4005, "File is too large", HttpStatus.PAYLOAD_TOO_LARGE),
    FILE_FORMAT_UNSUPPORTED(4006, "File format is not supported", HttpStatus.UNSUPPORTED_MEDIA_TYPE),

    // Nhóm lỗi cơ sở dữ liệu
    DATABASE_ERROR(5001, "Database error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    DATA_INTEGRITY_VIOLATION(5002, "Data integrity violation", HttpStatus.CONFLICT),
    TRANSACTION_FAILED(5003, "Database transaction failed", HttpStatus.INTERNAL_SERVER_ERROR),
    TRANSACTION_NOT_FOUND(5004, "Loan transaction not found", HttpStatus.NOT_FOUND), // Thêm dòng này

    // Nhóm lỗi hệ thống
    SERVER_ERROR(6001, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE(6002, "Service temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    GATEWAY_TIMEOUT(6003, "Gateway timeout", HttpStatus.GATEWAY_TIMEOUT),
    CONFIGURATION_ERROR(6004, "System configuration error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNKNOWN_ERROR(6005, "Unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
