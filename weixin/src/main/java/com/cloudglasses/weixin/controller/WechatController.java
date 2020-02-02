package com.cloudglasses.weixin.controller;

import com.cloudglasses.model.SystemConfig;
import com.cloudglasses.repository.SystemConfigRepository;
import com.cloudglasses.weixin.service.WeixinUserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialFileBatchGetResult;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.result.WxMpUserList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@RestController
@RequestMapping("/wechat/portal")
@Slf4j
public class WechatController {
    @Autowired
    private WxMpService wxService;
    @Autowired
    private WxMpMessageRouter router;
    @Autowired
    private SystemConfigRepository systemConfigRepository;
    @Autowired
    private WeixinUserService weixinUserService;

    @GetMapping(produces = "text/plain;charset=utf-8")
    public String authGet(
            @RequestParam(name = "signature",
                    required = false) String signature,
            @RequestParam(name = "timestamp",
                    required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr) {

        log.info("\n接收到来自微信服务器的认证消息：[{}, {}, {}, {}]", signature,
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
        log.info(
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
            log.debug("\n消息解密后内容为：\n{} ", inMessage.toString());
            WxMpXmlOutMessage outMessage = this.route(inMessage);
            if (outMessage == null) {
                return "";
            }

            out = outMessage
                    .toEncryptedXml(this.wxService.getWxMpConfigStorage());
        }

        log.debug("\n组装回复信息：{}", out);

        return out;
    }

    @GetMapping("menu/create")
    public String createMenu() throws WxErrorException {
        File menuFile = new File("menu.json");
        if (menuFile.exists()) {
            try {
                String input = FileUtils.readFileToString(menuFile, "UTF-8");
                wxService.getMenuService().menuCreate(input);
            } catch (IOException e) {
                log.error("读取菜单json错误", e);
            }
        }
        return "success";
    }

    @GetMapping("users")
    @Transactional(rollbackOn = Exception.class)
    public String getAllUsers() throws WxErrorException {
        SystemConfig nextOpenidConfig = systemConfigRepository.findById("nextOpenid").get();
        WxMpUserList wxMpUserList = wxService.getUserService().userList(nextOpenidConfig.getCfgValue());

        if (wxMpUserList.getOpenids().size() > 0) {
            List<WxMpUser> wxMpUsers = wxService.getUserService().userInfoList(wxMpUserList.getOpenids());

            for (WxMpUser wxMpUser : wxMpUsers) {
                weixinUserService.save(wxMpUser);
            }

            nextOpenidConfig.setCfgValue(wxMpUserList.getNextOpenid());
            systemConfigRepository.save(nextOpenidConfig);
        }

        return "success";
    }

    @GetMapping("test")
    public String text() throws WxErrorException {
        WxMpMaterialFileBatchGetResult wxMpMaterialFileBatchGetResult = wxService.getMaterialService().materialFileBatchGet(WxConsts.MaterialType.NEWS, 0, 20);

        List<String> collect = wxMpMaterialFileBatchGetResult.getItems().stream().map(item -> {
            String mediaId = item.getMediaId();
            String title = "";
            try {
                title = wxService.getMaterialService().materialNewsInfo(mediaId).getArticles().get(0).getTitle();
            } catch (WxErrorException e) {
                log.error("获取素材失败", e);
            }

            return title + ":" + mediaId;
        }).collect(Collectors.toList());

        log.info(collect.toString());
        return "success";
    }

    private WxMpXmlOutMessage route(WxMpXmlMessage message) {
        try {
            return this.router.route(message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}
