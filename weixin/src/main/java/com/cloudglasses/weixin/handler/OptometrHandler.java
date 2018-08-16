package com.cloudglasses.weixin.handler;

import com.cloudglasses.model.OptometryDetail;
import com.cloudglasses.model.WeixinUser;
import com.cloudglasses.repository.WeixinUserRepository;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Map;


@Component
public class OptometrHandler extends AbstractHandler {
    public static final String BUTTON_KEY = "B1001_OPTOMETRY";

    @Autowired
    private WeixinUserRepository weixinUserRepository;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        WeixinUser user = weixinUserRepository.findFirstByOpenid(wxMessage.getToUser());
        OptometryDetail optometryDetail = user.getOptometryDetails().stream()
                .filter(OptometryDetail::getStatus)
                .max(Comparator.comparing(OptometryDetail::getCreateTime))
                .get();

        if (optometryDetail == null) {
            return WxMpXmlOutMessage.TEXT().content("请先关联手机号,请输入:关联手机号+手机号码\n例如:关联手机号13888888888")
                    .fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser())
                    .build();
        } else {

            String optometryForm = getOptometryForm(optometryDetail);
            return WxMpXmlOutMessage.TEXT().content(optometryForm)
                    .fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser())
                    .build();
        }
    }

    private String getOptometryForm(OptometryDetail optometryDetail) {
        // TODO 查看验光单
        return "";
    }
}
