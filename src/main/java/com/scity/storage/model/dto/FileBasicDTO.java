package com.scity.storage.model.dto;

import lombok.Data;

@Data
public class FileBasicDTO {

  private String id;
  private String signature;
  private boolean existed = false;
}
