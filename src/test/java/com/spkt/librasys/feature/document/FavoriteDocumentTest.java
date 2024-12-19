package com.spkt.librasys.feature.document;

import com.spkt.librasys.service.DocumentService;
import com.spkt.librasys.controller.DocumentController;
import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.entity.FavoriteDocument;
import com.spkt.librasys.repository.DocumentRepository;
import com.spkt.librasys.repository.FavoriteDocumentRepository;
import com.spkt.librasys.service.SecurityContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FavoriteDocumentTest {

    private MockMvc mockMvc;

    @Mock
    private DocumentService documentService;

    @Mock
    private SecurityContextService securityContextService;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private FavoriteDocumentRepository favoriteDocumentRepository;

    @InjectMocks
    private DocumentController documentController;

    private User mockUser;
    private Document mockDocument;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();

        // Giả lập User và Document
        mockUser = new User();
        mockUser.setUserId("1L");
        mockUser.setUsername("user01");

        mockDocument = new Document();
        mockDocument.setDocumentId(1L);
        mockDocument.setDocumentName("Test Document");
    }

    @Test
    void testFavoriteDocument_Success() throws Exception {
        // Giả lập khi người dùng đã đăng nhập
        when(securityContextService.getCurrentUser()).thenReturn(mockUser);
        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));
        when(favoriteDocumentRepository.existsByUserAndDocument(mockUser, mockDocument)).thenReturn(false);

        // Giả lập lưu tài liệu yêu thích
        FavoriteDocument favoriteDocument = new FavoriteDocument();
        favoriteDocument.setUser(mockUser);
        favoriteDocument.setDocument(mockDocument);
        when(favoriteDocumentRepository.save(any(FavoriteDocument.class))).thenReturn(favoriteDocument);

        // Gọi API
        mockMvc.perform(post("/api/v1/documents/1/favorite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tài liệu đã được đánh dấu yêu thích thành công"));

    }

    @Test
    void testFavoriteDocument_DocumentAlreadyFavorited() throws Exception {
        // Giả lập khi người dùng đã đăng nhập và tài liệu đã được yêu thích
        when(securityContextService.getCurrentUser()).thenReturn(mockUser);
        when(documentRepository.findById(1L)).thenReturn(java.util.Optional.of(mockDocument));
        when(favoriteDocumentRepository.existsByUserAndDocument(mockUser, mockDocument)).thenReturn(true);

        // Gọi API và kiểm tra ngoại lệ khi tài liệu đã yêu thích
        mockMvc.perform(post("/api/v1/documents/1/favorite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1003))
                .andExpect(jsonPath("$.message").value("Document already marked as favorite"));
    }
}

