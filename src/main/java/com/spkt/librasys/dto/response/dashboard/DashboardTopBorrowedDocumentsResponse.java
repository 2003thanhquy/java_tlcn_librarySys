package com.spkt.librasys.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DashboardTopBorrowedDocumentsResponse {
    private List<String> topBorrowedDocuments; // Có thể là danh sách tên tài liệu hoặc đối tượng chứa thông tin tài liệu
}
