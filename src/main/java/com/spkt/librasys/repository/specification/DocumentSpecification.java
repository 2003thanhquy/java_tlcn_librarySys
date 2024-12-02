package com.spkt.librasys.repository.specification;

import com.spkt.librasys.entity.Course;
import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.DocumentType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class DocumentSpecification {

    public static Specification<Document> hasTitle(String documentName) {
        return (root, query, builder) ->
                documentName == null ? null : builder.like(builder.lower(root.get("documentName")), "%" + documentName.toLowerCase() + "%");
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
    public static Specification<Document> hasDocumentTypeIds(Long[] documentTypeIds) {
        return (root, query, builder) -> {
            if (documentTypeIds == null || documentTypeIds.length == 0) {
                return null; // Nếu mảng rỗng, không cần thêm điều kiện
            }

            // Join với bảng 'documentTypes' để lấy các loại tài liệu liên quan
            Join<Document, DocumentType> documentTypeJoin = root.join("documentTypes");

            // Điều kiện kiểm tra tài liệu có tất cả các loại tài liệu trong mảng documentTypeIds
            Predicate predicate = documentTypeJoin.get("documentTypeId").in((Object[]) documentTypeIds);

            // Kiểm tra số lượng các loại tài liệu của tài liệu phải bằng số lượng trong mảng
            query.groupBy(root.get("documentId"));
            query.having(builder.equal(builder.count(documentTypeJoin), documentTypeIds.length));

            // Đảm bảo rằng kết quả là duy nhất
            query.distinct(true);

            return builder.and(predicate);
        };
    }

    public static Specification<Document> hasCourseIds(Long[] courseIds) {
        return (root, query, builder) -> {
            if (courseIds == null || courseIds.length == 0) {
                return null; // Nếu mảng rỗng, không cần thêm điều kiện
            }

            // Join với bảng 'courses' để lấy các khóa học liên quan
            Join<Document, Course> courseJoin = root.join("courses");

            // Điều kiện kiểm tra tài liệu có tất cả các khóa học trong mảng courseIds
            Predicate predicate = courseJoin.get("courseId").in((Object[]) courseIds);

            // Kiểm tra số lượng các khóa học của tài liệu phải bằng số lượng trong mảng
            query.groupBy(root.get("documentId"));
            query.having(builder.equal(builder.count(courseJoin), courseIds.length));

            // Đảm bảo rằng kết quả là duy nhất
            query.distinct(true);

            return builder.and(predicate);
        };
    }
}
