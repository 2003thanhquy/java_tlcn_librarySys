package com.spkt.librasys.config;

import com.github.javafaker.Faker;
import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.DocumentType;
import com.spkt.librasys.entity.Role;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.repository.access.RoleRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.repository.document.DocumentRepository;
import com.spkt.librasys.repository.document.DocumentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DocumentRepository documentRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        Faker faker = new Faker();
        Random random = new Random();

        // Kiểm tra và tạo dữ liệu giả cho Document nếu chưa có dữ liệu
        if (documentRepository.count() == 0) {
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
                        .documentType(documentType)
                        .build();

                documentRepository.save(document);
            }

            System.out.println("100 documents have been inserted into the database.");
        } else {
            System.out.println("Documents already exist in the database. Skipping data initialization.");
        }

        // Kiểm tra và tạo dữ liệu giả cho User nếu chưa có dữ liệu
        if (userRepository.count() <2) {
            // Tạo các role giả nếu chưa tồn tại
            Role adminRole = roleRepository.findById("ADMIN").orElseGet(() -> {
                Role role = Role.builder().name("ADMIN").build();
                return roleRepository.save(role);
            });
            Role userRole = roleRepository.findById("USER").orElseGet(() -> {
                Role role = Role.builder().name("USER").build();
                return roleRepository.save(role);
            });

            for (int i = 0; i < 100; i++) {
                User user = User.builder()
                        .username(faker.internet().emailAddress())
                        .password(faker.internet().password())
                        .firstName(faker.name().firstName())
                        .lastName(faker.name().lastName())
                        .dob(LocalDate.now().minusYears(random.nextInt(60) + 18)) // Giả lập tuổi từ 18 đến 78
                        .phoneNumber(faker.phoneNumber().cellPhone())
                        .address(faker.address().fullAddress())
                        .registrationDate(LocalDate.now().minusDays(random.nextInt(365))) // Đăng ký từ 1 năm trước
                        .expirationDate(LocalDate.now().plusYears(1)) // Hạn sử dụng là 1 năm sau
                        .roles(new HashSet<>(Set.of(userRole))) // Gán role cho user
                        .build();

                userRepository.save(user);
            }

            System.out.println("10 users have been inserted into the database.");
        } else {
            System.out.println("Users already exist in the database. Skipping user data initialization.");
        }
    }
}
