package com.spkt.librasys.mapper;


import com.spkt.librasys.dto.request.document.DocumentTypeCreateRequest;
import com.spkt.librasys.dto.response.document.DocumentTypeResponse;
import com.spkt.librasys.entity.DocumentType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentTypeMapper {
    DocumentType toDocumentType(DocumentTypeCreateRequest request);
    DocumentTypeResponse toDocumentTypeResponse(DocumentType documentType);
}