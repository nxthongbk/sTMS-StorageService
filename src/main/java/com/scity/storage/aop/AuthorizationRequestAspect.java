package com.scity.storage.aop;

import com.scity.storage.aop.annotation.AuthorizeRequest;
import com.scity.storage.exception.AuthenticationException;
import com.scity.storage.exception.ForbiddenException;
import com.scity.storage.interceptor.SessionHelper;
import com.scity.storage.model.constant.ERole;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Aspect
@Component
public class AuthorizationRequestAspect {
    @Autowired
    SessionHelper sessionHelper;

    @Around("@annotation(authorizationRequest)")
    public Object checkAuthorizePermission(ProceedingJoinPoint joinPoint, AuthorizeRequest authorizationRequest) throws Throwable {

        if (sessionHelper.getCurrentUserId() != null && !sessionHelper.getCurrentUserId().toString().equals("00000000-0000-0000-0000-000000000000")) {
            String[] roles = authorizationRequest.roles();
            List<String> userRoles = sessionHelper.getRoles();
            if (roles.length == 0 || userRoles.contains(ERole.SYSADMIN.name())) {
                return joinPoint.proceed();
            }
            for (String role : roles) {
                if (userRoles.contains(role))
                    return joinPoint.proceed();
            }
            throw new ForbiddenException("No permission");
        }

        throw new AuthenticationException("Authenticate is requested!");
    }
}