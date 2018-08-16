package com.cloudglasses.weixin.controller;

import com.cloudglasses.model.SystemConfig;
import com.cloudglasses.model.WeixinUser;
import com.cloudglasses.repository.OptometryDetailRepository;
import com.cloudglasses.repository.SystemConfigRepository;
import com.cloudglasses.repository.WeixinUserRepository;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMenuService;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.menu.WxMpGetSelfMenuInfoResult;
import me.chanjar.weixin.mp.bean.menu.WxMpSelfMenuInfo;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.result.WxMpUserList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@RestController
@RequestMapping("/wechat/portal")
public class WechatController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WxMpService wxService;
    @Autowired
    private WxMpMessageRouter router;
    @Autowired
    private SystemConfigRepository systemConfigRepository;
    @Autowired
    private WeixinUserRepository weixinUserRepository;
    @Autowired
    private OptometryDetailRepository optometryDetailsRepository;

    @GetMapping(produces = "text/plain;charset=utf-8")
    public String authGet(
            @RequestParam(name = "signature",
                    required = false) String signature,
            @RequestParam(name = "timestamp",
                    required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr) {

        this.logger.info("\n接收到来自微信服务器的认证消息：[{}, {}, {}, {}]", signature,
                timestamp, nonce, echostr);

        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }

        if (this.wxService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }

        return "非法请求";
    }

    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(@RequestBody String requestBody,
                       @RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce,
                       @RequestParam(name = "encrypt_type",
                               required = false) String encType,
                       @RequestParam(name = "msg_signature",
                               required = false) String msgSignature) {
        this.logger.info(
                "\n接收微信请求：[signature=[{}], encType=[{}], msgSignature=[{}],"
                        + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                signature, encType, msgSignature, timestamp, nonce, requestBody);

        if (!this.wxService.checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }

        String out = null;
        if (encType == null) {
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
            WxMpXmlOutMessage outMessage = this.route(inMessage);
            if (outMessage == null) {
                return "";
            }

            out = outMessage.toXml();
        } else if ("aes".equals(encType)) {
            // aes加密的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(
                    requestBody, this.wxService.getWxMpConfigStorage(), timestamp,
                    nonce, msgSignature);
            this.logger.debug("\n消息解密后内容为：\n{} ", inMessage.toString());
            WxMpXmlOutMessage outMessage = this.route(inMessage);
            if (outMessage == null) {
                return "";
            }

            out = outMessage
                    .toEncryptedXml(this.wxService.getWxMpConfigStorage());
        }

        this.logger.debug("\n组装回复信息：{}", out);

        return out;
    }

    @GetMapping("menu/create")
    public String createMenu() throws WxErrorException {
        WxMpMenuService menuService = wxService.getMenuService();
        WxMpGetSelfMenuInfoResult selfMenuInfoResult = menuService.getSelfMenuInfo();
        WxMpSelfMenuInfo selfMenuInfo = selfMenuInfoResult.getSelfMenuInfo();

        if (selfMenuInfo == null) {
            menuService.menuCreate("{\"button\":[{\"type\":\"click\",\"name\":\"验光单\",\"key\":\"B1001_OPTOMETRY\"}]}");
        }

        return "success";
    }

    @GetMapping("users")
    @Transactional(rollbackOn = Exception.class)
    public String getAllUsers() throws WxErrorException {
        SystemConfig nextOpenidConfig = systemConfigRepository.findById("nextOpenid").get();
        WxMpUserList wxMpUserList = wxService.getUserService().userList(nextOpenidConfig.getCfgValue());

        if (wxMpUserList.getOpenids().size()>0){
            List<WxMpUser> wxMpUsers = wxService.getUserService().userInfoList(wxMpUserList.getOpenids());

            for (WxMpUser wxMpUser : wxMpUsers) {
                WeixinUser weixinUser = getWeixinUser(wxMpUser);
                weixinUserRepository.save(weixinUser);
            }

            nextOpenidConfig.setCfgValue(wxMpUserList.getNextOpenid());
            systemConfigRepository.save(nextOpenidConfig);
        }

        return "success";
    }

    @GetMapping("test")
    public String text() {
        List<WeixinUser> users = weixinUserRepository.findAll();
        return "success";
    }

    private WxMpXmlOutMessage route(WxMpXmlMessage message) {
        try {
            return this.router.route(message);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }

        return null;
    }

    private WeixinUser getWeixinUser(WxMpUser userWxInfo) {
        WeixinUser weixinUser = new WeixinUser();
        BeanUtils.copyProperties(userWxInfo, weixinUser);

        return weixinUser;
    }
}
