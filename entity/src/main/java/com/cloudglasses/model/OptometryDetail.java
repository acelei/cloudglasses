package com.cloudglasses.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Accessors(chain = true)
public class OptometryDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "mobile")
    private String mobile;
    @Column(name = "sph_r")
    private Float sphR;
    @Column(name = "sph_l")
    private Float sphL;
    @Column
    private Integer pd;
    @Column(name = "cyl_r")
    private Float cylR;
    @Column(name = "cyl_l")
    private Float cylL;
    @Column(name = "axis_r")
    private Integer axisR;
    @Column(name = "axis_l")
    private Integer axisL;
    @Column(name = "vision_r")
    private Float visionR;
    @Column(name = "vision_l")
    private Float visionL;
    @Column
    private String address;
    @Column
    private Date createTime;
    @Column
    private Date updateTime;
    @Column
    private Boolean status;
}
