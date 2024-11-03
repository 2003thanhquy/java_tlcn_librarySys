package com.spkt.librasys.config;

import com.spkt.librasys.entity.*;
import com.spkt.librasys.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataInit implements CommandLineRunner {
    WarehouseRepository warehouseRepository;
    DocumentTypeRepository documentTypeRepository;
    RackRepository rackRepository;
    ShelfRepository shelfRepository;
    DisplayZoneRepository displayZoneRepository;
    LoanPolicyRepository loanPolicyRepository;

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra nếu chưa có Warehouse nào thì tiến hành tạo dữ liệu mẫu
        if (warehouseRepository.count() < 1) {
            Warehouse warehouse = Warehouse.builder()
                    .warehouseName("Main Warehouse")
                    .location("123 Main St, City Center")
                    .build();
            warehouseRepository.save(warehouse); // Lưu Warehouse vào cơ sở dữ liệu
            // Tạo dữ liệu mẫu cho DisplayZone trong Warehouse
            DisplayZone displayZone = DisplayZone.builder()
                    .zoneName("Zone A")
                    .build();

            displayZoneRepository.save(displayZone); // Lưu DisplayZone vào cơ sở dữ liệu

            // Tạo các Shelf trong DisplayZone
            Shelf shelf1 = Shelf.builder()
                    .shelfNumber("Shelf 1")
                    .zone(displayZone)
                    .build();

            Shelf shelf2 = Shelf.builder()
                    .shelfNumber("Shelf 2")
                    .zone(displayZone)
                    .build();

            shelfRepository.saveAll(List.of(shelf1, shelf2)); // Lưu các Shelf vào cơ sở dữ liệu

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

            rackRepository.saveAll(List.of(rack1, rack2, rack3)); // Lưu các Rack vào cơ sở dữ liệu
        }

        // Kiểm tra nếu chưa có DocumentType nào thì tiến hành tạo dữ liệu mẫu
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
            // Lưu danh sách DocumentType vào cơ sở dữ liệu


            LoanPolicy loanPolicy1 = LoanPolicy.builder()
                    .documentType(type1)
                    .maxLoanDays(15)
                    .maxRenewals(2)
                    .build();
            LoanPolicy loanPolicy2 = LoanPolicy.builder()
                    .documentType(type1)
                    .maxLoanDays(20)
                    .maxRenewals(2)
                    .build();
            LoanPolicy loanPolicy3 = LoanPolicy.builder()
                    .documentType(type1)
                    .maxLoanDays(25)
                    .maxRenewals(2)
                    .build();
            LoanPolicy loanPolicy4 = LoanPolicy.builder()
                    .documentType(type1)
                    .maxLoanDays(30)
                    .maxRenewals(2)
                    .build();
            loanPolicyRepository.saveAll(Arrays.asList(loanPolicy1, loanPolicy2, loanPolicy3, loanPolicy4));
        }
    }
}
