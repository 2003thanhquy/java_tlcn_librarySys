package com.spkt.librasys.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spkt.librasys.dto.request.AuthenticationRequest;
import com.spkt.librasys.dto.request.RefreshRequest;
import com.spkt.librasys.dto.response.AuthenticationResponse;
import com.spkt.librasys.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testLogin() throws Exception {
        // Giả lập yêu cầu đăng nhập
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        // Giả lập phản hồi từ service
        AuthenticationResponse response = AuthenticationResponse.builder()
                .token("dummyToken")
                .authenticated(true)
                .build();

        Mockito.when(authenticationService.login(Mockito.any(AuthenticationRequest.class)))
                .thenReturn(response);

        // Kiểm thử endpoint /api/v1/auth/login
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void testRefreshToken() throws Exception {
        // Giả lập yêu cầu làm mới token
        RefreshRequest request = new RefreshRequest();
        request.setToken("oldToken");

        // Giả lập phản hồi từ service
        AuthenticationResponse response = AuthenticationResponse.builder()
                .token("newToken")
                .authenticated(true)
                .build();

        Mockito.when(authenticationService.refresh(Mockito.any(RefreshRequest.class)))
                .thenReturn(response);

        // Kiểm thử endpoint /api/v1/auth/refresh
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void testLogout() throws Exception {
        // Giả lập yêu cầu logout
        String token = "Bearer dummyToken";

        // Không cần phản hồi từ service vì logout chỉ là hành động đơn giản
       // Mockito.doNothing().when(authenticationService).logout(Mockito.anyString());

        // Kiểm thử endpoint /api/v1/auth/logout
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", token))
                .andExpect(status().isNoContent());
    }
}
