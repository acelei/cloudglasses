package com.cloudglasses.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
public class WeixinUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String openid;
    @Column
    private String unionid;
    @Column
    private String mobile;
    @Column
    private Boolean subscribe;
    @Column
    private String nickname;
    @Column
    @Enumerated(value = EnumType.ORDINAL)
    private Sex sex;
    @Column
    private String language;
    @Column
    private String city;
    @Column
    private String province;
    @Column
    private String country;
    @Column
    private String headimgurl;
    @Column
    private Long subscribeTime;
    @Column
    private String subscribeScene;
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "mobile", referencedColumnName = "mobile")
    private Set<OptometryDetail> optometryDetails;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Boolean getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Boolean subscribe) {
        this.subscribe = subscribe;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public Long getSubscribeTime() {
        return subscribeTime;
    }

    public void setSubscribeTime(Long subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    public String getSubscribeScene() {
        return subscribeScene;
    }

    public void setSubscribeScene(String subscribeScene) {
        this.subscribeScene = subscribeScene;
    }

    public Set<OptometryDetail> getOptometryDetails() {
        return optometryDetails;
    }

    public void setOptometryDetails(Set<OptometryDetail> optometryDetails) {
        this.optometryDetails = optometryDetails;
    }
}
