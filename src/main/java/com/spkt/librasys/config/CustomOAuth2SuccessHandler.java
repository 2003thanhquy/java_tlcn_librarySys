package com.spkt.librasys.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Lấy access token hoặc refresh token từ Google OAuth2
        String googleAccessToken = (String) request.getAttribute("access_token");

        if (googleAccessToken != null) {
            // Hủy token của Google
            revokeGoogleToken(googleAccessToken);
        }

        // Tiếp tục logic để cấp token hệ thống của bạn hoặc chuyển hướng
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private void revokeGoogleToken(String token) {
        String revokeUrl = "https://accounts.google.com/o/oauth2/revoke?token=" + token;
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject(revokeUrl, null, String.class);
            // Log hoặc xử lý thành công
        } catch (Exception e) {
            // Log hoặc xử lý khi không hủy được token Google
        }
    }
}
