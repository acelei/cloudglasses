package com.cloudglasses.weixin.config;

import com.cloudglasses.weixin.handler.*;
import me.chanjar.weixin.common.api.WxConsts.EventType;
import me.chanjar.weixin.common.api.WxConsts.MenuButtonType;
import me.chanjar.weixin.common.api.WxConsts.XmlMsgType;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@ConditionalOnClass(WxMpService.class)
@EnableConfigurationProperties(WechatMpProperties.class)
public class WeiXinConfig {
    @Autowired
    private WechatMpProperties properties;
    @Autowired
    protected LogHandler logHandler;
    @Autowired
    protected NullHandler nullHandler;
    @Autowired
    private MobileHandler mobileHandler;
    @Autowired
    private UnsubscribeHandler unsubscribeHandler;
    @Autowired
    private SubscribeHandler subscribeHandler;
    @Autowired
    private OptometrHandler optometrHandler;
    @Autowired
    private MsgHandler msgHandler;
    @Autowired
    private ButtonHandler buttonHandler;

    @Bean
    @ConditionalOnMissingBean
    public WxMpConfigStorage configStorage() {
        WxMpInMemoryConfigStorage configStorage = new WxMpInMemoryConfigStorage();
        configStorage.setAppId(this.properties.getAppId());
        configStorage.setSecret(this.properties.getSecret());
        configStorage.setToken(this.properties.getToken());
        configStorage.setAesKey(this.properties.getAesKey());
        return configStorage;
    }

    @Bean
    @ConditionalOnMissingBean
    public WxMpService wxMpService(WxMpConfigStorage configStorage) {
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(configStorage);
        return wxMpService;
    }

    @Bean
    public WxMpMessageRouter router(WxMpService wxMpService) throws WxErrorException {

        final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);

        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(this.logHandler).next();

        // 点击菜单连接事件
        newRouter.rule().async(false)
                .msgType(XmlMsgType.EVENT)
                .event(MenuButtonType.VIEW)
                .handler(nullHandler)
                .end();


        // 自定义菜单事件
        newRouter.rule().async(false)
                .msgType(XmlMsgType.EVENT)
                .event(MenuButtonType.CLICK)
                .eventKey(OptometrHandler.BUTTON_KEY)
                .handler(optometrHandler)
                .end();

        for (String buttonKey : ButtonHandler.BUTTON_KEYS) {
            newRouter.rule().async(false)
                    .msgType(XmlMsgType.EVENT)
                    .event(MenuButtonType.CLICK)
                    .eventKey(buttonKey)
                    .handler(buttonHandler)
                    .end();
        }

        // 获取验光单
        newRouter.rule().async(false)
                .msgType(XmlMsgType.TEXT)
                .rContent("验光单$")
                .handler(optometrHandler)
                .end();

        // 关注事件
        newRouter.rule().async(false)
                .msgType(XmlMsgType.EVENT)
                .event(EventType.SUBSCRIBE)
                .handler(subscribeHandler)
                .end();

        // 取消关注事件
        newRouter.rule().async(false)
                .msgType(XmlMsgType.EVENT)
                .event(EventType.UNSUBSCRIBE)
                .handler(this.unsubscribeHandler)
                .end();

        // 关联手机号
        newRouter.rule().async(false)
                .rContent("验光\\d+")
                .handler(mobileHandler)
                .end();
        newRouter.rule().async(false)
                .content("1")
                .handler(mobileHandler)
                .end();

        // 默认事件
        newRouter.rule().async(false)
                .msgType(XmlMsgType.TEXT)
                .event(EventType.CLICK)
                .handler(msgHandler)
                .end();

        return newRouter;
    }
}
