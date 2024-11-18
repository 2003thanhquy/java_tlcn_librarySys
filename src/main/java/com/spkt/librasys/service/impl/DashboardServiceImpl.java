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
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return userRepository.countActiveUsersInCurrentMonth(start, end);
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
        long totalDocuments = documentRepository.countTotalDocuments();
        long borrowedDocuments = documentRepository.countBorrowedDocuments();
        long availableDocuments = documentRepository.countAvailableDocuments();
        long disabledDocuments = documentRepository.countDisabledDocuments();

        // Chuyển đổi documentsByType
        List<Map<String, Object>> documentsByType = documentRepository.countDocumentsByType().stream()
                .map(obj -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("typeName", obj[0]);
                    map.put("count", obj[1]);
                    return map;
                })
                .collect(Collectors.toList());

        // Chuyển đổi documentsByCourseCode
        List<Map<String, Object>> documentsByCourseCode = documentRepository.countDocumentsByCourseCode(year).stream()
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
                .borrowedDocuments(borrowedDocuments)
                .availableDocuments(availableDocuments)
                .disabledDocuments(disabledDocuments)
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
        List<Map<String, Object>> loanActivities = loanTransactionRepository.getLoanTransactionActivities(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31)).stream()
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
    public List<Map<String, Object>> getLoanTransactionActivities(LocalDate startDate, LocalDate endDate) {
        return loanTransactionRepository.getLoanTransactionActivities(startDate, endDate).stream()
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
    }
}