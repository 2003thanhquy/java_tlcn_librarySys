package com.spkt.librasys.controller;

import com.nimbusds.jose.JOSEException;
import com.spkt.librasys.dto.request.*;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.AuthenticationResponse;
import com.spkt.librasys.dto.response.IntrospectResponse;
import com.spkt.librasys.service.AuthenticationService;
import com.spkt.librasys.service.VerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;

/**
 * Controller để xử lý các yêu cầu liên quan đến xác thực người dùng.
 * Bao gồm các hành động như đăng nhập, đăng xuất, làm mới token và introspect.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/auth/")
public class AuthenticationController {

    AuthenticationService authenticationService;
    VerificationService verificationService;

    /**
     * Đăng nhập người dùng.
     *
     * @param request Thông tin yêu cầu đăng nhập, bao gồm tên người dùng và mật khẩu.
     * @return Một đối tượng ApiResponse chứa thông tin đăng nhập thành công hoặc thất bại.
     */
    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.login(request))
                .build();
    }

    /**
     * Đăng xuất người dùng.
     *
     * @param request Thông tin yêu cầu đăng xuất.
     * @return Một đối tượng ApiResponse với thông báo đăng xuất thành công.
     * @throws ParseException Nếu có lỗi xảy ra khi phân tích dữ liệu.
     * @throws JOSEException Nếu có lỗi xảy ra trong quá trình xử lý JWT.
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }

    /**
     * Làm mới token của người dùng.
     *
     * @param request Thông tin yêu cầu làm mới token (refresh token).
     * @return Một đối tượng ApiResponse chứa thông tin đăng nhập với token mới.
     * @throws ParseException Nếu có lỗi xảy ra khi phân tích dữ liệu.
     * @throws JOSEException Nếu có lỗi xảy ra trong quá trình xử lý JWT.
     */
    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refresh(request);
        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }

    /**
     * Kiểm tra tính hợp lệ của token thông qua introspection.
     *
     * @param request Thông tin yêu cầu introspect token.
     * @return Một đối tượng ApiResponse chứa thông tin introspect của token.
     */
    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder().result(result).build();
    }

    /**
     * Xử lý yêu cầu đăng nhập qua Google OAuth2.
     * Chuyển hướng người dùng đến endpoint OAuth2 của Google.
     *
     * @param request Đối tượng HttpServletRequest chứa thông tin yêu cầu từ client.
     * @param response Đối tượng HttpServletResponse dùng để chuyển hướng người dùng đến Google.
     * @throws IOException Nếu có lỗi xảy ra khi gửi yêu cầu HTTP.
     */
    @GetMapping("/login-google")
    public void googleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String googleOAuth2Endpoint = "/oauth2/authorization/google";
        request.getSession().invalidate();
        response.sendRedirect(googleOAuth2Endpoint);
    }

    /**
     * Xử lý sự kiện đăng nhập thành công từ Google OAuth2.
     *
     * @param authenticationToken Thông tin xác thực của người dùng sau khi đăng nhập thành công qua Google.
     * @return Một đối tượng ApiResponse chứa thông tin đăng nhập thành công từ Google.
     */
    @GetMapping("/oauth2/success")
    public ApiResponse<AuthenticationResponse> googleLoginSuccess(OAuth2AuthenticationToken authenticationToken) {
        AuthenticationResponse response = authenticationService.handleGoogleLogin(authenticationToken);
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Google login successful")
                .result(response)
                .build();
    }
    /**
     * Xác minh tài khoản người dùng.
     *
     * @param request yêu cầu xác minh tài khoản
     * @return ApiResponse thông báo xác minh tài khoản thành công hoặc thất bại
     */
    @PostMapping("/verify-email")
    public ApiResponse<Void> verifyAccount(@RequestBody VerificationRequest request) {
        boolean isVerified = verificationService.verifyAccount(request);
        return ApiResponse.<Void>builder()
                .message(isVerified ? "Tài khoản đã được xác minh thành công" : "Mã xác minh không hợp lệ hoặc đã hết hạn")
                .build();
    }

    /**
     * Gửi lại mã xác minh cho tài khoản.
     *
     * @param email email của người dùng cần gửi mã xác minh lại
     * @return ApiResponse thông báo gửi lại mã xác minh thành công hoặc thất bại
     */
    @PostMapping("/resend-verify-email")
    public ApiResponse<Void> resendVerificationCode(@RequestParam String email) {
        boolean isResent = verificationService.resendVerificationCode(email);
        return ApiResponse.<Void>builder()
                .message(isResent ? "Mã xác minh đã được gửi lại thành công" : "Gửi lại mã xác minh thất bại. Vui lòng thử lại sau.")
                .build();
    }

    /**
     * Yêu cầu gửi mã reset mật khẩu
     *
     * @param email email của người dùng yêu cầu reset mật khẩu
     * @return ApiResponse thông báo gửi email reset thành công hoặc thất bại
     */
    @PostMapping("/request-reset-password")
    public ApiResponse<Void> requestPasswordReset(@RequestParam String email) {
        verificationService.requestPasswordReset(email);
        return ApiResponse.<Void>builder()
                .message("Email hướng dẫn reset mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư.")
                .build();
    }

    /**
     * Xử lý reset mật khẩu
     *
     * @param resetPasswordRequest yêu cầu reset mật khẩu với mã token và mật khẩu mới
     * @return ApiResponse thông báo kết quả của việc đặt lại mật khẩu
     */
    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        boolean isResetSuccessful = verificationService.resetPassword(resetPasswordRequest);
        return ApiResponse.<Void>builder()
                .message(isResetSuccessful ? "Mật khẩu đã được thay đổi thành công." : "Mã reset mật khẩu không hợp lệ hoặc đã hết hạn.")
                .build();
    }

}
