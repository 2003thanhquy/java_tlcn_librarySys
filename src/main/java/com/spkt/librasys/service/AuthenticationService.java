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

public interface AuthenticationService {
    User getCurrentUser();
    Boolean isAdmin(User user);
    AuthenticationResponse login(AuthenticationRequest request);
    void logout(LogoutRequest request) throws ParseException, JOSEException;
    IntrospectResponse introspect(IntrospectRequest request);
    AuthenticationResponse refresh(RefreshRequest request) throws JOSEException, ParseException;
    AuthenticationResponse handleGoogleLogin(OAuth2AuthenticationToken authenticationToken);
}
