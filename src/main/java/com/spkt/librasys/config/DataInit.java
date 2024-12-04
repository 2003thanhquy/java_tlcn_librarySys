package com.spkt.librasys.config;

import com.spkt.librasys.entity.*;
import com.spkt.librasys.entity.enums.DocumentSize;
import com.spkt.librasys.entity.enums.DocumentStatus;
import com.spkt.librasys.repository.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataInit implements CommandLineRunner {
    DepartmentRepository departmentRepository;
    CourseRepository courseRepository;
    DocumentRepository documentRepository;
    DocumentTypeRepository documentTypeRepository;
    private final WarehouseRepository warehouseRepository;
    private final LoanPolicyRepository loanPolicyRepository;

    @Override
    @Transactional // Thêm annotation này
    public void run(String... args) throws Exception {
        // 1. Tạo và lưu Departments nếu chưa có
        if (departmentRepository.count() < 1) {
            Department csDepartment = Department.builder()
                    .departmentCodeId(10L)
                    .departmentCode("CS")
                    .departmentName("Công nghệ Thông tin")
                    .description("Mô tả ngành Công nghệ Thông tin")
                    .build();

            Department econDepartment = Department.builder()
                    .departmentCodeId(22L)
                    .departmentCode("ECON")
                    .departmentName("Kinh tế")
                    .description("Mô tả ngành Kinh tế")
                    .build();

            departmentRepository.saveAll(Arrays.asList(csDepartment, econDepartment));
        }

        // 2. Tạo và lưu Courses nếu chưa có
        if (courseRepository.count() < 1) {
            List<Course> courses = Arrays.asList(
                    Course.builder()
                            .courseCode("GEFC220105")
                            .courseName("Kinh tế học đại cương")
                            .description("Mô tả môn Kinh tế học đại cương")
                            .build(),
                    Course.builder()
                            .courseCode("IQMA220205")
                            .courseName("Nhập môn quản trị chất lượng")
                            .description("Mô tả môn Nhập môn quản trị chất lượng")
                            .build(),
                    Course.builder()
                            .courseCode("INMA220305")
                            .courseName("Nhập môn Quản trị học")
                            .description("Mô tả môn Nhập môn Quản trị học")
                            .build(),
                    Course.builder()
                            .courseCode("INLO220405")
                            .courseName("Nhập môn Logic học")
                            .description("Mô tả môn Nhập môn Logic học")
                            .build(),
                    Course.builder()
                            .courseCode("TOEN430979")
                            .courseName("Công cụ và môi trường phát triển PM")
                            .description("Mô tả môn Công cụ và môi trường phát triển PM")
                            .build(),
                    Course.builder()
                            .courseCode("SOPM431679")
                            .courseName("Quản lý dự án phần mềm")
                            .description("Mô tả môn Quản lý dự án phần mềm")
                            .build(),
                    Course.builder()
                            .courseCode("ADMP431879")
                            .courseName("Lập trình di động nâng cao")
                            .description("Mô tả môn Lập trình di động nâng cao")
                            .build(),
                    Course.builder()
                            .courseCode("ADPL331379")
                            .courseName("Ngôn ngữ Lập trình tiên tiến")
                            .description("Mô tả môn Ngôn ngữ Lập trình tiên tiến")
                            .build(),
                    Course.builder()
                            .courseCode("DIFO432180")
                            .courseName("Pháp lý kỹ thuật số")
                            .description("Mô tả môn Pháp lý kỹ thuật số")
                            .build(),
                    Course.builder()
                            .courseCode("NSMS432280")
                            .courseName("Hệ thống giám sát an toàn mạng")
                            .description("Mô tả môn Hệ thống giám sát an toàn mạng")
                            .build(),
                    Course.builder()
                            .courseCode("WISE432380")
                            .courseName("An toàn mạng không dây và di động")
                            .description("Mô tả môn An toàn mạng không dây và di động")
                            .build(),
                    Course.builder()
                            .courseCode("CLAD432480")
                            .courseName("Quản trị trên môi trường cloud")
                            .description("Mô tả môn Quản trị trên môi trường cloud")
                            .build(),
                    Course.builder()
                            .courseCode("ADDB331784")
                            .courseName("Cơ sở dữ liệu Nâng cao")
                            .description("Mô tả môn Cơ sở dữ liệu Nâng cao")
                            .build(),
                    Course.builder()
                            .courseCode("DAWH430784")
                            .courseName("Kho dữ liệu")
                            .description("Mô tả môn Kho dữ liệu")
                            .build(),
                    Course.builder()
                            .courseCode("INRE431084")
                            .courseName("Truy tìm thông tin")
                            .description("Mô tả môn Truy tìm thông tin")
                            .build(),
                    Course.builder()
                            .courseCode("SEEN431579")
                            .courseName("Search Engine")
                            .description("Mô tả môn Search Engine")
                            .build()
            );

            courseRepository.saveAll(courses);
        }

        // 3. Tạo và lưu DocumentType nếu chưa có
        if (documentTypeRepository.count() < 4) {
            DocumentType type1 = DocumentType.builder()
                    .typeName("Fiction")
                    .description("Fictional Books")
                    .build();

            DocumentType type2 = DocumentType.builder()
                    .typeName("Science")
                    .description("Scientific Journals and Books")
                    .build();

            DocumentType type3 = DocumentType.builder()
                    .typeName("History")
                    .description("Historical Documents")
                    .build();

            DocumentType type4 = DocumentType.builder()
                    .typeName("Technology")
                    .description("Tech-related Manuals and Guides")
                    .build();

            documentTypeRepository.saveAll(Arrays.asList(type1, type2, type3, type4));

            LoanPolicy loanPolicy1 = LoanPolicy.builder()
                    .maxRenewals(2)
                    .maxLoanDays(90)
                    .documentType(type1)
                    .build();
            LoanPolicy loanPolicy2 = LoanPolicy.builder()
                    .maxRenewals(2)
                    .maxLoanDays(90)
                    .documentType(type2)
                    .build();
            LoanPolicy loanPolicy3= LoanPolicy.builder()
                    .maxRenewals(2)
                    .maxLoanDays(90)
                    .documentType(type3)
                    .build();
            LoanPolicy loanPolicy4 = LoanPolicy.builder()
                    .maxRenewals(2)
                    .maxLoanDays(90)
                    .documentType(type4)
                    .build();
            loanPolicyRepository.saveAll(List.of(loanPolicy1, loanPolicy2, loanPolicy3,loanPolicy4));
        }

        //insert wareHouse default document -> warehouse
        Warehouse warehouse = Warehouse.builder()
                .warehouseName("Main Warehouse")
                .location("123 Main St, City Center")
                .build();
        if (warehouseRepository.count() < 1) {
            warehouseRepository.save(warehouse); // Lưu Warehouse vào cơ sở dữ liệu
            // Tạo dữ liệu mẫu cho DisplayZone trong Warehouse
            DisplayZone displayZone = DisplayZone.builder()
                    .zoneName("Zone A")
                    .build();

            //displayZoneRepository.save(displayZone); // Lưu DisplayZone vào cơ sở dữ liệu

            // Tạo các Shelf trong DisplayZone
            Shelf shelf1 = Shelf.builder()
                    .shelfNumber("Shelf 1")
                    .zone(displayZone)
                    .build();

            Shelf shelf2 = Shelf.builder()
                    .shelfNumber("Shelf 2")
                    .zone(displayZone)
                    .build();

            //shelfRepository.saveAll(List.of(shelf1, shelf2)); // Lưu các Shelf vào cơ sở dữ liệu

            // Tạo các Rack trên mỗi Shelf và thiết lập capacity
            Rack rack1 = Rack.builder()
                    .rackNumber("Rack 1A")
                    .capacity(1000.0) // dung lượng tối đa của Rack
                    .shelf(shelf1)
                    .build();

            Rack rack2 = Rack.builder()
                    .rackNumber("Rack 2A")
                    .capacity(2000.0)
                    .shelf(shelf1)
                    .build();

            Rack rack3 = Rack.builder()
                    .rackNumber("Rack 1B")
                    .capacity(1500.0)
                    .shelf(shelf2)
                    .build();

            //rackRepository.saveAll(List.of(rack1, rack2, rack3)); // Lưu các Rack vào cơ sở dữ liệu
        }


        // 4. Chèn dữ liệu cho Documents sử dụng builder pattern
        if (documentRepository.count() <1) {
            // Đảm bảo chèn đầy đủ 40 documents
            // Định dạng ngày tháng
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

            // Tạo danh sách các Documents
            List<Document> documents = Arrays.asList(
                    // Document 1
                    Document.builder()
                            .isbn("8390000000000")
                            .documentName("GEFC220105 - Basics of Economics")
                            .author("Alice Johnson")
                            .quantity(20)
                            .availableCount(20)
                            .size(DocumentSize.LARGE)
                            .status(DocumentStatus.AVAILABLE)
                            .coverImage("cover_1.jpg")
                            .description("Description of GEFC220105 - Kinh tế học đại cương - Basics of Economics")
                            .documentLink(null) // "########" được chuyển thành null
                            .language("Japanese")
                            .pageCount(202)
                            .price(new BigDecimal("67.62"))
                            .publishedDate(null) // "########" được chuyển thành null
                            .publisher("Publisher D")
                            .locations(List.of(DocumentLocation.builder()
                                    .size(DocumentSize.LARGE)
                                    .warehouseId(warehouse.getWarehouseId())
                                    .rackId(null) // Không gán rack cụ thể lúc tạo
                                    .quantity(20).build()))
                            .build(),

                    // Document 2
                    Document.builder()
                            .isbn("8250000000000")
                            .documentName("IQMA220205 - Quality Management Introduction")
                            .author("Carol Davis")
                            .quantity(49)
                            .availableCount(49)
                            .size(DocumentSize.SMALL)
                            .status(DocumentStatus.AVAILABLE)
                            .coverImage("cover_2.jpg")
                            .description("Description of IQMA220205 - Nhập môn quản trị chất lượng - Quality Management Introduction")
                            .documentLink("http://example.com/book_2")
                            .language("English")
                            .pageCount(792)
                            .price(new BigDecimal("17.01"))
                            .publishedDate(LocalDate.parse("1/3/2021", formatter))
                            .publisher("Publisher C")
                            .locations(List.of(DocumentLocation.builder()
                                    .size(DocumentSize.SMALL)
                                    .warehouseId(warehouse.getWarehouseId())
                                    .rackId(null) // Không gán rack cụ thể lúc tạo
                                    .quantity(49).build()))
                            .build(),

                    // Document 3
                    Document.builder()
                            .isbn("4360000000000")
                            .documentName("INMA220305 - Management Basics")
                            .author("Bob Brown")
                            .quantity(32)
                            .availableCount(32)
                            .size(DocumentSize.LARGE)
                            .status(DocumentStatus.AVAILABLE)
                            .coverImage("cover_3.jpg")
                            .description("Description of INMA220305 - Nhập môn Quản trị học - Management Basics")
                            .documentLink(null)
                            .language("English")
                            .pageCount(554)
                            .price(new BigDecimal("74.44"))
                            .publishedDate(null)
                            .publisher("Publisher D")
                            .locations(List.of(DocumentLocation.builder()
                                    .size(DocumentSize.LARGE)
                                    .warehouseId(warehouse.getWarehouseId())
                                    .rackId(null) // Không gán rack cụ thể lúc tạo
                                    .quantity(32).build()))
                            .build(),

                    // Document 4
                    Document.builder()
                            .isbn("4280000000000")
                            .documentName("INLO220405 -  Introduction to Logic")
                            .author("Carol Davis")
                            .quantity(47)
                            .availableCount(47)
                            .size(DocumentSize.MEDIUM)
                            .status(DocumentStatus.AVAILABLE)
                            .coverImage("cover_4.jpg")
                            .description("Description of INLO220405 - Nhập môn Logic học - Introduction to Logic")
                            .documentLink("http://example.com/book_4")
                            .language("French")
                            .pageCount(788)
                            .price(new BigDecimal("56.91"))
                            .publishedDate(LocalDate.parse("9/5/2024", formatter))
                            .publisher("Publisher E")
                            .locations(List.of(DocumentLocation.builder()
                            .size(DocumentSize.MEDIUM)
                            .warehouseId(warehouse.getWarehouseId())
                            .rackId(null) // Không gán rack cụ thể lúc tạo
                            .quantity(47).build()))
                    .build()


            );

            // Liên kết Document với các Course dựa trên related_subjects
            // Do đã định nghĩa sẵn các Document với các Course tương ứng, ta cần thực hiện liên kết
            // Giả sử bạn muốn liên kết dựa trên các mã môn học đã đặt trong tên document

            // Ví dụ: Document 1 liên kết với "GEFC220105"
            // Document 2 liên kết với "IQMA220205", v.v.

            for (Document document : documents) {
                // Xác định các courseCodes từ documentName hoặc một cách nào đó
                // Trong ví dụ này, chúng ta sẽ tách mã môn học từ documentName

                String documentName = document.getDocumentName();
                // Giả sử format "COURSECODE - Description"
                String[] parts = documentName.split(" - ");
                if (parts.length > 0) {
                    String courseCode = parts[0].split(" - ")[0].trim();
                    Optional<Course> courseOpt = courseRepository.findByCourseCode(courseCode);
                    courseOpt.ifPresent(course -> {
                        document.getCourses().add(course);
                        course.getDocuments().add(document); // Đảm bảo quan hệ hai chiều
                    });
                }
            }

            // Lưu tất cả Documents vào cơ sở dữ liệu
            documentRepository.saveAll(documents);

            // Nếu có liên kết nhiều hơn một Course cho một Document, hãy đảm bảo thực hiện tương tự
            // Ví dụ: Document 17 liên kết với "GEFC220105, NSMS432280, INRE431084"

            // Tạo các liên kết bổ sung cho các Documents có nhiều related_subjects
            // Bạn có thể lặp lại quá trình này hoặc định nghĩa rõ ràng các liên kết

            List<String> courseCodesToAdd = Arrays.asList(
                    "GEFC220105", "IQMA220205", "INMA220305", "INLO220405",
                    "TOEN430979", "SOPM431679", "ADMP431879", "ADPL331379",
                    "DIFO432180", "NSMS432280", "WISE432380", "CLAD432480",
                    "ADDB331784", "DAWH430784", "INRE431084", "SEEN431579"
            );
            // Duyệt qua các tài liệu và thêm khóa học vào tài liệu phù hợp
            for (Document document : documentRepository.findAll()) {
                // Tách mã môn học từ tên tài liệu để kiểm tra xem có trong danh sách cần thêm hay không
                String documentName = document.getDocumentName();
                String[] parts = documentName.split(" - ");
                if (parts.length > 0) {
                    String courseCode = parts[0].trim();

                    // Nếu mã môn học nằm trong danh sách cần thêm khóa học
                    if (courseCodesToAdd.contains(courseCode)) {
                        // Tìm khóa học theo mã môn học
                        Optional<Course> courseOpt = courseRepository.findByCourseCode(courseCode);

                        courseOpt.ifPresent(course -> {
                            // Thêm khóa học vào tài liệu và tài liệu vào khóa học (quan hệ hai chiều)
                            document.getCourses().add(course);
                            course.getDocuments().add(document);
                        });
                    }
                }
            }

            // Lưu lại tất cả tài liệu đã cập nhật vào cơ sở dữ liệu
           // documentRepository.saveAll(documentRepository.findAll());

            // Tương tự, bạn có thể thêm các liên kết bổ sung cho các Documents khác nếu cần
        }

    }
}
