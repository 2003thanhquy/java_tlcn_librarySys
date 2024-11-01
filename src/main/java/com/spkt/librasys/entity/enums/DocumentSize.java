package com.spkt.librasys.entity.enums;

public enum DocumentSize {
    SMALL(1000),    // 1000 cm³
    MEDIUM(2000),   // 2000 cm³
    LARGE(3000);    // 3000 cm³

    private final double sizeValue;

    DocumentSize(double sizeValue) {
        this.sizeValue = sizeValue;
    }

    public double getSizeValue() {
        return sizeValue;
    }
}
