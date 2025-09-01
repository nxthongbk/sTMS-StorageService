package com.scity.storage.service;

import com.scity.storage.model.dto.AppInfoDTO;
import com.scity.storage.model.dto.AppInfoDetailDTO;

public interface IAppInfoService {
    AppInfoDetailDTO getInfo();
    void setVersion(AppInfoDTO appInfoDTO);
}
