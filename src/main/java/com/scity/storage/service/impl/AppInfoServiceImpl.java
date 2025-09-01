package com.scity.storage.service.impl;

import com.scity.storage.model.dto.*;
import com.scity.storage.model.entity.Version;
import com.scity.storage.repository.VersionRepository;
import com.scity.storage.service.IAppInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AppInfoServiceImpl implements IAppInfoService {
    @Autowired
    VersionRepository versionRepository;

    @Override
    public AppInfoDetailDTO getInfo() {
        List<ElementDTO> data = new ArrayList<>();
        Version latestVersion = versionRepository.findLatestUpdated();
        ElementDTO versionDTO = new ElementDTO("APP_VERSION", latestVersion.getVersion(), latestVersion.getUpdatedAt());
        ElementDTO signupDTO = new ElementDTO("SIGNUP_AVAILABLE", latestVersion.getIsSignUp().toString(), latestVersion.getSignUpUpdatedAt());
        data.add(versionDTO);
        data.add(signupDTO);
        AppInfoDetailDTO appInfoDetailDTO = new AppInfoDetailDTO(data);
        return appInfoDetailDTO;
    }

    @Override
    public void setVersion(AppInfoDTO appInfoDTO) {
        Date currentTime = new Date();
        Version newVer = new Version(appInfoDTO.getVersion(), currentTime, appInfoDTO.getIsSignUp(), currentTime);
        versionRepository.save(newVer);
    }
}
