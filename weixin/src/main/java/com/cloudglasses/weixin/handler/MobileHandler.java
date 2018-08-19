package com.cloudglasses.weixin.handler;

import com.cloudglasses.model.WeixinUser;
import com.cloudglasses.repository.WeixinUserRepository;
import com.cloudglasses.util.CodeUtile;
import com.cloudglasses.util.ValidateUtile;
import com.cloudglasses.weixin.builder.TextBuilder;
import com.cloudglasses.weixin.service.OptometryDetailService;
import com.cloudglasses.weixin.service.SmsService;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class MobileHandler extends AbstractHandler {
    public static Map<String, MobileCode> keyMap = new ConcurrentHashMap<>();
    @Autowired
    private WeixinUserRepository weixinUserRepository;
    @Autowired
    private OptometryDetailService optometryDetailService;
    @Autowired
    private SmsService smsService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {
        String openid = wxMessage.getFromUser();

        //判断用户是否在关联手机号
        String messageContent = wxMessage.getContent();
        if ("1".equals(messageContent)) {
            return relationMobile(wxMessage, weixinService, openid, messageContent);
        } else {
//            try {
//                return getMobileCodeByMobileText(wxMessage, weixinService, openid, messageContent);
//            } catch (Exception e) {
//                logger.error("短信发送失败", e);
                return getMobileCodeByWeixinText(wxMessage, weixinService, openid, messageContent);
//            }
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
    private WxMpXmlOutMessage getMobileCodeByMobileText(WxMpXmlMessage wxMessage, WxMpService weixinService, String openid, String messageContent) throws Exception {
        String mobile = messageContent.replace("验光", "");
        if (ValidateUtile.isPhone(mobile)) {
            String code = CodeUtile.generateCode();
            keyMap.put(openid, new MobileCode(code, mobile));
            smsService.sendCode(mobile, code);
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
        String mobile = messageContent.replace("验光", "");
        if (ValidateUtile.isPhone(mobile)) {
            keyMap.put(openid, new MobileCode(mobile, mobile));
            return new TextBuilder().build("请确认手机号码:" + mobile + ",如正确请回复:1", wxMessage, weixinService);
        } else {
            return new TextBuilder().build("手机号格式有误,请重新输入", wxMessage, weixinService);
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
        if (mobileCode != null && (messageContent.equals(mobileCode.key) || ("1".equalsIgnoreCase(messageContent) && mobileCode.key.equals(mobileCode.mobile)))) {
            WeixinUser user = weixinUserRepository.findById(openid).get();
            user.setMobile(mobileCode.mobile);
            weixinUserRepository.save(user);

            String optometryForm = optometryDetailService.getOptometryFormByMobile(mobileCode.mobile);

            if (StringUtils.isEmpty(optometryForm)) {
                optometryForm = "您还没有验光数据!";
            }

            return new TextBuilder().build(optometryForm, wxMessage, weixinService);
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
