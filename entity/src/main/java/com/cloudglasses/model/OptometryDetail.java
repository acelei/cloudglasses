package com.cloudglasses.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
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
    @Column(name = "[type]")
    private Integer type;
    @Column
    private Date createTime;
    @Column
    private Date updateTime;
    @Column
    private Boolean status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Float getSphR() {
        return sphR;
    }

    public void setSphR(Float sphR) {
        this.sphR = sphR;
    }

    public Float getSphL() {
        return sphL;
    }

    public void setSphL(Float sphL) {
        this.sphL = sphL;
    }

    public Integer getPd() {
        return pd;
    }

    public void setPd(Integer pd) {
        this.pd = pd;
    }

    public Float getCylR() {
        return cylR;
    }

    public void setCylR(Float cylR) {
        this.cylR = cylR;
    }

    public Float getCylL() {
        return cylL;
    }

    public void setCylL(Float cylL) {
        this.cylL = cylL;
    }

    public Integer getAxisR() {
        return axisR;
    }

    public void setAxisR(Integer axisR) {
        this.axisR = axisR;
    }

    public Integer getAxisL() {
        return axisL;
    }

    public void setAxisL(Integer axisL) {
        this.axisL = axisL;
    }

    public Float getVisionR() {
        return visionR;
    }

    public void setVisionR(Float visionR) {
        this.visionR = visionR;
    }

    public Float getVisionL() {
        return visionL;
    }

    public void setVisionL(Float visionL) {
        this.visionL = visionL;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
