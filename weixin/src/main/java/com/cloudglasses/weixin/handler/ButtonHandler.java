package com.cloudglasses.weixin.handler;

import com.cloudglasses.weixin.builder.TextBuilder;
import com.cloudglasses.weixin.service.SystemConfigService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ButtonHandler extends AbstractHandler {
    public static String[] BUTTON_KEYS = new String[]{"B1002_CONNECTION", "B1003_COOPERATION"};

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        logger.info("ButtonKey:{}", wxMessage.getEventKey());
        String buttonKey = wxMessage.getEventKey();
        String result = systemConfigService.getValue(buttonKey);
        return new TextBuilder().build(result, wxMessage, wxMpService);
    }
}
