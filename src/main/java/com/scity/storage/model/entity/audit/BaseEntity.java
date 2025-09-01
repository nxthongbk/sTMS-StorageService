package com.scity.storage.model.entity.audit;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

  @Id
  protected String id;

  public BaseEntity() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BaseEntity that)) {
      return false;
    }
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "BaseEntity {" + "id = " + id + "}";
  }
}
