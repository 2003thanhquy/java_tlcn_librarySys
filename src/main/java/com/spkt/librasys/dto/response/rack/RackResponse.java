package com.spkt.librasys.dto.response.rack;

import lombok.Data;

@Data
public class RackResponse {

    private Long rackId;
    private String rackNumber;
    private double capacity;
    private String shelfName; // Tên của Shelf chứa Rack
}
