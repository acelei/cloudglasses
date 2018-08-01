package com.cloudglasses.weixin.controller;

import com.cloudglasses.weixin.service.MessageUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("weixin")
public class WeiXinRestController {

    @RequestMapping("")
    public String weixinMessageService() {
        return MessageUtil.DEFAULT_MESSAGE;
    }
}
