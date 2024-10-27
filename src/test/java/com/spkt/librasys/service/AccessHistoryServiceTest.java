package com.spkt.librasys.service;

import com.spkt.librasys.dto.response.accessHistory.AccessHistoryResponse;
import com.spkt.librasys.entity.AccessHistory;
import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.repository.AccessHistoryRepository;
import com.spkt.librasys.service.impl.AccessHistoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccessHistoryServiceTest {

    @Mock
    private AccessHistoryRepository accessHistoryRepository;

    @InjectMocks
    private AccessHistoryServiceImpl accessHistoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllAccessHistories() {
        Pageable pageable = PageRequest.of(0, 5);
        AccessHistory accessHistory = AccessHistory.builder()
                .accessId(1L)
                .accessTime(LocalDateTime.now())
                .activity(AccessHistory.Activity.READ_ONLINE)
                .user(new User())
                .document(new Document())
                .build();

        Page<AccessHistory> page = new PageImpl<>(Collections.singletonList(accessHistory));
        when(accessHistoryRepository.findAll(pageable)).thenReturn(page);

        Page<AccessHistoryResponse> result = accessHistoryService.getAllAccessHistories(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(accessHistoryRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testCreateAccessHistory() {
        AccessHistory accessHistory = AccessHistory.builder()
                .accessTime(LocalDateTime.now())
                .activity(AccessHistory.Activity.DOWNLOADED)
                .user(new User())
                .document(new Document())
                .build();

        when(accessHistoryRepository.save(any(AccessHistory.class))).thenReturn(accessHistory);

        accessHistoryService.recordAccess(new User(), new Document(),AccessHistory.Activity.DOWNLOADED );

        verify(accessHistoryRepository, times(1)).save(any(AccessHistory.class));
    }
}
