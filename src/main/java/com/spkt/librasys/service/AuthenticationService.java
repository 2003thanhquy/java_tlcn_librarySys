package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.AuthenticationRequest;
import com.spkt.librasys.dto.request.IntrospectRequest;
import com.spkt.librasys.dto.request.LogoutRequest;
import com.spkt.librasys.dto.request.RefreshRequest;
import com.spkt.librasys.dto.response.AuthenticationResponse;
import com.spkt.librasys.dto.response.IntrospectResponse;

public interface AuthenticationService {
    AuthenticationResponse login(AuthenticationRequest request);
    void logout(LogoutRequest request);
    IntrospectResponse introspect(IntrospectRequest request);
    AuthenticationResponse refresh(RefreshRequest request);
}
