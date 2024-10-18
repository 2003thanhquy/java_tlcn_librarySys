package com.spkt.librasys.mapper;

import com.spkt.librasys.dto.request.document.DocumentCreateRequest;
import com.spkt.librasys.dto.request.document.DocumentUpdateRequest;
import com.spkt.librasys.dto.response.document.DocumentResponse;
import com.spkt.librasys.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(source = "documentTypeId", target = "documentType.documentTypeId")
    Document toDocument(DocumentCreateRequest request);

    @Mapping(source = "documentType.typeName", target = "documentTypeName")
    DocumentResponse toDocumentResponse(Document document);

    @Mapping(target = "loanTransactions", ignore = true)
    @Mapping(target = "accessHistories", ignore = true)
    @Mapping(source = "documentTypeId", target = "documentType.documentTypeId")
    void updateDocument(@MappingTarget Document document, DocumentUpdateRequest request);
}
