package com.spkt.librasys.service;

import com.spkt.librasys.entity.User;

public interface SecurityContextService {
    User getCurrentUser();
}
