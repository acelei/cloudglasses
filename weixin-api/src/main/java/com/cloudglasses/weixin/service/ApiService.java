package com.cloudglasses.weixin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApiService {
    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);

    @Autowired
    private TokenService tokenService;

    @Value("${weixin.appid}")
    private String appid;

    @Value("${weixin.secret}")
    private String secret;

    @Value("${weixin.default-mess}")
    private String defaultMessage;

    private final String baseUrl = "https://api.weixin.qq.com/cgi-bin/";
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final ObjectMapper mapper = new ObjectMapper();


    public JsonNode getToken() throws IOException {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", "client_credential"));
        params.add(new BasicNameValuePair("appid", appid));
        params.add(new BasicNameValuePair("secret", secret));

        String paramStr = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
        HttpGet httpGet = new HttpGet(baseUrl + Method.TOKEN.url + "?" + paramStr);

        CloseableHttpResponse response = httpClient.execute(httpGet);

        String jsonStr = EntityUtils.toString(response.getEntity());
        return mapper.readTree(jsonStr);
    }

    public JsonNode getUserList(String nextOpenid) throws IOException {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("access_token", tokenService.getToken()));
        params.add(new BasicNameValuePair("next_openid", nextOpenid));

        String paramStr = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
        HttpGet httpGet = new HttpGet(baseUrl + Method.USERLIST.url + "?" + paramStr);

        CloseableHttpResponse response = httpClient.execute(httpGet);

        String jsonStr = EntityUtils.toString(response.getEntity());
        return mapper.readTree(jsonStr);
    }

    public JsonNode getUserInfo(String openid) throws IOException {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("access_token", tokenService.getToken()));
        params.add(new BasicNameValuePair("openid", openid));
        params.add(new BasicNameValuePair("lang", "zh_CN"));

        String paramStr = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
        HttpGet httpGet = new HttpGet(baseUrl + Method.USERINFO.url + "?" + paramStr);

        CloseableHttpResponse response = httpClient.execute(httpGet);

        String jsonStr = EntityUtils.toString(response.getEntity());
        return mapper.readTree(jsonStr);
    }

    public String getCreateTime() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    enum Method {
        TOKEN("token"),
        USERLIST("user/get"),
        USERINFO("user/info");

        private String url;

        Method(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
