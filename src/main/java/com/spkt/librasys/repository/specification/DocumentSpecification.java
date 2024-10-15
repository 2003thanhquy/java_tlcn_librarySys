package com.spkt.librasys.repository.specification;

import com.spkt.librasys.entity.Document;
import org.springframework.data.jpa.domain.Specification;

public class DocumentSpecification {

    public static Specification<Document> hasTitle(String title) {
        return (root, query, builder) ->
                title == null ? null : builder.like(builder.lower(root.get("documentName")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Document> hasAuthor(String author) {
        return (root, query, builder) ->
                author == null ? null : builder.like(builder.lower(root.get("author")), "%" + author.toLowerCase() + "%");
    }

    public static Specification<Document> hasPublisher(String publisher) {
        return (root, query, builder) ->
                publisher == null ? null : builder.like(builder.lower(root.get("publisher")), "%" + publisher.toLowerCase() + "%");
    }

    public static Specification<Document> hasDocumentTypeId(Long documentTypeId) {
        return (root, query, builder) ->
                documentTypeId == null ? null : builder.equal(root.get("documentType").get("documentTypeId"), documentTypeId);
    }
}
