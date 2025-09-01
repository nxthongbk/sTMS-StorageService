package com.scity.storage.model.entity.audit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@NoArgsConstructor
@MappedSuperclass
@JsonIgnoreProperties(
    value = {"created_at", "updated_at", "created_by", "updated_by"},
    allowGetters = true)
public abstract class BaseEntityAudit extends BaseEntity implements Serializable {
  @JsonIgnore
  public String updatedBy;
  @JsonIgnore
  protected Date createdAt;
  @JsonIgnore
  protected String createdBy;
  @JsonIgnore
  protected Date updatedAt;
}
