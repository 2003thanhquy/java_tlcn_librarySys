package com.spkt.librasys.config;

import com.github.javafaker.Faker;
import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.DocumentType;
import com.spkt.librasys.repository.document.DocumentRepository;
import com.spkt.librasys.repository.document.DocumentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DocumentRepository documentRepository;
    private final DocumentTypeRepository documentTypeRepository;

    @Override
    public void run(String... args) {
        if (documentRepository.count() == 0) { // Kiểm tra nếu chưa có dữ liệu trong bảng Document thì mới thêm dữ liệu giả
            Faker faker = new Faker();
            Random random = new Random();

            // Tạo một số loại tài liệu giả
            DocumentType documentType = DocumentType.builder()
                    .typeName("General Knowledge")
                    .build();
            documentTypeRepository.save(documentType);

            for (int i = 0; i < 100; i++) {
                Document document = Document.builder()
                        .documentName(faker.book().title())
                        .author(faker.book().author())
                        .publisher(faker.book().publisher())
                        .publishedDate(LocalDate.now().minusDays(random.nextInt(1000)))
                        .pageCount(random.nextInt(200) + 100)
                        .quantity(random.nextInt(10) + 1)
                        .description(faker.lorem().sentence())
                        .documentLink("https://example.com/" + faker.lorem().word())
                        .documentType(documentType) // Liên kết với loại tài liệu đã tạo
                        .build();

                documentRepository.save(document);
            }

            System.out.println("100 documents have been inserted into the database.");
        } else {
            System.out.println("Documents already exist in the database. Skipping data initialization.");
        }
    }
}
