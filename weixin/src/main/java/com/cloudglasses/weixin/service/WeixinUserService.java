package com.cloudglasses.weixin.service;

import com.cloudglasses.model.WeixinUser;
import com.cloudglasses.repository.WeixinUserRepository;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class WeixinUserService {
    @Autowired
    private WeixinUserRepository weixinUserRepository;
    @Autowired
    private WxMpService wxMpService;

    public void save(String openId) throws WxErrorException {
        WxMpUser userWxInfo = wxMpService.getUserService()
                .userInfo(openId);
        if (userWxInfo != null) {
            weixinUserRepository.save(getWeixinUser(userWxInfo));
        }
    }

    public void save(WxMpUser wxMpUser) {
        weixinUserRepository.save(getWeixinUser(wxMpUser));
    }

    public List<WeixinUser> findAll() {
        return weixinUserRepository.findAll();
    }


    private WeixinUser getWeixinUser(WxMpUser userWxInfo) {
        WeixinUser weixinUser = new WeixinUser();
        BeanUtils.copyProperties(userWxInfo, weixinUser);

        return weixinUser;
    }

    public WeixinUser findById(String openId) throws WxErrorException {
        WeixinUser weixinUser = weixinUserRepository.findById(openId).orElse(null);
        if (weixinUser == null) {
            WxMpUser userWxInfo = wxMpService.getUserService()
                    .userInfo(openId);
            weixinUser = weixinUserRepository.save(getWeixinUser(userWxInfo));
        }

        return weixinUser;
    }
}
