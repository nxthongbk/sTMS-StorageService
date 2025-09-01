package com.scity.storage.service;

import com.scity.storage.exception.ForbiddenException;
import com.scity.storage.exception.MaxUploadSizeExceedException;
import com.scity.storage.model.dto.BatchDeleteDTO;
import com.scity.storage.model.dto.FileBasicDTO;
import com.scity.storage.model.dto.FileDTO;
import com.scity.storage.model.dto.FileUpdateDTO;
import com.scity.storage.model.dto.StreamingMediaDTO;
import com.scity.storage.model.dto.ZipFileResponseDTO;
import com.scity.storage.model.entity.File;
import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {

  FileDTO uploadFile(MultipartFile multipartFile, String description)
      throws IOException, MaxUploadSizeExceedException;

  List<FileDTO> uploadFiles(MultipartFile[] multipartFile, String description)
      throws IOException, MaxUploadSizeExceedException;

    FileDTO uploadFileSystem(MultipartFile multipartFile) throws IOException;

  void deleteFileSystem(String fileId) throws IOException;

  StreamingMediaDTO loadSystemFile(String fileId, String rangeHeader) throws IOException;

  File getFile(String fileId);

  File updateFile(String fileId, FileUpdateDTO fileUpdateDTO) throws ForbiddenException;

  void deleteFile(String fileId) throws IOException, ForbiddenException;

  void deleteFiles(BatchDeleteDTO fileIds) throws IOException;

  StreamingMediaDTO downloadFile(String filename, String rangeHeader) throws IOException;

  FileBasicDTO[] validateFile(FileBasicDTO[] fileBasicDTOList);

  StreamingMediaDTO getThumbnailImage(String fileName) throws IOException;

  void deleteFileSystem(String[] ids) throws IOException;

  ZipFileResponseDTO downloadFilesAsZip(List<String> fileIds, String zipName) throws IOException;

  ResponseEntity<byte[]> streamVideoV2(String fileName, String rangeHeader);

  StreamingMediaDTO getImageFile(String fileName, String rangeHeader) throws IOException;
}
