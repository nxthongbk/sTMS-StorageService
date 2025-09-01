package com.scity.storage.service.impl;

import com.scity.storage.constant.SUPPORT_FILE_TYPE;
import com.scity.storage.exception.BadRequestException;
import com.scity.storage.exception.ForbiddenException;
import com.scity.storage.exception.MaxUploadSizeExceedException;
import com.scity.storage.interceptor.SessionHelper;
import com.scity.storage.model.dto.*;
import com.scity.storage.model.entity.File;
import com.scity.storage.repository.FileRepository;
import com.scity.storage.service.IFileService;
import com.scity.storage.util.FileUploadUtil;
import com.scity.storage.util.MD5Util;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.webjars.NotFoundException;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class FileServiceImpl implements IFileService {

  private static final String FILE_NOT_FOUND = "File not found.";
  private static final String UPLOAD_PATH = "./home/media/";
  private final ResourcePatternResolver resourcePatternResolver;
  @Autowired
  private SessionHelper sessionHelper;
  @Autowired
  private FileRepository fileRepository;
  @Autowired
  private FileUploadUtil fileUploadUtil;
  @Autowired
  private MD5Util md5Util;

  @Autowired
  public FileServiceImpl(ResourcePatternResolver resourcePatternResolver) {
    this.resourcePatternResolver = resourcePatternResolver;
    this.setDefaultImage();
  }

  public void setDefaultImage() {
    String resourcesFolder = "classpath*:/default-images/*";
    String resourcesFolderImageTemplate = "classpath*:/default-images-template/*";
    try {
      // Sử dụng ResourcePatternResolver để lấy danh sách tất cả các tệp trong thư mục resources
      Resource[] resources = resourcePatternResolver.getResources(resourcesFolder);
      Resource[] resourcesImageTemplate = resourcePatternResolver.getResources(
          resourcesFolderImageTemplate);

      List<Resource> combinedResources = new ArrayList<>();
      combinedResources.addAll(Arrays.asList(resources));
      combinedResources.addAll(Arrays.asList(resourcesImageTemplate));

      Resource[] resulResources = combinedResources.toArray(new Resource[0]);

      for (Resource resource : resulResources) {
        try {
          String fullPathThumbnail =
              getThumbnailUploadPath() + Objects.requireNonNull(resource.getFilename());
          {
            java.io.File directory = new java.io.File(new java.io.File(fullPathThumbnail).getParent());
            if (!directory.exists()) {
              Files.createDirectories(Paths.get(directory.getPath()));
            }

            InputStream inputStream = resource.getInputStream();
            java.nio.file.Files.copy(inputStream, Paths.get(fullPathThumbnail),
                StandardCopyOption.REPLACE_EXISTING);
          }

        } catch (Exception ex) {
          log.error("COPY DEFAULT FAIL EXCEPTION 1", ex);
        }
      }
    } catch (Exception ex) {
      log.error("COPY DEFAULT FAIL EXCEPTION 2", ex);
    }
  }

  private String getThumbnailUploadPath() {
    return UPLOAD_PATH + "/" + "/thumbnail/";
  }

  private String getImageUploadPath(String tenant) {
    return UPLOAD_PATH + tenant + "/" + "image/";
  }

  private String getSystemUploadPath() {
    return UPLOAD_PATH + "system/";
  }

  private String getVideoUploadPath(String tenant) {
    return UPLOAD_PATH + tenant + "/" + "/video/";
  }

  private String getOtherUploadPath(String tenant) {
    return UPLOAD_PATH + tenant + "/" + "/other/";
  }

  @Override
  public FileDTO uploadFile(MultipartFile multipartFile, String description)
      throws IOException {
    String fileType = fileUploadUtil.validateUploadingFile(multipartFile);

    File fileEntity = new File();
    String originalFileName =
        StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

    UUID createdBy = sessionHelper.getCurrentUserId();
    fileEntity.setDescription(description);

    fileEntity.setType(fileType);
    Path uploadPath;
    if (SUPPORT_FILE_TYPE.Image.MIME_TYPES.contains(fileType)) {
      uploadPath = Paths.get(getImageUploadPath(sessionHelper.getTenantId()));
    } else if (SUPPORT_FILE_TYPE.Video.MIME_TYPES.contains(fileType)) {
      uploadPath = Paths.get(getVideoUploadPath(sessionHelper.getTenantId()));
    } else {
      uploadPath = Paths.get(getOtherUploadPath(sessionHelper.getTenantId()));
    }

    if (!Files.exists(uploadPath)) {
      Files.createDirectories(uploadPath);
    }

    String fileBaseName = UUID.randomUUID().toString();
    String extension = "." + FilenameUtils.getExtension(originalFileName);
    String fileName = fileBaseName + "_" + sessionHelper.getTenantId() + extension;
    fileEntity.setId(fileName);
    fileEntity.setExtension(extension);
    fileEntity.setOriginalName(originalFileName);

    try (InputStream inputStream = multipartFile.getInputStream()) {
      Path filePath = uploadPath.resolve(fileName);
      Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException ioe) {
      throw new IOException("Could not save file: " + originalFileName, ioe);
    }
    long size = multipartFile.getSize();
    fileEntity.setSize(size);
    fileEntity.setCreatedBy(createdBy.toString());
    fileEntity.setCreatedAt(new Date());
    fileEntity.setUpdatedBy(createdBy.toString());
    fileEntity.setUpdatedAt(new Date());
    fileRepository.save(fileEntity);

    FileDTO fileDTO = new FileDTO();
    fileDTO.setId(fileEntity.getId());
    fileDTO.setType(fileEntity.getType());
    fileDTO.setSize(fileEntity.getSize());
    fileDTO.setExtension(fileEntity.getExtension());
    fileDTO.setOriginal_name(fileEntity.getOriginalName());
    fileDTO.setDescription(fileEntity.getDescription());
    fileDTO.setSignature(md5Util.encode(fileEntity.getId()));
    return fileDTO;
  }

  @Override
  public List<FileDTO> uploadFiles(MultipartFile[] multipartFiles, String description)
      throws IOException, MaxUploadSizeExceedException {
    List<FileDTO> result = new ArrayList<>();
    for (MultipartFile multipartFile : multipartFiles) {
      result.add(uploadFile(multipartFile, description));
    }
    return result;
  }

  @Override
  public FileDTO uploadFileSystem(MultipartFile multipartFile) throws IOException {
    String fileType = fileUploadUtil.validateUploadingFile(multipartFile);

    String originalFileName =
            StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
    Path uploadPath = Paths.get(getSystemUploadPath());

    if (!Files.exists(uploadPath)) {
      Files.createDirectories(uploadPath);
    }

    try (InputStream inputStream = multipartFile.getInputStream()) {
      Path filePath = uploadPath.resolve(originalFileName);
      Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException ioe) {
      throw new IOException("Không thể lưu file: " + originalFileName, ioe);
    }
    FileDTO fileDTO = new FileDTO();
    fileDTO.setId(originalFileName);
    fileDTO.setType(fileType);
    fileDTO.setSize(multipartFile.getSize());
    fileDTO.setExtension("." + FilenameUtils.getExtension(originalFileName));
    fileDTO.setOriginal_name(originalFileName);
    fileDTO.setDescription("");
    fileDTO.setSignature(null);
    return fileDTO;
  }

  @Override
  public void deleteFileSystem(String fileId) throws IOException {
    Files.deleteIfExists(
            Paths.get(getSystemUploadPath() + fileId));
  }

  @Override
  public StreamingMediaDTO loadSystemFile(String fileId, String rangeHeader) throws IOException {
    String fullPath;
    fullPath = getSystemUploadPath() + fileId;

    java.io.File file = new java.io.File(fullPath);
    if (!file.exists()) {
      throw new EntityNotFoundException(FILE_NOT_FOUND);
    }

    FileInputStream mediaFileStream = new FileInputStream(file);
    long totalSize = mediaFileStream.available();
    long rangeStart = 0;
    long rangeEnd = totalSize - 1;
    String mimeType = Files.probeContentType(file.toPath());
    StreamingResponseBody stream = readDataFromSSD(fullPath);

    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set(HttpHeaders.CONTENT_TYPE, mimeType);
    responseHeaders.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));
    if (SUPPORT_FILE_TYPE.Video.MIME_TYPES.contains(mimeType)) {
      if (rangeHeader != null) {
        String[] ranges = rangeHeader.split("-");
        rangeStart = Long.parseLong(ranges[0].substring(6));
        if (ranges.length > 1) {
          rangeEnd = Long.parseLong(ranges[1]);
        }
      }
      String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
      responseHeaders.set(HttpHeaders.CONTENT_LENGTH, contentLength);
      responseHeaders.set(
              HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + totalSize);
      responseHeaders.set(HttpHeaders.ACCEPT_RANGES, "bytes");
    }
    mediaFileStream.close();
    return StreamingMediaDTO.builder()
            .headers(responseHeaders)
            .mediaType(mimeType)
            .responseBody(stream)
            .build();
  }

  @Override
  public File getFile(String fileId) {
    return fileRepository
        .findById(fileId)
        .orElseThrow(() -> new EntityNotFoundException(FILE_NOT_FOUND));
  }

  @Override
  public File updateFile(String fileId, FileUpdateDTO fileUpdateDTO) throws ForbiddenException {
    UUID ownerId = sessionHelper.getCurrentUserId();
    File updateFile =
        fileRepository
            .findById(fileId)
            .orElseThrow(() -> new EntityNotFoundException(FILE_NOT_FOUND));
    if (!ownerId.toString().equals(updateFile.getCreatedBy())) {
      throw new ForbiddenException("You don’t have permission to update");
    }
    updateFile.setDescription(fileUpdateDTO.getDescription());
    return fileRepository.save(updateFile);
  }

  @Override
  public void deleteFile(String fileId) throws IOException, ForbiddenException {
    UUID ownerId = sessionHelper.getCurrentUserId();
    File file =
        fileRepository
            .findById(fileId)
            .orElseThrow(() -> new EntityNotFoundException(FILE_NOT_FOUND));
    if (!ownerId.toString().equals(file.getCreatedBy())) {
      throw new ForbiddenException("Forbidden!");
    }
    deleteFile(file);
  }

  private void deleteFile(File file) throws IOException {
    if (!Files.deleteIfExists(
            Paths.get(getOtherUploadPath(sessionHelper.getTenantId()) + file.getId()))) {
      log.debug(
              "Delete failed web file: {} ",
              getOtherUploadPath(sessionHelper.getTenantId()) + file.getId());
    }
    if (!Files.deleteIfExists(
            Paths.get(getVideoUploadPath(sessionHelper.getTenantId()) + file.getId()))) {
      log.debug(
              "Delete failed mobile file: {} ",
              getVideoUploadPath(sessionHelper.getTenantId()) + file.getId());
    }
    if (!Files.deleteIfExists(
            Paths.get(getImageUploadPath(sessionHelper.getTenantId()) + file.getId()))) {
      log.debug(
              "Delete failed thumbnail file: {} ",
              getImageUploadPath(sessionHelper.getTenantId()) + file.getId());
    }

    fileRepository.delete(file); // Delete file in File entity
  }

  @Override
  public void deleteFiles(BatchDeleteDTO batchDelete) {
    String[] deletedFiles = batchDelete.getFileIds();
    for (String id : deletedFiles) {
      try {
        deleteFile(id);
      } catch (IOException | EntityNotFoundException ignored) {
      } catch (ForbiddenException e) {
          throw new RuntimeException(e);
      }
    }
  }

  @Override
  public void deleteFileSystem(String[] ids) throws IOException {
    for (String fileId : ids) {
      File file =
          fileRepository
              .findById(fileId)
              .orElseThrow(() -> new EntityNotFoundException(FILE_NOT_FOUND));
      deleteFile(file);
    }
  }

  private String getUserPathFromFilename(String filename) {
    try {
      return filename.split("_")[1];
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new EntityNotFoundException(FILE_NOT_FOUND);
    }
  }

  private StreamingResponseBody readDataFromSSD(String fullPath) {
    return outputStream -> {
      try (FileChannel fileChannel = new FileInputStream(fullPath).getChannel()) {
        fileChannel.transferTo(0, fileChannel.size(), Channels.newChannel(outputStream));
      } catch (IOException e) {
        throw new BadRequestException("Không thể đọc file");
      }
    };
  }

  @Override
  public StreamingMediaDTO getImageFile(String fileName, String rangeHeader) throws IOException {
    String fullPath = getImageUploadPath(sessionHelper.getTenantId()) + fileName;
    log.info("FULL PATH: " + fullPath);
    java.io.File file = new java.io.File(fullPath);
    if (!file.exists()) {
      fullPath = fullPathAdmin(fileName);
      file = new java.io.File(fullPath);
      if (!file.exists()) {
          fullPath = getImageUploadPath(fileName.split("_")[1]) + fileName;
          file = new java.io.File(fullPath);
        if (!file.exists())
          throw new NotFoundException("Not Found");
      }
    }
    StreamingResponseBody stream = readDataFromSSD(fullPath);

    HttpHeaders responseHeaders = new HttpHeaders();
    String mimeType = Files.probeContentType(file.toPath());
    responseHeaders.set(HttpHeaders.CONTENT_TYPE, mimeType);
    responseHeaders.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));
    return StreamingMediaDTO.builder()
            .headers(responseHeaders)
            .mediaType(mimeType)
            .responseBody(stream)
            .build();
  }

  private String fullPathAdmin(String fileName) {
    String[] namesElement = getFileNameWithoutExtension(fileName).split("_");
    if (namesElement.length > 1)
      return getImageUploadPath(namesElement[1]) + fileName;
    else {
      throw new NotFoundException("Not Found");
    }
  }

  public static String getFileNameWithoutExtension(String fileName) {
    String baseName = Paths.get(fileName).getFileName().toString();

    int lastDotIndex = baseName.lastIndexOf('.');

    if (lastDotIndex == -1) {
      return baseName;
    }

    return baseName.substring(0, lastDotIndex);
  }

  @Override
  public StreamingMediaDTO downloadFile(String fileName, String rangeHeader) throws IOException {
    String fullPath;
    fullPath = getOtherUploadPath(sessionHelper.getTenantId()) + fileName;

    java.io.File file = new java.io.File(fullPath);
    if (!file.exists()) {

      throw new EntityNotFoundException(FILE_NOT_FOUND);
    }

    FileInputStream mediaFileStream = new FileInputStream(file);
    long totalSize = mediaFileStream.available();
    long rangeStart = 0;
    long rangeEnd = totalSize - 1;
    String mimeType = Files.probeContentType(file.toPath());
    StreamingResponseBody stream = readDataFromSSD(fullPath);

    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set(HttpHeaders.CONTENT_TYPE, mimeType);
    responseHeaders.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));
    if (SUPPORT_FILE_TYPE.Video.MIME_TYPES.contains(mimeType)) {
      if (rangeHeader != null) {
        String[] ranges = rangeHeader.split("-");
        rangeStart = Long.parseLong(ranges[0].substring(6));
        if (ranges.length > 1) {
          rangeEnd = Long.parseLong(ranges[1]);
        }
      }
      String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
      responseHeaders.set(HttpHeaders.CONTENT_LENGTH, contentLength);
      responseHeaders.set(
          HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + totalSize);
      responseHeaders.set(HttpHeaders.ACCEPT_RANGES, "bytes");
    }
    return StreamingMediaDTO.builder()
        .headers(responseHeaders)
        .mediaType(mimeType)
        .responseBody(stream)
        .build();
  }

  @Override
  public StreamingMediaDTO getThumbnailImage(String fileName) throws IOException {
    String fullPath = getThumbnailUploadPath() + fileName;
    java.io.File file = new java.io.File(fullPath);
    if (!file.exists()) {
      throw new EntityNotFoundException(FILE_NOT_FOUND);
    }

    StreamingResponseBody stream = readDataFromSSD(fullPath);
    String mimeType = Files.probeContentType(file.toPath());
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set(HttpHeaders.CONTENT_TYPE, mimeType);
    responseHeaders.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));
    return StreamingMediaDTO.builder()
        .headers(responseHeaders)
        .mediaType(mimeType)
        .responseBody(stream)
        .build();
  }

  @Override
  public FileBasicDTO[] validateFile(FileBasicDTO[] fileBasicDTOList) {
    for (FileBasicDTO fileBasic : fileBasicDTOList) {
      String signature = md5Util.encode(fileBasic.getId());
      fileBasic.setExisted(signature.equals(fileBasic.getSignature()));
    }
    return fileBasicDTOList;
  }

  @Override
  public ZipFileResponseDTO downloadFilesAsZip(List<String> fileIds, String zipName)
      throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try (ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {
      for (String id : fileIds) {
        String filePath = getFileUriWithPrefix(id);
        if (filePath == null) {
          continue;
        }
        FileInputStream fis = new FileInputStream(filePath);
        String fileExtension = FilenameUtils.getExtension(filePath);
        String zipEntryName =
            org.apache.commons.lang3.StringUtils.isEmpty(fileExtension)
                ? id
                : id + "." + fileExtension;
        ZipEntry zipEntry = new ZipEntry(zipEntryName);
        zipOut.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
          zipOut.write(bytes, 0, length);
        }
        fis.close();
      }
    }
    String zipFile =
        (zipName == null || zipName.trim().isEmpty())
            ? LocalDateTime.now().toString().replaceAll("[^A-Za-z\\d]", "")
            : zipName;
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set(
        HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFile + ".zip\"");
    responseHeaders.set(
        HttpHeaders.CONTENT_TYPE,
        String.valueOf(MediaType.parseMediaType("application/octet-stream")));
    ByteArrayInputStream byteArrayInputStream =
        new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    return ZipFileResponseDTO.builder()
        .headers(responseHeaders)
        .body(new InputStreamResource(byteArrayInputStream))
        .build();
  }

  private String getFileUriWithPrefix(String prefix) {
    String directoryPath = UPLOAD_PATH + getUserPathFromFilename(prefix);
    java.io.File directory = new java.io.File(directoryPath);
    java.io.File[] files = directory.listFiles((dir, name) -> name.startsWith(prefix));
    if (files != null && files.length > 0) {
      return files[0].getAbsolutePath();
    }
    return null;
  }

  @Override
  public ResponseEntity<byte[]> streamVideoV2(String fileName, String rangeHeader) {
    long rangeStart = 0;
    long rangeEnd;
    byte[] data;
    Long fileSize;
    String fullPath = getVideoUploadPath(sessionHelper.getTenantId()) + fileName;
    Path path = Path.of(fullPath);
    if (!fileExists(path)) {
      throw new EntityNotFoundException(FILE_NOT_FOUND);
    }

    String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
    try {
      fileSize = Optional.ofNullable(fileName)
              .map(file -> Paths.get(fullPath))
              .map(this::sizeFromFile)
              .orElse(0L);
      if (rangeHeader == null) {
        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "video/" + fileType)
                .header("Content-Length", String.valueOf(fileSize))
                .body(readByteRange(path, rangeStart, fileSize - 1));
      }
      String[] ranges = rangeHeader.split("-");
      rangeStart = Long.parseLong(ranges[0].substring(6));
      if (ranges.length > 1) {
        rangeEnd = Long.parseLong(ranges[1]);
      } else {
        rangeEnd = fileSize - 1;
      }
      if (fileSize < rangeEnd) {
        rangeEnd = fileSize - 1;
      }
      data = readByteRange(path, rangeStart, rangeEnd);
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
    return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
            .header("Content-Type", "video/" + fileType)
            .header("Accept-Ranges", "bytes")
            .header("Content-Length", contentLength)
            .header("Content-Range", "bytes" + " " + rangeStart + "-" + rangeEnd + "/" + fileSize)
            .body(data);
  }
  public byte[] readByteRange(Path path, long start, long end) throws IOException {
    try (InputStream inputStream = (Files.newInputStream(path));
         ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream()) {
      byte[] data = new byte[1024];
      int nRead;
      while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
        bufferedOutputStream.write(data, 0, nRead);
      }
      bufferedOutputStream.flush();
      byte[] result = new byte[(int) (end - start) + 1];
      System.arraycopy(bufferedOutputStream.toByteArray(), (int) start, result, 0, result.length);
      return result;
    }
  }
  private boolean fileExists(Path path) {
    return Files.exists(path);
  }
  private Long sizeFromFile(Path path) {
    try {
      return Files.size(path);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return 0L;
  }
}
