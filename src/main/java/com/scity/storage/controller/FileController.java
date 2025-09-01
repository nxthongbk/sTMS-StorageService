package com.scity.storage.controller;

import com.scity.storage.aop.annotation.AuthorizeRequest;
import com.scity.storage.exception.ForbiddenException;
import com.scity.storage.exception.MaxUploadSizeExceedException;
import com.scity.storage.model.dto.*;
import com.scity.storage.model.entity.File;
import com.scity.storage.service.impl.FileServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@Validated
@RequestMapping(value = "/files")
public class FileController {

  @Autowired
  private FileServiceImpl fileServiceImplement;

  @Operation(description = "Upload file", summary = "Upload file")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "Successfully",
              content = @Content(mediaType = "application/json")),
          @ApiResponse(responseCode = "400", description = "Bad request"),
          @ApiResponse(responseCode = "401", description = "unauthenticated"),
          @ApiResponse(responseCode = "403", description = "Unauthorized"),
          @ApiResponse(responseCode = "404", description = "Not found")
      })
  @PostMapping(
      value = "/upload-file",
      consumes = {"multipart/form-data", "application/json"})
  @AuthorizeRequest
  public ResponseEntity<ResModel<FileDTO>> uploadFile(
      @RequestParam(value = "description", defaultValue = "") String description,
      @RequestParam("file") @Valid MultipartFile multipartFile)
          throws IOException {
    FileDTO response = fileServiceImplement.uploadFile(multipartFile, description);
    return new ResponseEntity<>(ResModel.ok(response), HttpStatus.OK);
  }

  @Operation(description = "Upload files", summary = "Upload files")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "Successfully",
              content = @Content(mediaType = "application/json")),
          @ApiResponse(responseCode = "400", description = "Bad request"),
          @ApiResponse(responseCode = "401", description = "unauthenticated"),
          @ApiResponse(responseCode = "403", description = "Unauthorized"),
          @ApiResponse(responseCode = "404", description = "Not found")
      })
  @PostMapping(
      value = "/upload-files",
      consumes = {"multipart/form-data", "application/json"})
  @AuthorizeRequest
  public ResponseEntity<ResModel<List<FileDTO>>> uploadFiles(
      @RequestParam(value = "description", defaultValue = "") String description,
      @RequestParam("files") @Valid MultipartFile[] multipartFile)
          throws IOException, MaxUploadSizeExceedException {
    List<FileDTO> response = fileServiceImplement.uploadFiles(multipartFile, description);
    return new ResponseEntity<>(ResModel.ok(response), HttpStatus.OK);
  }

  @Operation(description = "Get file info", summary = "Get file info by id")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "Successfully",
              content = @Content(mediaType = "application/json")),
          @ApiResponse(responseCode = "400", description = "Bad request"),
          @ApiResponse(responseCode = "401", description = "unauthenticated"),
          @ApiResponse(responseCode = "403", description = "Unauthorized"),
          @ApiResponse(responseCode = "404", description = "Not found")
      })
  @GetMapping("/{id}")
  @AuthorizeRequest
  public ResponseEntity<ResModel<File>> getFile(@PathVariable String id)
      throws MethodArgumentTypeMismatchException {
    File file = fileServiceImplement.getFile(id);
    return new ResponseEntity<>(ResModel.ok(file), HttpStatus.OK);
  }

  @PutMapping("/{id}")
  @AuthorizeRequest
  public ResponseEntity<ResModel<File>> updateFile(
      @PathVariable String id, @RequestBody @Valid FileUpdateDTO fileUpdateDTO)
          throws MethodArgumentTypeMismatchException, ForbiddenException {
    File file = fileServiceImplement.updateFile(id, fileUpdateDTO);
    return new ResponseEntity<>(ResModel.ok(file), HttpStatus.OK);
  }

  @Operation(description = "Delete file", summary = "Delete file by id")
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "Successfully",
              content = @Content(mediaType = "application/json")),
          @ApiResponse(responseCode = "400", description = "Bad request"),
          @ApiResponse(responseCode = "401", description = "unauthenticated"),
          @ApiResponse(responseCode = "403", description = "Unauthorized"),
          @ApiResponse(responseCode = "404", description = "Not found")
      })
  @DeleteMapping("/{id}")
  @AuthorizeRequest
  public ResponseEntity<ResModel<Void>> deleteFile(@PathVariable String id)
          throws MethodArgumentTypeMismatchException, IOException, ForbiddenException {
    fileServiceImplement.deleteFile(id);
    return new ResponseEntity<>(ResModel.ok( null), HttpStatus.OK);
  }

  @DeleteMapping("/batch")
  @AuthorizeRequest
  public ResponseEntity<ResModel<Void>> deleteFiles(@RequestBody BatchDeleteDTO fileIds) {
    fileServiceImplement.deleteFiles(fileIds);
    return new ResponseEntity<>(ResModel.ok(null), HttpStatus.OK);
  }


  @Operation(description = "Get file thumbnail", summary = "{file} = file_id")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Successfully"),
          @ApiResponse(responseCode = "400", description = "Bad request"),
          @ApiResponse(responseCode = "401", description = "unauthenticated"),
          @ApiResponse(responseCode = "403", description = "Unauthorized"),
          @ApiResponse(responseCode = "404", description = "Not found")
      })

  @GetMapping(value = "/image/{file}")
  @AuthorizeRequest
  public ResponseEntity<StreamingResponseBody> getImageByFileName(
      @PathVariable(value = "file") String fileName,
      @RequestHeader(value = "Range", defaultValue = "") String range) throws IOException {
    StreamingMediaDTO resourceDto = fileServiceImplement.getImageFile(fileName, range);
    HttpHeaders headers = resourceDto.getHeaders();
    headers.setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic());

    return new ResponseEntity<>(resourceDto.getResponseBody(), headers, HttpStatus.OK);

  }

  @Operation(description = "Get image", summary = "{file} = file_id")
  @ApiResponses(
          value = {
                  @ApiResponse(responseCode = "200", description = "Successfully"),
                  @ApiResponse(responseCode = "400", description = "Bad request"),
                  @ApiResponse(responseCode = "401", description = "unauthenticated"),
                  @ApiResponse(responseCode = "403", description = "Unauthorized"),
                  @ApiResponse(responseCode = "404", description = "Not found")
          })

  @GetMapping(value = "/thumbnail/{file}")
  @AuthorizeRequest
  public ResponseEntity<StreamingResponseBody> getThumbnailByFileName(
          @PathVariable(value = "file") String fileName) throws IOException {
    StreamingMediaDTO resourceDto = fileServiceImplement.getThumbnailImage(
            fileName); //fileService.getWebFile(fileName);//

    HttpHeaders headers = resourceDto.getHeaders();
    headers.setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic());

    return new ResponseEntity<>(resourceDto.getResponseBody(), headers, HttpStatus.OK);

  }

  @Operation(description = "Get file origin", summary = "{file} = file_id + extension")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Successfully"),
          @ApiResponse(responseCode = "400", description = "Bad request"),
          @ApiResponse(responseCode = "401", description = "unauthenticated"),
          @ApiResponse(responseCode = "403", description = "Unauthorized"),
          @ApiResponse(responseCode = "404", description = "Not found")
      })
  @GetMapping("/download/{file}")
  @AuthorizeRequest
  public ResponseEntity<StreamingResponseBody> downloadFile(
      @PathVariable(value = "file") String fileName,
      @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {
    StreamingMediaDTO resourceDto = fileServiceImplement.downloadFile(fileName, rangeHeader);
    StreamingResponseBody responseBody = resourceDto.getResponseBody();

    return ResponseEntity.ok().headers(resourceDto.getHeaders()).body(responseBody);
  }


  @Operation(description = "Validation file", summary = "Validation file")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Successfully"),
          @ApiResponse(responseCode = "400", description = "Bad request"),
          @ApiResponse(responseCode = "401", description = "unauthenticated"),
          @ApiResponse(responseCode = "403", description = "Unauthorized"),
          @ApiResponse(responseCode = "404", description = "Not found")
      })
  @PostMapping("/validation")
  @AuthorizeRequest
  public ResponseEntity<ResModel<FileBasicDTO[]>> validateFile(
      @RequestBody FileBasicDTO[] fileBasic) {
    return ResponseEntity.ok()
        .body(ResModel.ok(fileServiceImplement.validateFile(fileBasic)));
  }

  @Operation(description = "Stream video", summary = "{file} = file_id + extension")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Successfully"),
          @ApiResponse(responseCode = "400", description = "Bad request"),
          @ApiResponse(responseCode = "401", description = "unauthenticated"),
          @ApiResponse(responseCode = "403", description = "Unauthorized"),
          @ApiResponse(responseCode = "404", description = "Not found")
      })
  @GetMapping("/noauth/stream-video/{file}")
  public Mono<ResponseEntity<byte[]>> streamVideo(
          @PathVariable(value = "file") String fileName,
          @RequestHeader(value = "Range", required = false) String rangeHeader) {
    return Mono.just(fileServiceImplement.streamVideoV2(fileName, rangeHeader));
  }

  @PostMapping("/download-zip")
  @AuthorizeRequest
  public ResponseEntity<InputStreamResource> downloadFilesAsZip(
      @RequestBody @Valid ZipFileRequestDTO requestDTO) throws IOException {
    ZipFileResponseDTO zipFileResponseDTO =
        fileServiceImplement.downloadFilesAsZip(requestDTO.getFiles(), requestDTO.getName());
    return ResponseEntity.ok()
        .headers(zipFileResponseDTO.getHeaders())
        .body(zipFileResponseDTO.getBody());
  }

  @DeleteMapping("/system/{fileId}")
  @AuthorizeRequest(roles = "SYSADMIN")
  public ResModel<String> deleteSystemFile(
          @PathVariable String fileId
  ) throws IOException {
    fileServiceImplement.deleteFileSystem(fileId);
    return ResModel.ok("Successfully");
  }

  @PostMapping(value = "/system"
          , consumes = {"multipart/form-data", "application/json"})
  @AuthorizeRequest(roles = "SYSADMIN")
  public ResModel<FileDTO> uploadSystemFile(
          @RequestParam("file") @Valid MultipartFile multipartFile
  ) throws IOException {
    return ResModel.ok(fileServiceImplement.uploadFileSystem(multipartFile));
  }

  @GetMapping("/system/{file}")
  public ResponseEntity<StreamingResponseBody> downloadSystemFile(
          @PathVariable(value = "file") String fileName,
          @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {
    StreamingMediaDTO resourceDto = fileServiceImplement.loadSystemFile(fileName, rangeHeader);
    StreamingResponseBody responseBody = resourceDto.getResponseBody();

    return ResponseEntity.ok().headers(resourceDto.getHeaders()).body(responseBody);
  }
}
