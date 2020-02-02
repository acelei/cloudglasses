package com.cloudglasses.weixin.service;

import com.cloudglasses.weixin.config.SmsProperties;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmsService {
    private SmsSingleSender ssender;

    public SmsService(SmsProperties smsProperties) {
        this.ssender = new SmsSingleSender(smsProperties.getAppId(), smsProperties.getAppKey());
    }


    public void sendCode(String mobile, String code) throws Exception {
        int smsVerificationTemplateId = 177321;
        SmsSingleSenderResult result = ssender.sendWithParam("86", mobile,
                smsVerificationTemplateId, new String[]{code}, "", "", "");
        log.info(result.toString());
        if (!"OK".equals(result.errMsg)) {
            throw new Exception(result.errMsg);
        }
    }
}
