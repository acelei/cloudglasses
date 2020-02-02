package com.cloudglasses.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@Accessors(chain = true)
public class SystemConfig {
    @Id
    private String cfgName;
    @Column
    private String cfgValue;
}
