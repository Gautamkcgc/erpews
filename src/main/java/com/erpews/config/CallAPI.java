package com.erpews.config;

import com.erpews.entity.CallAPIModel;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class CallAPI {

    Logger logger= LoggerFactory.getLogger(getClass());

    public CallAPIModel sendGet(String apiUrl, Map<String, String> headers) {
        CallAPIModel model = new CallAPIModel();
        HttpGet post = new HttpGet(apiUrl);

        if (headers != null) {
            for (Map.Entry<String, String> set : headers.entrySet()) {
                post.addHeader(set.getKey(), set.getValue());
            }
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {
            model.setResponseCode(response.getCode());
            model.setResponse(
                    response.getEntity() != null ? EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)
                            : "");
        } catch (Exception e) {
            logger.error("sendGet", e);
            model.setResponseCode(500);
            model.setResponse(e.getMessage());
        }
        return model;
    }
}




