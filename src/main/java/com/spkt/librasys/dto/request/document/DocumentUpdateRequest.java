package com.spkt.librasys.dto.request.document;

import com.spkt.librasys.entity.enums.DocumentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentUpdateRequest {

    // Các trường có thể là null nếu không được cung cấp từ phía client
    String documentName;

    String isbn;

    String author;

    String publisher;

    LocalDate publishedDate;

    Integer pageCount;  // Dùng Integer để hỗ trợ null

    String language;

    Integer quantity;  // Dùng Integer để hỗ trợ null

    BigDecimal price;

    String description;

    String documentLink;

 //   Long documentTypeId;  // Có thể không cung cấp nếu không muốn thay đổi loại tài liệu - su dung trong classify

    DocumentStatus status;  // Có thể bỏ trống nếu không muốn thay đổi trạng thái
}
