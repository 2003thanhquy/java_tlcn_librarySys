package com.spkt.librasys.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DashboardTopBorrowedDocumentsResponse {
    private String documentName;
    private long borrowCount;
}