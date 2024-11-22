package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.response.dashboard.*;
import com.spkt.librasys.entity.Fine;
import com.spkt.librasys.repository.FineRepository;
import com.spkt.librasys.repository.LoanTransactionRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.repository.DocumentRepository;
import com.spkt.librasys.service.DashboardService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardServiceImpl implements DashboardService {

    DocumentRepository documentRepository;
    LoanTransactionRepository loanTransactionRepository;
    UserRepository userRepository;
    FineRepository fineRepository;

    @Override
    public DashboardDocumentCountResponse getDocumentCount() {
        long count = documentRepository.count();
        return new DashboardDocumentCountResponse(count);
    }

    @Override
    public DashboardLoanTransactionCountResponse getLoanTransactionsCount(int month, int year) {
        long count = loanTransactionRepository.countByMonthAndYear(month, year);
        return new DashboardLoanTransactionCountResponse(count);
    }

    @Override
    public Page<DashboardTopBorrowedDocumentsResponse> getTopBorrowedDocuments(PageRequest pageRequest) {
        return documentRepository.findTopBorrowedDocuments(pageRequest)
                .map(doc -> new DashboardTopBorrowedDocumentsResponse((String) doc[0], (Long) doc[1]));
    }

    @Override
    public Long getNewUsersCount(int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return userRepository.countNewUsersInCurrentMonth(start, end);
    }

    @Override
    public Long getUnpaidFinesCount() {
        return fineRepository.countByStatus(Fine.Status.UNPAID);
    }

    @Override
    public Long getUnreturnedDocumentsCount() {
        return loanTransactionRepository.countByReturnDateIsNull();
    }

    @Override
    public Long getMonthlyActiveUsersCount(int month, int year) {
        // Ngày đầu tháng với thời gian 00:00:00
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        // Ngày cuối tháng với thời gian 23:59:59
        LocalDateTime end = start.withDayOfMonth(start.toLocalDate().lengthOfMonth()).toLocalDate().atTime(0,0);

        return userRepository.countActiveUsersInCurrent(start, end);
    }

    @Override
    public Long getYearlyActiveUsersCount(int year) {
        // Ngày đầu năm với thời gian 00:00:00
        LocalDateTime start = LocalDateTime.of(year, 1, 1, 0, 0);
        // Ngày cuối năm với thời gian 23:59:59
        LocalDateTime end = LocalDateTime.of(year, 12, 31, 23, 59, 59);

        return userRepository.countActiveUsersInCurrent(start, end);
    }
    @Override
    public UserStatisticsResponse getUserStatistics(Integer month, Integer year) {
        // Tổng số người dùng
        Long totalUsers = userRepository.count();

        // Phân loại người dùng theo vai trò
        Map<String, Long> usersByRole = userRepository.findAll().stream()
                .collect(Collectors.groupingBy(user -> user.getRoles().iterator().next().getName(), Collectors.counting()));

        // Số người dùng hoạt động trong tháng/năm cụ thể
        Long activeUsers = null;
        if (month != null && year != null) {
            activeUsers = userRepository.findAll().stream()
                    .filter(user -> user.getLastLoginDate() != null &&
                            YearMonth.from(user.getLastLoginDate()).equals(YearMonth.of(year, month)))
                    .count();
        }

        // Trả về kết quả
        return UserStatisticsResponse.builder()
                .totalUsers(totalUsers)
                .usersByRole(usersByRole)
                .activeUsers(activeUsers)
                .build();
    }

    @Override
    public DocumentStatisticsResponse getDocumentStatistics(String year) {
        // Lấy tổng số sách
        long totalDocuments = documentRepository.countTotalDocuments();

        // Lấy số sách còn sẵn trong kho
        long availableDocuments = documentRepository.countAvailableBooks();

        // Lấy số sách bị hư hỏng từ LoanTransaction
        long damagedDocuments = loanTransactionRepository.countDamagedBooksFromTransactions();

        // Tính số sách đang mượn
        long borrowedDocs = totalDocuments - availableDocuments - damagedDocuments;

        // Chuyển đổi documentsByType
        List<Map<String, Object>> documentsByType = documentRepository.countDocumentsByType().stream()
                .map(obj -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("typeName", obj[0]);
                    map.put("count", obj[1]);
                    //map.put("percentage", ((long) obj[1] * 100.0) / totalBooks); // Tính phần trăm
                    return map;

                })
                .collect(Collectors.toList());

        // Chuyển đổi documentsByCourseCode
        String academicYear = year + "-" + (Integer.parseInt(year) + 1);
        List<Map<String, Object>> documentsByCourseCode = documentRepository.countDocumentsByCourseCode(academicYear).stream()
                .map(obj -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("year", obj[0]);
                    map.put("courseCode", obj[1]);
                    map.put("count", obj[2]);

                    return map;
                })
                .collect(Collectors.toList());

        return DocumentStatisticsResponse.builder()
                .totalDocuments(totalDocuments)
                .borrowedDocuments(borrowedDocs)
                .availableDocuments(availableDocuments)
                .disabledDocuments(damagedDocuments)
                .documentsByType(documentsByType)
                .documentsByCourseCode(documentsByCourseCode)
                .build();
    }
    @Override
    public LoanTransactionStatisticsResponse getLoanTransactionStatistics(int year) {
        // Thống kê lượt mượn và trả theo tháng
        List<Map<String, Object>> loansByMonth = loanTransactionRepository.countLoansByMonth(year).stream()
                .map(obj -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("month", obj[0]);
                    map.put("loanCount", obj[1]);
                    return map;
                })
                .collect(Collectors.toList());

        // Số người vi phạm
        long violatorsCount = loanTransactionRepository.countViolators();

        // Số lượng sách trả quá hạn theo tháng
        List<Map<String, Object>> overdueBooksByMonth = loanTransactionRepository.countOverdueBooksByYear(year).stream()
                .map(obj -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("month", obj[0]);
                    map.put("overdueCount", obj[1]);
                    return map;
                })
                .collect(Collectors.toList());

        // Số lượng sách bị hư hỏng theo tháng
        List<Map<String, Object>> damagedBooksByMonth = loanTransactionRepository.countDamagedBooksByYear(year).stream()
                .map(obj -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("month", obj[0]);
                    map.put("damagedCount", obj[1]);
                    return map;
                })
                .collect(Collectors.toList());

        // Bảng thống kê hoạt động mượn trả
        List<Map<String, Object>> loanActivities = loanTransactionRepository.getLoanTransactionActivities(LocalDateTime.of(year, 1, 1,0,0), LocalDateTime.of(year, 12, 31,23,59)).stream()
                .map(obj -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", obj[0]);
                    map.put("firstName", obj[1]);
                    map.put("lastName", obj[2]);
                    map.put("documentName", obj[3]);
                    map.put("loanDate", obj[4]);
                    map.put("returnDate", obj[5]);
                    map.put("status", obj[6]);
                    return map;
                })
                .collect(Collectors.toList());

        // Trả về đối tượng LoanTransactionStatisticsResponse
        return LoanTransactionStatisticsResponse.builder()
                .loansByMonth(loansByMonth)
                .violatorsCount(violatorsCount)
                .overdueBooksByMonth(overdueBooksByMonth)
                .damagedBooksByMonth(damagedBooksByMonth)
                .loanActivities(loanActivities)
                .build();
    }
    @Override
    public Map<String, Object> getLoanTransactionActivities(LocalDate startDate, LocalDate endDate) {
        // Chuyển đổi LocalDate sang LocalDateTime
        LocalDateTime startDateTime = startDate.atStartOfDay(); // 00:00:00
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59); // 23:59:59

        var results = loanTransactionRepository.getLoanTransactionActivities(startDateTime, endDateTime).stream()
                .map(obj -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", obj[0]);
                    map.put("firstName", obj[1]);
                    map.put("lastName", obj[2]);
                    map.put("documentName", obj[3]);
                    map.put("loanDate", obj[4]);
                    map.put("returnDate", obj[5]);
                    map.put("status", obj[6]);
                    return map;
                })
                .collect(Collectors.toList());
        // Tính tổng số lượng cho từng trạng thái
        Map<String, Long> statusSummary = results.stream()
                .collect(Collectors.groupingBy(
                        item -> item.get("status").toString(), // Chuyển enum sang String
                        Collectors.counting()
                ));

        // Trả về kết quả kèm tổng số trạng thái
        Map<String, Object> summary = new HashMap<>();
        summary.put("statusSummary", statusSummary); // Tổng kết trạng thái
        summary.put("activities", results); // Danh sách hoạt động

        return summary;
    }
}