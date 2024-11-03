package com.spkt.librasys.mapper;

import com.spkt.librasys.dto.request.document.DocumentCreateRequest;
import com.spkt.librasys.dto.request.document.DocumentUpdateRequest;
import com.spkt.librasys.dto.response.document.DocumentResponse;
import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.DocumentType;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(target = "documentTypes", ignore = true) // Bỏ qua vì sẽ gán thủ công trong service
    Document toDocument(DocumentCreateRequest request);

    @Mapping(target = "documentTypes", source = "documentTypes") // Chuyển đổi DocumentType thành ID
    @Mapping(target = "documentLocations", source = "locations") // Ánh xạ locations vào documentLocations
    DocumentResponse toDocumentResponse(Document document);

    //@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDocument(@MappingTarget Document document, DocumentUpdateRequest request);

    // Phương thức lấy tên loại tài liệu từ Set<DocumentType>
    default Set<String> getDocumentTypeIds(Document document) {
        return document.getDocumentTypes().stream()
                .map(DocumentType::getTypeName)
                .collect(Collectors.toSet());
    }
}
