package com.scity.storage.config;

import org.springframework.http.CacheControl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

import java.util.concurrent.TimeUnit;

@Component
public class StaticResourceConfiguration implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebContentInterceptor interceptor = new WebContentInterceptor();
        interceptor.addCacheMapping(CacheControl.maxAge(7, TimeUnit.DAYS).noTransform().mustRevalidate(),
                "*/storage/files/web/**");
        interceptor.addCacheMapping(CacheControl.maxAge(7, TimeUnit.DAYS).noTransform().mustRevalidate(),
                "*/storage/files/thumbnail/**");
        interceptor.addCacheMapping(CacheControl.maxAge(7, TimeUnit.DAYS).noTransform().mustRevalidate(),
                "*/storage/files/mobile/**");
        registry.addInterceptor(interceptor);
    }
}