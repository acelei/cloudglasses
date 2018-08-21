package com.cloudglasses.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@DynamicUpdate
@Data
@Accessors(chain = true)
public class WeixinUser implements Serializable {
    @Id
    private String openId;
    @Column
    private String unionId;
    @Column
    private String mobile;
    @Column
    private Boolean subscribe;
    @Column
    private String nickname;
    @Column
    private Integer sex;
    @Column
    private String language;
    @Column
    private String city;
    @Column
    private String province;
    @Column
    private String country;
    @Column
    private String headImgUrl;
    @Column
    private Long subscribeTime;
    @Column
    private String subscribeScene;
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "mobile", referencedColumnName = "mobile")
    private Set<OptometryDetail> optometryDetails;
}
