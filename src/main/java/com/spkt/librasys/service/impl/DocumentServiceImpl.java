package com.spkt.librasys.service.impl;

import com.spkt.librasys.constant.PredefinedRole;
import com.spkt.librasys.dto.request.document.DocumentCreateRequest;
import com.spkt.librasys.dto.request.document.DocumentQuantityUpdateRequest;
import com.spkt.librasys.dto.request.document.DocumentSearchRequest;
import com.spkt.librasys.dto.request.document.DocumentUpdateRequest;
import com.spkt.librasys.dto.response.document.DocumentResponse;
import com.spkt.librasys.entity.*;
import com.spkt.librasys.entity.enums.DocumentStatus;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.DocumentMapper;
import com.spkt.librasys.repository.*;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.service.AccessHistoryService;
import com.spkt.librasys.service.DocumentService;
import com.spkt.librasys.repository.specification.DocumentSpecification;
import com.spkt.librasys.service.SecurityContextService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DocumentServiceImpl implements DocumentService {

    DocumentRepository documentRepository;
    DocumentTypeRepository documentTypeRepository;
    UserRepository userRepository;
    AccessHistoryService accessHistoryService;
    DocumentMapper documentMapper;
    SecurityContextService securityContextService;
    FavoriteDocumentRepository favoriteDocumentRepository;
    WarehouseRepository warehouseRepository;
    DocumentHistoryRepository documentHistoryRepository;
    RackRepository rackRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public DocumentResponse createDocument(DocumentCreateRequest request) {
        // 1. Kiểm tra và lấy các DocumentType
        Set<DocumentType> documentTypes = getDocumentTypes(request.getDocumentTypeIds());

        // 2. Kiểm tra và lấy Warehouse
        Warehouse warehouse = getWarehouse(request.getWarehouseId());

        // 3. Ánh xạ DocumentCreateRequest sang Document
        Document document = documentMapper.toDocument(request);
        document.setDocumentTypes(documentTypes);

        // 4. Tạo DocumentLocation và gán vào Document
        DocumentLocation location = createDocumentLocation(warehouse, request.getQuantity(), request.getSize());
        document.getLocations().add(location);
        // 5. Lưu Document
        Document savedDocument = documentRepository.save(document);

        // 6. Lưu DocumentHistory
        saveDocumentHistory(savedDocument, location, request.getQuantity(), DocumentHistory.Action.ADD);

        // 7. Trả về DocumentResponse
        return documentMapper.toDocumentResponse(savedDocument);
    }

    private Set<DocumentType> getDocumentTypes(Set<Long> documentTypeIds) {
        Set<DocumentType> documentTypes = new HashSet<>(documentTypeRepository.findAllById(documentTypeIds));
        if (documentTypes.size() != documentTypeIds.size()) {
            throw new AppException(ErrorCode.DOCUMENT_TYPE_NOT_FOUND, "One or more DocumentTypeIds are invalid");
        }
        return documentTypes;
    }

    private Warehouse getWarehouse(Long warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND));
    }

    private DocumentLocation createDocumentLocation(Warehouse warehouse, int quantity, com.spkt.librasys.entity.enums.DocumentSize size) {
        DocumentLocation location = DocumentLocation.builder()
                .warehouseId(warehouse.getWarehouseId())
                .rackId(null) // Không gán rack cụ thể lúc tạo
                .quantity(quantity)
                .size(size)
                .build();
        location.updateTotalSize();
        return location;
    }

    private void saveDocumentHistory(Document document, DocumentLocation location, int quantityChange, DocumentHistory.Action action) {
        DocumentHistory history = DocumentHistory.builder()
                .document(document)
                .location(location)
                .changeTime(LocalDateTime.now())
                .action(action)
                .quantityChange(quantityChange)
                .build();
        documentHistoryRepository.save(history);
    }

    //

    @Override
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public DocumentResponse updateDocument(Long id, DocumentUpdateRequest request) {
        // 1. Tìm Document dựa trên ID
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        // 2. Cập nhật Document bằng mapper mà không thay đổi quantity
        documentMapper.updateDocument(document, request);

        // 3. Cập nhật DocumentType nếu có thay đổi trong request
        if (request.getDocumentTypeIds() != null && !request.getDocumentTypeIds().isEmpty()) {
            Set<DocumentType> documentTypes = getDocumentTypes(request.getDocumentTypeIds());
            document.setDocumentTypes(documentTypes);
        }

        // 4. Lưu Document đã cập nhật
        Document updatedDocument = documentRepository.save(document);

        // 5. Trả về DocumentResponse
        return documentMapper.toDocumentResponse(updatedDocument);
    }

    @Override
    public DocumentResponse getDocumentById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));
        User user = securityContextService.getCurrentUser();
        accessHistoryService.recordAccess(user,document,AccessHistory.Activity.VIEWED);
        return documentMapper.toDocumentResponse(document);
    }

    @Override
    public Page<DocumentResponse> searchDocuments(DocumentSearchRequest request, Pageable pageable) {
        Specification<Document> spec = Specification.where(null); // Khởi tạo với null để thêm các điều kiện sau

        // Kiểm tra và thêm điều kiện tìm kiếm dựa trên giá trị của request
        if (request.getDocumentName() != null ) {
            spec = spec.and(DocumentSpecification.hasTitle(request.getDocumentName().trim()));
        }
        if (request.getAuthor() != null ) {
            spec = spec.and(DocumentSpecification.hasAuthor(request.getAuthor().trim()));
        }
        if (request.getPublisher() != null) {
            spec = spec.and(DocumentSpecification.hasPublisher(request.getPublisher().trim()));
        }
        if (request.getDocumentTypeId() != null) {
            spec = spec.and(DocumentSpecification.hasDocumentTypeId(request.getDocumentTypeId()));
        }
        spec = spec.and((root, query, builder) ->
                builder.equal(root.get("status"), DocumentStatus.AVAILABLE));


        // Thực hiện tìm kiếm với Specification đã được thiết lập
        Page<Document> documents = documentRepository.findAll(spec, pageable);
        return documents.map(documentMapper::toDocumentResponse);
    }


    @Override
    public Page<DocumentResponse> getAllDocuments(Pageable pageable) {
        Specification<Document> spec = Specification.where(null);
        User userCurrent =  securityContextService.getCurrentUser();
        if (userCurrent == null || userHasRole(userCurrent, PredefinedRole.USER_ROLE)) {
            spec = spec.and((root, query, builder) -> builder.equal(root.get("status"), DocumentStatus.AVAILABLE));
        }

        Page<Document> documents = documentRepository.findAll(spec, pageable);
        return documents.map(documentMapper::toDocumentResponse);
    }
    private boolean userHasRole(User user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

    @Override
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));
        document.setStatus(DocumentStatus.UNAVAILABLE);
        documentRepository.save(document);

        // Ghi lại lịch sử xóa tài liệu
        User currentUser = securityContextService.getCurrentUser();
        accessHistoryService.recordAccess(currentUser, document, AccessHistory.Activity.DOC_UNAVAILABLE);
    }
    @Override
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Transactional
    public void deleteDocumentsByIds(List<Long> documentIds){
        List<Document> documents = documentRepository.findAllById(documentIds);
        User currentUser = securityContextService.getCurrentUser();
        documents.forEach(document -> {
            document.setStatus(DocumentStatus.UNAVAILABLE);
            accessHistoryService.recordAccess(currentUser, document, AccessHistory.Activity.DOC_UNAVAILABLE);
        });
        documentRepository.saveAll(documents);

    }
    @Override
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public void classifyDocument(Long id, String newTypeName) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        DocumentType newType = documentTypeRepository.findByTypeName(newTypeName)
                .orElseThrow(() -> new RuntimeException("Document type not found"));
       // document.setDocumentType(newType);
        documentRepository.save(document);
    }

    @Override
    public void favoriteDocument(Long documentId) {
        User user =  securityContextService.getCurrentUser();
        if(user == null)
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        // Kiểm tra nếu tài liệu đã được yêu thích trước đó
        if (favoriteDocumentRepository.existsByUserAndDocument(user, document)) {
            throw new RuntimeException("Document already marked as favorite");
        }

        FavoriteDocument favoriteDocument = FavoriteDocument.builder()
                .user(user)
                .document(document)
                .build();

        favoriteDocumentRepository.save(favoriteDocument);
    }
    @Override
    public Page<DocumentResponse> getFavoriteDocuments( Pageable pageable) {
        User user =  securityContextService.getCurrentUser();
        if(user == null)
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        return favoriteDocumentRepository.findAllByUser(user, pageable)
                .map(favoriteDocument -> documentMapper.toDocumentResponse(favoriteDocument.getDocument()));
    }

    @Override
    public void unFavoriteDocument(Long documentId) {
        User user =  securityContextService.getCurrentUser();
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        // Tìm kiếm tài liệu yêu thích với người dùng và tài liệu cụ thể
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));
        FavoriteDocument favoriteDocument = favoriteDocumentRepository.findByUserAndDocument(user, document)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        // Xóa bản ghi yêu thích
        favoriteDocumentRepository.delete(favoriteDocument);
    }

    @Override
    public boolean isFavoriteDocument(Long documentId) {
        User user =  securityContextService.getCurrentUser();
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        // Kiểm tra xem tài liệu có được người dùng yêu thích không
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        return favoriteDocumentRepository.existsByUserAndDocument(user, document);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public void updateQuantity(DocumentQuantityUpdateRequest request) {
        Document document = documentRepository.findById(request.getDocumentId())
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Document not found"));

        switch (request.getLocationType()) {
            case WAREHOUSE:
                updateQuantityInWarehouse(document, request.getLocationId(), request.getQuantityChange());
                break;
            case RACK:
                updateQuantityInRack(document, request.getLocationId(), request.getQuantityChange());
                break;
            default:
                throw new AppException(ErrorCode.INVALID_LOCATION_TYPE, "Invalid location type");
        }
    }
    private void updateQuantityInWarehouse(Document document, Long warehouseId, int newQuantity) {
        // Kiểm tra Warehouse tồn tại
        warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND, "Warehouse not found"));

        // Tìm DocumentLocation cho Warehouse
        DocumentLocation location = document.getLocations().stream()
                .filter(loc -> loc.getWarehouseId().equals(warehouseId) && loc.getRackId() == null)
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_FOUND, "Document location in warehouse not found"));

        if (newQuantity < 0) {
            throw new AppException(ErrorCode.INVALID_QUANTITY, "Quantity cannot be negative");
        }

        // Cập nhật số lượng mới cho location
        int previousQuantity = location.getQuantity();
        location.setQuantity(newQuantity);

        // Cập nhật tổng số lượng trong Document
        int documentQuantityChange = newQuantity - previousQuantity;
        int avai =  document.getAvailableCount() + documentQuantityChange;
        if(avai <0){
            throw new AppException(ErrorCode.INVALID_QUANTITY, "Document Available cannot be negative");
        }
        document.setQuantity(document.getQuantity() + documentQuantityChange);
        document.setAvailableCount(avai);
        DocumentHistory.Action action = DocumentHistory.Action.UPDATE;
        // Lưu lại lịch sử cập nhật nếu có thay đổi
        if (documentQuantityChange != 0) {
            saveDocumentHistory(document, location, documentQuantityChange, action);
        }
    }

    private void updateQuantityInRack(Document document, Long rackId, int newQuantity) {
        // Kiểm tra Rack tồn tại
        rackRepository.findById(rackId)
                .orElseThrow(() -> new AppException(ErrorCode.RACK_NOT_FOUND, "Rack not found"));

        // Tìm DocumentLocation cho Rack
        DocumentLocation location = document.getLocations().stream()
                .filter(loc -> rackId.equals(loc.getRackId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_FOUND, "Document location in rack not found"));

        if (newQuantity < 0) {
            throw new AppException(ErrorCode.INVALID_QUANTITY, "Quantity cannot be negative");
        }

        // Cập nhật số lượng mới cho location
        int previousQuantity = location.getQuantity();
        location.setQuantity(newQuantity);

        // Cập nhật tổng số lượng trong Document
        int documentQuantityChange = newQuantity - previousQuantity;
        int avai =  document.getAvailableCount() + documentQuantityChange;
        if(avai <0){
            throw new AppException(ErrorCode.INVALID_QUANTITY, "Document Available cannot be negative");
        }
        document.setQuantity(document.getQuantity() + documentQuantityChange);
        document.setAvailableCount(avai);
        // Lưu lại lịch sử cập nhật nếu có thay đổi
        if (documentQuantityChange != 0) {
            DocumentHistory.Action action = DocumentHistory.Action.UPDATE;
            saveDocumentHistory(document, location, documentQuantityChange,action);
        }
    }


}
