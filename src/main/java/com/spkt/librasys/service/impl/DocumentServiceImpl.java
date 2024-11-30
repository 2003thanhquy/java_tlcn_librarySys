package com.spkt.librasys.service.impl;

import com.cloudinary.utils.ObjectUtils;
import com.levigo.jbig2.JBIG2ImageReaderSpi;
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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DocumentServiceImpl implements DocumentService {

    DocumentRepository documentRepository;
    DocumentTypeRepository documentTypeRepository;
    AccessHistoryService accessHistoryService;
    DocumentMapper documentMapper;
    SecurityContextService securityContextService;
    FavoriteDocumentRepository favoriteDocumentRepository;
    WarehouseRepository warehouseRepository;
    DocumentHistoryRepository documentHistoryRepository;
    RackRepository rackRepository;
    CloudinaryService cloudinaryService;
    CourseRepository courseRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public DocumentResponse createDocument(DocumentCreateRequest request) {
        try {
            // 1. Kiểm tra và lấy các DocumentType
            Set<DocumentType> documentTypes = getDocumentTypes(request.getDocumentTypeIds());

            // 2. Kiểm tra và lấy Warehouse
            Warehouse warehouse = getWarehouse(request.getWarehouseId());

            // 3. Ánh xạ DocumentCreateMultipartRequest sang Document
            Document document = documentMapper.toDocument(request);
            document.setDocumentTypes(documentTypes);

            // 4. Lấy và thêm các Course vào Document
            Set<Course> courses = getCoursesByIds(request.getCourseIds());
            document.setCourses(courses);

            // 5. Tạo DocumentLocation và gán vào Document
            DocumentLocation location = createDocumentLocation(warehouse, request.getQuantity(), request.getSize());
            document.getLocations().add(location);

            // 6. Upload ảnh bìa lên Cloudinary trước khi lưu Document (nếu có)
            MultipartFile coverImage = request.getImage();
            if (coverImage != null && !coverImage.isEmpty()) {
                try {
                    // Tạo tên tệp duy nhất để tránh trùng lặp
                    String public_id = UUID.randomUUID().toString();
                    Map options = ObjectUtils.asMap(
                            "folder", "document",
                            "overwrite", true,
                            "public_id", public_id
                    );
                    var uploadResult = cloudinaryService.uploadFile(coverImage, options);
                    String coverImageUrl = (String) uploadResult.get("secure_url");
                    document.setCoverImage(coverImageUrl); // Gán URL ảnh bìa
                    document.setImagePublicId(public_id);
                } catch (IOException e) {
                    log.error("Error uploading cover image to Cloudinary: {}", e.getMessage());
                    throw new AppException(ErrorCode.CLOUDINARY_UPLOAD_FAILED, "Error uploading cover image");
                }
            }

            // 7. Upload file PDF và lưu vào thư mục "upload/documents/"
            MultipartFile pdfFile = request.getPdfFile();
            if (pdfFile != null && !pdfFile.isEmpty()) {
                String fileNameOriginal = pdfFile.getOriginalFilename();
                if (!fileNameOriginal.toLowerCase().endsWith(".pdf")) {
                    throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, "Chỉ cho phép upload file PDF");
                }
                try {
                    String fileName = UUID.randomUUID().toString() + ".pdf";
                    String uploadDir = "upload/documents/";
                    File uploadDirFile = new File(uploadDir);
                    if (!uploadDirFile.exists()) {
                        uploadDirFile.mkdirs();
                    }
                    Path filePath = Paths.get(uploadDir).resolve(fileName);
                    Files.copy(pdfFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    document.setDocumentLink(filePath.toString());
                } catch (IOException e) {
                    log.error("Error uploading PDF file: {}", e.getMessage());
                    throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, "Error uploading PDF file");
                }
            }

            // 8. Lưu Document sau khi hoàn tất tất cả thông tin
            Document savedDocument = documentRepository.save(document);

            // 9. Lưu DocumentHistory (nếu có)
            saveDocumentHistory(savedDocument, location, request.getQuantity(), DocumentHistory.Action.ADD);

            // 10. Trả về DocumentResponse
            return documentMapper.toDocumentResponse(savedDocument);

        } catch (Exception e) {
            log.error("Error creating document: {}", e.getMessage());
            throw new AppException(ErrorCode.DATABASE_ERROR, "Error creating document");
        }
    }


    // Phương thức bổ trợ để lấy danh sách Course theo ID
    private Set<Course> getCoursesByIds(Set<Long> courseIds) {
        return courseRepository.findAllById(courseIds).stream().collect(Collectors.toSet());
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

        // 2. Cập nhật thông tin cơ bản của Document
        documentMapper.updateDocument(document, request);

        // 3. Cập nhật DocumentType nếu có thay đổi
        if (request.getDocumentTypeIds() != null && !request.getDocumentTypeIds().isEmpty()) {
            Set<DocumentType> documentTypes = getDocumentTypes(request.getDocumentTypeIds());
            document.setDocumentTypes(documentTypes);
        }

        // 4. Cập nhật hình ảnh (nếu có)
        MultipartFile image = request.getImage();
        if (image != null && !image.isEmpty()) {
            try {
                // Xóa ảnh cũ trên Cloudinary (nếu có)
                if (document.getImagePublicId() != null) {
                    cloudinaryService.deleteFile(document.getImagePublicId());
                }

                // Upload ảnh mới lên Cloudinary
                String publicId = UUID.randomUUID().toString();
                Map<String, Object> options = Map.of(
                        "folder", "document",
                        "overwrite", true,
                        "public_id", publicId
                );
                Map uploadResult = cloudinaryService.uploadFile(image, options);
                document.setCoverImage((String) uploadResult.get("secure_url"));
                document.setImagePublicId(publicId);
            } catch (IOException e) {
                log.error("Error uploading cover image to Cloudinary: {}", e.getMessage());
                throw new AppException(ErrorCode.CLOUDINARY_UPLOAD_FAILED, "Error uploading cover image");
            }
        }

        // 5. Cập nhật file PDF (nếu có)
        MultipartFile pdfFile = request.getPdfFile();
        if (pdfFile != null && !pdfFile.isEmpty()) {
            try {
                // Kiểm tra định dạng file
                if (!pdfFile.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
                    throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, "Chỉ cho phép upload file PDF");
                }

                // Xóa file PDF cũ (nếu có)
                if (document.getDocumentLink() != null) {
                    Files.deleteIfExists(Paths.get(document.getDocumentLink()));
                }

                // Upload file PDF mới
                String fileName = UUID.randomUUID().toString() + ".pdf";
                String uploadDir = "upload/documents/";
                Path filePath = Paths.get(uploadDir).resolve(fileName);
                Files.createDirectories(filePath.getParent());
                Files.copy(pdfFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Gán đường dẫn mới vào document
                document.setDocumentLink(filePath.toString());
            } catch (IOException e) {
                log.error("Error uploading PDF file: {}", e.getMessage());
                throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, "Error uploading PDF file");
            }
        }

        // 6. Lưu Document sau khi cập nhật
        Document updatedDocument = documentRepository.save(document);

        // 7. Trả về DocumentResponse
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
        User userCurrent =  securityContextService.getCurrentUser();
        if (userCurrent == null || userHasRole(userCurrent, PredefinedRole.USER_ROLE)) {
            spec = spec.and((root, query, builder) -> builder.equal(root.get("status"), DocumentStatus.AVAILABLE));
        }


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
    //Read book

    @Override
    public byte[] getDocumentPageContent(Long documentId, int pageNumber) {
        // Lấy tài liệu từ cơ sở dữ liệu hoặc hệ thống lưu trữ
        User userCurr = securityContextService.getCurrentUser();
        if(userCurr == null)
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND,"Tài liệu không tồn tại"));

        // Lấy nội dung trang sách
        byte[] pageContent = extractPageContent(document, pageNumber);

        // Thêm watermark vào nội dung trang
        byte[] watermarkedContent = addWatermarkToImage(pageContent, "User: " + userCurr.getUsername());

        return watermarkedContent;
    }
    public byte[] extractPageContent(Document document, int pageNumber) {
        String filePath = document.getDocumentLink(); // Thay bằng đường dẫn phù hợp
        if (filePath == null || filePath.isEmpty()) {
            throw new RuntimeException("Đường dẫn file của tài liệu không được thiết lập");
        }

        Path pdfPath = Paths.get(filePath).toAbsolutePath();
        System.out.println("Đường dẫn tuyệt đối tới file PDF: " + pdfPath.toString());

        if (!Files.exists(pdfPath)) {
            throw new RuntimeException("File không tồn tại tại đường dẫn: " + pdfPath.toString());
        }

        // Đăng ký JBIG2 ImageIO plugin
        IIORegistry.getDefaultInstance().registerServiceProvider(new JBIG2ImageReaderSpi());

        try (PDDocument pdfDocument = PDDocument.load(pdfPath.toFile())) {
            PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);
            BufferedImage bim = pdfRenderer.renderImageWithDPI(pageNumber - 1, 150, ImageType.RGB);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bim, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc tài liệu", e);
        }
    }
    private byte[] addWatermarkToImage(byte[] imageData, String watermarkText) {
        try {
            InputStream is = new ByteArrayInputStream(imageData);
            BufferedImage image = ImageIO.read(is);

            Graphics2D g2d = (Graphics2D) image.getGraphics();
            AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
            g2d.setComposite(alphaChannel);
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
            FontMetrics fontMetrics = g2d.getFontMetrics();
            Rectangle2D rect = fontMetrics.getStringBounds(watermarkText, g2d);

            int centerX = (image.getWidth() - (int) rect.getWidth()) / 2;
            int centerY = image.getHeight() / 2;

            g2d.drawString(watermarkText, centerX, centerY);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            g2d.dispose();

            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi thêm watermark", e);
        }
    }



}
