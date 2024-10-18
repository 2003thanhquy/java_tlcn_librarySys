package com.spkt.librasys.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spkt.librasys.dto.request.user.UserCreateRequest;
import com.spkt.librasys.dto.request.user.UserUpdateRequest;
import com.spkt.librasys.dto.response.user.UserResponse;
import com.spkt.librasys.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllUsers() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        UserResponse response = new UserResponse();
        response.setUsername("testuser");
        Page<UserResponse> users = new PageImpl<>(Collections.singletonList(response));
        Mockito.when(userService.getAllUsers(any(), any(Pageable.class))).thenReturn(users);

        mockMvc.perform(get("/api/v1/users")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.content").isNotEmpty());
    }

    @Test
    public void testCreateUser() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("newuser");
        request.setFirstName("New");
        request.setLastName("User");

        UserResponse response = new UserResponse();
        response.setUsername("newuser");

        Mockito.when(userService.createUser(any(UserCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.username").value("newuser"));
    }

    @Test
    public void testGetUserById() throws Exception {
        UserResponse response = new UserResponse();
        response.setUsername("testuser");

        Mockito.when(userService.getUserById("testId")).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/testId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.username").value("testuser"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Updated");
        request.setLastName("User");

        UserResponse response = new UserResponse();
        response.setFirstName("Updated");
        response.setLastName("User");

        Mockito.when(userService.updateUser(Mockito.anyString(), Mockito.any(UserUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/users/testId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.firstName").value("Updated"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        Mockito.doNothing().when(userService).deleteUser("testId");

        mockMvc.perform(delete("/api/v1/users/testId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }
}
