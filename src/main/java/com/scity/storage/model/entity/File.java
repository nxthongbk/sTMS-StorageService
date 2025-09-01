package com.scity.storage.model.entity;

import com.scity.storage.model.entity.audit.BaseEntityAudit;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "storage_file")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Data
public class File extends BaseEntityAudit {
  private String originalName;
  private String extension;
  private String type;
  private Long size;
  private String description;
}
