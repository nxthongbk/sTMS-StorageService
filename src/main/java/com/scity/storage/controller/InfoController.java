package com.scity.storage.controller;

import com.scity.storage.aop.annotation.AuthorizeRequest;
import com.scity.storage.model.dto.AppInfoDTO;
import com.scity.storage.model.dto.AppInfoDetailDTO;
import com.scity.storage.model.dto.ResModel;
import com.scity.storage.service.IAppInfoService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/info")
public class InfoController {
    @Autowired
    IAppInfoService appInfoService;
    @Operation(description = "Information about the application", summary = "Information about the application")
    @GetMapping()
    public AppInfoDetailDTO appInfo() {
        return appInfoService.getInfo();
    }

    @Operation(description = "Set the application version", summary = "Set the application version")
    @PostMapping()
    @AuthorizeRequest(roles = {"TENANT"})
    public ResModel<String> version(
            @RequestBody AppInfoDTO appInfoDTO) {
        appInfoService.setVersion(appInfoDTO);
        return ResModel.ok("Successfully!");
    }
}
