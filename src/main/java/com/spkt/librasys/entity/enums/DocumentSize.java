package com.spkt.librasys.entity.enums;

public enum DocumentSize {
    SMALL(100),    // 1000 cm³
    MEDIUM(200),   // 2000 cm³
    LARGE(300);    // 3000 cm³

    private final double sizeValue;

    DocumentSize(double sizeValue) {
        this.sizeValue = sizeValue;
    }

    public double getSizeValue() {
        return sizeValue;
    }
}
