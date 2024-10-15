package com.spkt.librasys.entity.enums;


public enum DocumentActivityStatus {
    READ("READ"),
    DOWNLOAD("DOWNLOAD"),
    EDIT("EDIT"),
    DELETE("DELETE"),
    SHARE("SHARE"),
    SEARCH("SEARCH");

    private final String value;

    DocumentActivityStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}

