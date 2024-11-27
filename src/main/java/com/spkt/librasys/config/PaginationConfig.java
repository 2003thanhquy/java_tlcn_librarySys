package com.spkt.librasys.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaginationConfig {

    @Value("${pagination.max-size:100}")  // Giá trị mặc định là 100
    private int maxSize;

    public int getMaxSize() {
        return maxSize;
    }
}
