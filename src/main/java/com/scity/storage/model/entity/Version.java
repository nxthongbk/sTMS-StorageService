package com.scity.storage.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name="app_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Version {
    @Id
    private String version;
    private Date updatedAt;
    private Boolean isSignUp;
    private Date signUpUpdatedAt;

}
