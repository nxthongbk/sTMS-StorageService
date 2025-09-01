package com.scity.storage.util;

import com.scity.storage.constant.SUPPORT_FILE_TYPE;
import com.scity.storage.exception.InvalidFileTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class FileUploadUtil {

  public String validateUploadingFile(MultipartFile file) {
    String fileType = file.getContentType();
    if (!SUPPORT_FILE_TYPE.isSupported(fileType)) {
      throw new InvalidFileTypeException("The file type is not supported.");
    }
    return fileType;
  }
}
