package com.cloudglasses.weixin.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

@Service
public class TokenService {
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    @Autowired
    private ApiService apiService;

    /**
     * token获取延时设置
     */
    private int delayed = 10;

    /**
     * token值
     */
    private String accessToken = null;

    /**
     * 过期时间
     */
    private Date expiresDate = null;

    public String getToken() {
        if (expiresDate == null || expiresDate.getTime() < System.currentTimeMillis()) {
            createToken();
        }
        return accessToken;
    }

    private synchronized void createToken() {
        if (expiresDate == null || expiresDate.getTime() < System.currentTimeMillis()) {
            try {
                JsonNode rootNode = apiService.getToken();
                accessToken = rootNode.get("access_token").toString();

                if ("null".equals(accessToken)) {
                    logger.error("获取token失败", rootNode);
                    return;
                }

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.SECOND, rootNode.get("expires_in").asInt() - delayed);
                expiresDate = calendar.getTime();
            } catch (IOException e) {
                logger.error("获取token失败", e);
            }
        }
    }
}
