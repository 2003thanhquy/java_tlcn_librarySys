package com.spkt.librasys.mapper;

import com.spkt.librasys.dto.request.document.DocumentCreateRequest;
import com.spkt.librasys.dto.request.document.DocumentUpdateRequest;
import com.spkt.librasys.dto.response.document.DocumentResponse;
import com.spkt.librasys.entity.Document;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(source = "documentTypeId", target = "documentType.documentTypeId")
    Document toDocument(DocumentCreateRequest request);

    @Mapping(source = "documentType.typeName", target = "documentTypeName")
    DocumentResponse toDocumentResponse(Document document);

   // @Mapping(target = "loanTransactions", ignore = true)
    @Mapping(target = "accessHistories", ignore = true)
  //  @Mapping(source = "documentTypeId", target = "documentType.documentTypeId")
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    void updateDocument(@MappingTarget Document document, DocumentUpdateRequest request);
}
