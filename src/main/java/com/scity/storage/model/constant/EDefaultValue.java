package com.scity.storage.model.constant;

public enum EDefaultValue {
    COMPANY_CODE("DEFAULT");

    private String value;
    EDefaultValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
