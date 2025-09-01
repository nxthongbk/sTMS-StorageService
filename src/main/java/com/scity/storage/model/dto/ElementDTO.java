package com.scity.storage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
public class ElementDTO {
    private String key;
    private String value;
    private Date updatedAt;
}
