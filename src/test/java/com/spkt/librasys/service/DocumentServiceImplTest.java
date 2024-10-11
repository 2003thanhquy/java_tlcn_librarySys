package com.spkt.librasys.service;


import com.github.javafaker.Faker;
import com.spkt.librasys.dto.request.documentRequest.DocumentCreateRequest;
import com.spkt.librasys.dto.request.documentRequest.DocumentUpdateRequest;
import com.spkt.librasys.dto.response.documentResponse.DocumentResponse;
import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.DocumentType;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.DocumentMapper;
import com.spkt.librasys.repository.document.DocumentRepository;
import com.spkt.librasys.repository.document.DocumentTypeRepository;
import com.spkt.librasys.service.DocumentService;
import com.spkt.librasys.service.impl.DocumentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DocumentServiceImplTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentTypeRepository documentTypeRepository;

    @Mock
    private DocumentMapper documentMapper;

    @InjectMocks
    private DocumentServiceImpl documentService;

    private Faker faker;
    private Random random;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        faker = new Faker();
        random = new Random();
    }

    @Test
    public void createFakeDocumentForTesting() {
        DocumentType documentType = new DocumentType();
        documentType.setDocumentTypeId(1L);
        documentType.setTypeName("General Knowledge");

        Document document = Document.builder()
                .documentName(faker.book().title())
                .author(faker.book().author())
                .publisher(faker.book().publisher())
                .publishedDate(LocalDate.now().minusDays(random.nextInt(1000)))
                .pageCount(random.nextInt(200) + 100)
                .quantity(random.nextInt(10) + 1)
                .description(faker.lorem().sentence())
                .documentLink("https://example.com/" + faker.lorem().word())
                .documentType(documentType)
                .build();

        when(documentRepository.save(document)).thenReturn(document);
    }

    @Test
    public void testCreateDocumentSuccess() {
        DocumentCreateRequest request = new DocumentCreateRequest();
        request.setDocumentTypeId(1L);
        request.setDocumentName("Test Document");

        DocumentType documentType = new DocumentType();
        documentType.setDocumentTypeId(1L);

        Document document = new Document();
        when(documentTypeRepository.findById(1L)).thenReturn(Optional.of(documentType));
        when(documentMapper.toDocument(request)).thenReturn(document);
        when(documentRepository.save(document)).thenReturn(document);

        DocumentResponse response = new DocumentResponse();
        when(documentMapper.toDocumentResponse(document)).thenReturn(response);

        DocumentResponse result = documentService.createDocument(request);
        assertNotNull(result);
        verify(documentRepository, times(1)).save(document);
    }

    @Test
    public void testGetDocumentByIdNotFound() {
        when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> documentService.getDocumentById(1L));
        assertEquals(ErrorCode.DOCUMENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void testGetAllDocumentsWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Document document = new Document();

        // Sử dụng ArgumentMatchers.any(Specification.class) để tránh lỗi ép kiểu
        when(documentRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(Collections.singletonList(document)));

        DocumentResponse response = new DocumentResponse();
        when(documentMapper.toDocumentResponse(any())).thenReturn(response);

        var result = documentService.getAllDocuments(null, null, null, null, pageable);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }
}
