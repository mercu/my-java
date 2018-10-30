package com.mercu.bricklink.model;

public enum CategoryType {
    S("S"),
    P("P"),
    M("M");

    private String code;

    CategoryType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
