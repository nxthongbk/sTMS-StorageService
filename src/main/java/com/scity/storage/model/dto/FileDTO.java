package com.scity.storage.model.dto;

import lombok.Data;

@Data
public class FileDTO {

  private String id;
  private String extension;
  private String original_name;
  private String type;
  private Long size;
  private String description;
  private String signature;
}
