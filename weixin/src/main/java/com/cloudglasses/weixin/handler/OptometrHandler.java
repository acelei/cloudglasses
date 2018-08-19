package com.cloudglasses.weixin.handler;

import com.cloudglasses.model.WeixinUser;
import com.cloudglasses.weixin.builder.TextBuilder;
import com.cloudglasses.weixin.service.OptometryDetailService;
import com.cloudglasses.weixin.service.WeixinUserService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class OptometrHandler extends AbstractHandler {
    public static final String BUTTON_KEY = "B1001_OPTOMETRY";

    @Autowired
    private WeixinUserService weixinUserService;
    @Autowired
    private OptometryDetailService optometryDetailService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        WeixinUser user = weixinUserService.findById(wxMessage.getFromUser());


        if (StringUtils.isEmpty(user.getMobile())) {
            return new TextBuilder().build("请输入:验光+手机号码\n例如:验光13888888888", wxMessage, wxMpService);
        } else {
            String result = optometryDetailService.getOptometryForm(user);
            if (StringUtils.isEmpty(result)) {
                result = "您还没有验光数据!";
            }

            return new TextBuilder().build(result, wxMessage, wxMpService);
        }
    }
}
