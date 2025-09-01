package com.scity.storage.interceptor;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class SessionHelper {
    private final RequestHeaders requestHeaders;

    public UUID getCurrentUserId() {
        try {
            if (requestHeaders != null && requestHeaders.getUserId() != null)
                return requestHeaders.getUserId();
            else
                return UUID.fromString("00000000-0000-0000-0000-000000000000");
        } catch (Exception ex) {
            return UUID.fromString("11111111-1111-1111-1111-111111111111");
        }
    }
    public void setCurrentUserID(UUID userID) {
        requestHeaders.setUserId(userID);
    }
    public String getTenantId() {
        try {
            if (requestHeaders != null && requestHeaders.getTenantId() != null)
                return requestHeaders.getTenantId();
            else
                return "DEFAULT";
        } catch (Exception ex) {
            return "DEFAULT";
        }
    }

    public List<String> getRoles() {
        try {
            if (requestHeaders != null && requestHeaders.getRoles() != null) {
                return requestHeaders.getRoles();
            } else {
                return new ArrayList<>();
            }
        } catch (Exception ex) {
            return List.of("SYSTEM");
        }
    }
}
