package com.scity.storage.controller;

import com.scity.storage.model.dto.ResModel;
import com.scity.storage.service.IFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;

@RestController
@RequestMapping("/private/files")
public class FilePrivateController {

  @Autowired
  private IFileService fileService;

  @Operation(description = "Delete file internal", summary = "Delete file internal")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully"),
      @ApiResponse(responseCode = "400", description = "Bad request"),
      @ApiResponse(responseCode = "401", description = "unauthenticated"),
      @ApiResponse(responseCode = "403", description = "Unauthorized"),
      @ApiResponse(responseCode = "404", description = "Not found")
  })
  @DeleteMapping()
  public ResponseEntity<ResModel<Void>> deleteFileSys(@RequestBody String[] ids)
      throws MethodArgumentTypeMismatchException, IOException {
    fileService.deleteFileSystem(ids);
    return new ResponseEntity<>(ResModel.ok(null), HttpStatus.OK);
  }
}
