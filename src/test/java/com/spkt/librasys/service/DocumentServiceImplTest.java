package com.spkt.librasys.service;


import com.github.javafaker.Faker;
import com.spkt.librasys.dto.request.document.DocumentCreateRequest;
import com.spkt.librasys.dto.response.document.DocumentResponse;
import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.DocumentType;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.DocumentMapper;
import com.spkt.librasys.repository.DocumentRepository;
import com.spkt.librasys.repository.DocumentTypeRepository;
import com.spkt.librasys.service.impl.DocumentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

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

//    @Test
//    public void createFakeDocumentForTesting() {
//        DocumentType documentType = new DocumentType();
//        documentType.setDocumentTypeId(1L);
//        documentType.setTypeName("General Knowledge");
//
//        Document document = Document.builder()
//                .documentName(faker.book().title())
//                .author(faker.book().author())
//                .publisher(faker.book().publisher())
//                .publishedDate(LocalDate.now().minusDays(random.nextInt(1000)))
//                .pageCount(random.nextInt(200) + 100)
//                .quantity(random.nextInt(10) + 1)
//                .description(faker.lorem().sentence())
//                .documentLink("https://example.com/" + faker.lorem().word())
//                .documentType(documentType)
//                .build();
//
//        when(documentRepository.save(document)).thenReturn(document);
//    }
    @Test
    public void testRepo() {
        DocumentType documentType = documentTypeRepository.findById(1L)
                .orElseThrow(() -> new NoSuchElementException("DocumentType with ID 1 not found"));

        Set<Document> documents = documentType.getDocuments(); // Truy cập vào danh sách Document
        System.out.println(documents);
    }

    @Test
    public void testGetDocumentByIdNotFound() {
        when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> documentService.getDocumentById(1L));
        assertEquals(ErrorCode.DOCUMENT_NOT_FOUND, exception.getErrorCode());
    }

//    @Test
//    public void testGetAllDocumentsWithPagination() {
//        Pageable pageable = PageRequest.of(0, 10);
//        Document document = new Document();
//
//        // Sử dụng ArgumentMatchers.any(Specification.class) để tránh lỗi ép kiểu
//        when(documentRepository.findAll(any(Specification.class), eq(pageable)))
//                .thenReturn(new PageImpl<>(Collections.singletonList(document)));
//
//        DocumentResponse response = new DocumentResponse();
//        when(documentMapper.toDocumentResponse(any())).thenReturn(response);
//
//        var result = documentService.getAllDocuments(null, null, null, null, pageable);
//        assertNotNull(result);
//        assertEquals(1, result.getContent().size());
//    }
}
