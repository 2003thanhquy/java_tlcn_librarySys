package com.spkt.librasys.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    // User-related errors
    USER_NOT_FOUND(1001, "User not found", HttpStatus.NOT_FOUND),
    DUPLICATE_USER(1002, "User already exists", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(1003, "Invalid credentials", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(1004, "Access is denied", HttpStatus.FORBIDDEN),
    UNAUTHORIZED(1005, "Unauthorized access", HttpStatus.UNAUTHORIZED),
    INSUFFICIENT_PERMISSIONS(1006, "User does not have sufficient permissions", HttpStatus.FORBIDDEN),
    USER_ACCOUNT_LOCKED(1007, "User account is locked", HttpStatus.FORBIDDEN),
    USER_ACCOUNT_DISABLED(1008, "User account is disabled", HttpStatus.FORBIDDEN),
    PASSWORD_EXPIRED(1009, "Password has expired", HttpStatus.UNAUTHORIZED),
    USER_DEACTIVATED(1010, "User account has been deactivated", HttpStatus.FORBIDDEN),
    USER_LOCKED(1011, "User account is locked", HttpStatus.FORBIDDEN),
    USER_ALREADY_DELETED(1012, "User account has already been deleted", HttpStatus.GONE),
    USER_PENDING(1013, "User account is pending verification", HttpStatus.PROCESSING),

    // Authentication errors
    TOKEN_EXPIRED(2001, "Token has expired", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(2002, "Token is invalid", HttpStatus.UNAUTHORIZED),
    TOKEN_MISSING(2003, "Token is missing", HttpStatus.UNAUTHORIZED),
    RATE_LIMIT_EXCEEDED(2004, "Rate limit exceeded", HttpStatus.TOO_MANY_REQUESTS),
    SESSION_EXPIRED(2005, "Session has expired", HttpStatus.UNAUTHORIZED),

    // Request and Input Data Errors
    INVALID_REQUEST(3001, "Invalid request data", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_FIELDS(3002, "Missing required fields", HttpStatus.BAD_REQUEST),
    DATA_FORMAT_ERROR(3003, "Invalid data format", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_MEDIA_TYPE(3004, "Unsupported media type", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    RESOURCE_CONFLICT(3005, "Resource conflict", HttpStatus.CONFLICT),
    RESOURCE_NOT_FOUND(3006, "Resource not found", HttpStatus.NOT_FOUND),

    // Document and File Errors
    INVALID_LOCATION_TYPE(4000, "Invalid location type", HttpStatus.BAD_REQUEST),
    DOCUMENT_NOT_FOUND(4001, "Document not found", HttpStatus.NOT_FOUND),
    DUPLICATE_DOCUMENT(4002, "Document already exists", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_FAILED(4003, "File upload failed", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_NOT_FOUND(4004, "File not found", HttpStatus.NOT_FOUND),
    FILE_TOO_LARGE(4005, "File is too large", HttpStatus.PAYLOAD_TOO_LARGE),
    FILE_FORMAT_UNSUPPORTED(4006, "File format is not supported", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    DOCUMENT_TYPE_NOT_FOUND(4007, "Document type not found", HttpStatus.NOT_FOUND),
    DOCUMENT_ALREADY_FAVORITE(4008, "Document already marked as favorite", HttpStatus.CONFLICT),
    // Warehouse Errors
    WAREHOUSE_NOT_FOUND(4009, "Warehouse not found", HttpStatus.NOT_FOUND),
    INVALID_QUANTITY(4010, "Quantity cannot be negative", HttpStatus.BAD_REQUEST),
    RACK_NOT_FOUND(4011, "Rack not found", HttpStatus.NOT_FOUND),
    LOCATION_NOT_FOUND(4012, "Document location in rack not found", HttpStatus.NOT_FOUND),
    RACK_CAPACITY_EXCEEDED(4013, "Rack does not have enough capacity", HttpStatus.BAD_REQUEST),
    // Database Errors
    DATABASE_ERROR(5001, "Database error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    DATA_INTEGRITY_VIOLATION(5002, "Data integrity violation", HttpStatus.CONFLICT),
    TRANSACTION_FAILED(5003, "Database transaction failed", HttpStatus.INTERNAL_SERVER_ERROR),
    TRANSACTION_NOT_FOUND(5004, "Loan transaction not found", HttpStatus.NOT_FOUND),
    FINE_NOT_FOUND(5005, "Fine not found", HttpStatus.NOT_FOUND),
    POLICY_NOT_FOUND(5006, "Policy loan not found", HttpStatus.NOT_FOUND),

    // System Errors
    SERVER_ERROR(6001, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE(6002, "Service temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    GATEWAY_TIMEOUT(6003, "Gateway timeout", HttpStatus.GATEWAY_TIMEOUT),
    CONFIGURATION_ERROR(6004, "System configuration error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNKNOWN_ERROR(6005, "Unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),

    // Notification Errors
    NOTIFICATION_NOT_FOUND(7001, "Notification not found", HttpStatus.NOT_FOUND),

    // Review Errors
    REVIEW_ALREADY_EXISTS(8001, "Review already exists", HttpStatus.CONFLICT),
    REVIEW_NOT_FOUND(8002, "Review not found", HttpStatus.NOT_FOUND),
    USER_HAS_NOT_BORROWED_DOCUMENT(8003, "User has not borrowed this document", HttpStatus.FORBIDDEN);
    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
