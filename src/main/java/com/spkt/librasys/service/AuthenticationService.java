package com.spkt.librasys.service;

import com.nimbusds.jose.JOSEException;
import com.spkt.librasys.dto.request.AuthenticationRequest;
import com.spkt.librasys.dto.request.IntrospectRequest;
import com.spkt.librasys.dto.request.LogoutRequest;
import com.spkt.librasys.dto.request.RefreshRequest;
import com.spkt.librasys.dto.response.AuthenticationResponse;
import com.spkt.librasys.dto.response.IntrospectResponse;
import com.spkt.librasys.entity.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.text.ParseException;

/**
 * Interface cung cấp các phương thức xử lý liên quan đến việc xác thực người dùng,
 * bao gồm đăng nhập, đăng xuất, kiểm tra thông tin token, và làm mới token.
 */
public interface AuthenticationService {

    /**
     * Đăng nhập người dùng.
     *
     * @param request Thông tin yêu cầu đăng nhập bao gồm username và password.
     * @return Response chứa thông tin xác thực như access token, refresh token, và thông tin người dùng.
     */
    AuthenticationResponse login(AuthenticationRequest request);

    /**
     * Đăng xuất người dùng.
     *
     * @param request Thông tin yêu cầu đăng xuất.
     * @throws ParseException Nếu có lỗi khi phân tích token.
     * @throws JOSEException Nếu có lỗi khi xử lý JSON Web Token (JWT).
     */
    void logout(LogoutRequest request) throws ParseException, JOSEException;

    /**
     * Kiểm tra và xác thực token OAuth2.
     *
     * @param request Thông tin yêu cầu để kiểm tra token (introspect).
     * @return Thông tin chi tiết về token, bao gồm trạng thái của token (còn hiệu lực hay không).
     */
    IntrospectResponse introspect(IntrospectRequest request);

    /**
     * Làm mới (refresh) token.
     *
     * @param request Thông tin yêu cầu làm mới token (refresh token).
     * @return Response chứa access token mới.
     * @throws JOSEException Nếu có lỗi khi xử lý JSON Web Token (JWT).
     * @throws ParseException Nếu có lỗi khi phân tích token.
     */
    AuthenticationResponse refresh(RefreshRequest request) throws JOSEException, ParseException;

    /**
     * Xử lý đăng nhập qua Google.
     *
     * @param authenticationToken Token xác thực OAuth2 từ Google.
     * @return Thông tin phản hồi sau khi xác thực thành công qua Google.
     */
    AuthenticationResponse handleGoogleLogin(OAuth2AuthenticationToken authenticationToken);
}
