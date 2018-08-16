package com.cloudglasses.weixin.handler;

import com.cloudglasses.model.WeixinUser;
import com.cloudglasses.repository.WeixinUserRepository;
import com.cloudglasses.util.CodeUtile;
import com.cloudglasses.util.ValidateUtile;
import com.cloudglasses.weixin.builder.TextBuilder;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType;


@Component
public class MsgHandler extends AbstractHandler {
    private Map<String, MobileCode> keyMap = new ConcurrentHashMap<>();
    @Autowired
    private WeixinUserRepository weixinUserRepository;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {

        if (!wxMessage.getMsgType().equals(XmlMsgType.EVENT)) {
            //TODO 可以选择将消息保存到本地
        }
        String openid = wxMessage.getFromUser();

        //判断用户是否在关联手机号
        String messageContent = wxMessage.getContent();
        if (StringUtils.startsWith(messageContent, "关联手机号")) {
            return getMobileCodeByWeixinText(wxMessage, weixinService, openid, messageContent);
        } else {
            return relationMobile(wxMessage, weixinService, openid, messageContent);
        }
    }

    /**
     * 确认获取手机号
     * <p>
     * 短信方式
     *
     * @param wxMessage
     * @param weixinService
     * @param openid
     * @param messageContent
     * @return
     */
    private WxMpXmlOutMessage getMobileCodeByMobileText(WxMpXmlMessage wxMessage, WxMpService weixinService, String openid, String messageContent) {
        String mobile = messageContent.replace("关联手机号", "");
        if (ValidateUtile.isPhone(mobile)) {
            String code = CodeUtile.generateCode();
            keyMap.put(openid, new MobileCode(code, mobile));
            // TODO 发送验证码

            return new TextBuilder().build("请回复验证码", wxMessage, weixinService);
        } else {
            return new TextBuilder().build("手机号格式有误!", wxMessage, weixinService);
        }
    }


    /**
     * 确认获取手机号
     * <p>
     * 微信方式
     *
     * @param wxMessage
     * @param weixinService
     * @param openid
     * @param messageContent
     * @return
     */
    private WxMpXmlOutMessage getMobileCodeByWeixinText(WxMpXmlMessage wxMessage, WxMpService weixinService, String openid, String messageContent) {
        String mobile = messageContent.replace("关联手机号", "");
        if (ValidateUtile.isPhone(mobile)) {
            keyMap.put(openid, new MobileCode(mobile, mobile));
            return new TextBuilder().build("确认手机号码:" + mobile + "。请回复:Yes", wxMessage, weixinService);
        } else {
            return new TextBuilder().build("手机号格式有误!", wxMessage, weixinService);
        }
    }

    /**
     * 关联手机号
     *
     * @param wxMessage
     * @param weixinService
     * @param openid
     * @param messageContent
     * @return
     */
    private WxMpXmlOutMessage relationMobile(WxMpXmlMessage wxMessage, WxMpService weixinService, String openid, String messageContent) {
        MobileCode mobileCode = keyMap.get(openid);
        keyMap.remove(openid);
        if (mobileCode != null && (messageContent.equals(mobileCode.key) || ("Yes".equals(messageContent) && mobileCode.key.equals(mobileCode.mobile)))) {
            WeixinUser user = weixinUserRepository.findById(openid).get();
            user.setMobile(mobileCode.mobile);
            weixinUserRepository.save(user);
            return new TextBuilder().build("关联成功!", wxMessage, weixinService);
        }
        return null;
    }


    private class MobileCode {
        private String key;
        private String mobile;

        MobileCode(String key, String mobile) {
            this.key = key;
            this.mobile = mobile;
        }
    }
}
