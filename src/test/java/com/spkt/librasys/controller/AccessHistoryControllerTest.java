package com.spkt.librasys.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spkt.librasys.dto.response.accessHistoryResponse.AccessHistoryResponse;
import com.spkt.librasys.service.AccessHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccessHistoryController.class)
public class AccessHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccessHistoryService accessHistoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllAccessHistories() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        AccessHistoryResponse response = new AccessHistoryResponse();
        Page<AccessHistoryResponse> page = new PageImpl<>(Collections.singletonList(response));

        when(accessHistoryService.getAllAccessHistories(pageable)).thenReturn(page);

        mockMvc.perform(get("/api/v1/access-histories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteAccessHistory() throws Exception {
        doNothing().when(accessHistoryService).deleteAccessHistoryById(1L);

        mockMvc.perform(delete("/api/v1/access-histories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
