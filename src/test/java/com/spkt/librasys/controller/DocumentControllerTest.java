package com.spkt.librasys.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.spkt.librasys.dto.request.document.DocumentCreateRequest;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.document.DocumentResponse;
import com.spkt.librasys.service.DocumentService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
public class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateDocument() throws Exception {
        DocumentCreateRequest request = new DocumentCreateRequest();
        request.setDocumentName("Test Document");

        DocumentResponse response = new DocumentResponse();
        response.setDocumentName("Test Document");

        Mockito.when(documentService.createDocument(Mockito.any(DocumentCreateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(ApiResponse.<DocumentResponse>builder()
                        .code(1000)
                        .message("Document created successfully")
                        .result(response)
                        .build())));
    }

//    @Test
//    public void testGetAllDocuments() throws Exception {
//        DocumentResponse response = new DocumentResponse();
//        response.setDocumentName("Test Document");
//
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<DocumentResponse> pageResponse = new PageImpl<>(Collections.singletonList(response), pageable, 1);
//
//        Mockito.when(documentService.getAllDocuments(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
//                .thenReturn(pageResponse);
//
//        mockMvc.perform(get("/api/v1/documents?page=0&size=10")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(content().json(objectMapper.writeValueAsString(ApiResponse.<Page<DocumentResponse>>builder()
//                        .code(1000)
//                        .message("Documents retrieved successfully")
//                        .result(pageResponse)
//                        .build())));
//    }
}
