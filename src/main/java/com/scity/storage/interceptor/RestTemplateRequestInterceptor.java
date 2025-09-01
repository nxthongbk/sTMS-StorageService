package com.scity.storage.interceptor;

import com.scity.storage.model.constant.EDefaultValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class RestTemplateRequestInterceptor implements ClientHttpRequestInterceptor  {
    private final UUID userId;

    public RestTemplateRequestInterceptor(UUID _userId) {
        userId = _userId;
    }

    public RestTemplateRequestInterceptor() {
        userId = null;
    }
    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        try{
            String currentUserId = "";
            try{
                currentUserId =
                        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                                .getRequest()
                                .getHeader("userId");
            }catch (Exception e){
                currentUserId = userId.toString();
            }

            String tenantId = null;
            try{
                tenantId = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest()
                        .getHeader("X-Tenant-ID");
            } catch (Exception e){
                tenantId = EDefaultValue.COMPANY_CODE.getValue();
            }

            String roleStr = "";
            try {
                roleStr = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest()
                        .getHeader("roles");
            } catch (Exception e) {
            }

            if (currentUserId != null && !currentUserId.isEmpty()) {
                request.getHeaders().set("userId", currentUserId);
            }
            if (tenantId != null && !tenantId.isEmpty()) {
                request.getHeaders().set("X-Tenant-ID", tenantId);
            }
            if (roleStr != null && !roleStr.isEmpty()) {
                request.getHeaders().set("roles", roleStr);
            }
        } catch (Exception ex){
            request.getHeaders().set("userId", "11111111-1111-1111-1111-111111111111");
            request.getHeaders().set("X-Tenant-ID", EDefaultValue.COMPANY_CODE.getValue());
            request.getHeaders().set("roles", "");
        }

        return execution.execute(request, body);
    }
}
