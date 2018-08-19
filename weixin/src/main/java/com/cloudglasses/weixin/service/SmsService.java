package com.cloudglasses.weixin.service;

import com.cloudglasses.weixin.config.SmsProperties;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmsService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private SmsSingleSender ssender;
    private SmsProperties properties;

    public SmsService(SmsProperties smsProperties) {
        this.properties = smsProperties;
        this.ssender = new SmsSingleSender(smsProperties.getAppId(), smsProperties.getAppKey());
    }


    public void sendCode(String mobile, String code) throws Exception {
        int smsVerificationTemplateId = 177321;
        SmsSingleSenderResult result = ssender.sendWithParam("86", mobile,
                smsVerificationTemplateId, new String[]{code}, properties.getSmsSign(), "", "");
        logger.info(result.toString());
        if (!StringUtils.isEmpty(result.errMsg)) {
            throw new Exception(result.errMsg);
        }
    }
}
