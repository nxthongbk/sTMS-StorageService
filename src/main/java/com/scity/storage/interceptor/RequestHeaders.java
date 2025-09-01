package com.scity.storage.interceptor;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RequestHeaders {
    private UUID userId;
    private String tenantId;
    private List<String> roles;
}
