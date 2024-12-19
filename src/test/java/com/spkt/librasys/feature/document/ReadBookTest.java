package com.spkt.librasys.feature.document;

import com.spkt.librasys.service.DocumentService;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.controller.DocumentController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Base64;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReadBookTest {

    private MockMvc mockMvc;

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private DocumentController documentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();
    }

    @Test
    void testReadDocument_Success() throws Exception {
        // Giả lập hành vi của documentService
        byte[] mockPageContent = new byte[]{1, 2, 3, 4, 5};
        when(documentService.getDocumentPageContent(1L, 1)).thenReturn(mockPageContent);

        // Giả lập Base64 mã hóa
        String base64Content = Base64.getEncoder().encodeToString(mockPageContent);

        // Thực hiện yêu cầu GET và kiểm tra phản hồi
        mockMvc.perform(get("/api/v1/documents/1/read")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Nội dung trang tài liệu đã được lấy thành công"))
                .andExpect(jsonPath("$.result").value(base64Content));
    }

    @Test
    void testReadDocument_Failure() throws Exception {
        // Giả lập khi gặp lỗi trong documentService
        when(documentService.getDocumentPageContent(1L, 1)).thenThrow(new RuntimeException("Lỗi khi lấy tài liệu"));

        // Thực hiện yêu cầu GET và kiểm tra phản hồi lỗi
        mockMvc.perform(get("/api/v1/documents/1/read")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1001))
                .andExpect(jsonPath("$.message").value("Lỗi khi lấy nội dung trang tài liệu: Lỗi khi lấy tài liệu"));
    }
}
