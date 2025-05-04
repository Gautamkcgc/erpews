package com.erpews.service;

import com.erpews.config.CallAPI;
import com.erpews.entity.ApiDetail;
import com.erpews.entity.CallAPIModel;
import com.erpews.entity.EmailContent;
import com.erpews.repository.ApiDetailrepository;
import com.erpews.repository.EmailContentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiSchedulerService {

    @Autowired
    private ApiDetailrepository apiDetailsRepository;

    @Autowired
    private EmailContentRepository emailContentRepository;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private CallAPI callAPI;

    Logger logger = LoggerFactory.getLogger(getClass());

    private List<String> failedApis = new ArrayList<>();

    public void scheduleApiCalls() {
        failedApis.clear();
        List<ApiDetail> apiDetailsList = apiDetailsRepository.findAll();

        for (ApiDetail api : apiDetailsList) {
            try {
                makeApiCall(api);
            } catch (Exception e) {
                logger.error("scheduleApiCalls", e);
            }
        }

        if (!failedApis.isEmpty()) {
            sendFailureEmail();
        }
    }

    private void makeApiCall(ApiDetail apiDetails) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", apiDetails.getAuthorization());

        CallAPIModel response = callAPI.sendGet(apiDetails.getApiUrl(), headers);

        if (response.getResponseCode() != 200) {
            failedApis.add("Product: " + apiDetails.getProductName() +
                    ", URL: " + apiDetails.getApiUrl() +
                    ", ResponseCode: " + response.getResponseCode());
        }
    }



    private void sendFailureEmail() {
        String title = "ERPEWS";
        EmailContent emailContent = emailContentRepository.findByTitle(title);
        String subject = emailContent.getSubject();
        StringBuilder htmlTable = new StringBuilder();
        htmlTable.append("<html><body>");
        htmlTable.append("<h3>Failed API Calls Report</h3>");

        htmlTable.append("<table border='1' style='border-collapse: collapse; width: 100%; text-align: left;'>");
        htmlTable.append("<tr>");
        htmlTable.append("<th>Product Name</th>");
        htmlTable.append("<th>API URL</th>");
        htmlTable.append("<th>Response Code</th>");
        htmlTable.append("</tr>");

        for (String failedApi : failedApis) {
            String[] parts = failedApi.split(", ");
            String productName = parts[0].split(": ")[1];
            String apiUrl = parts[1].split(": ")[1];
            String responseCode = parts[2].split(": ")[1];

            htmlTable.append("<tr>");
            htmlTable.append("<td>").append(productName).append("</td>");
            htmlTable.append("<td>").append(apiUrl).append("</td>");
            htmlTable.append("<td>").append(responseCode).append("</td>");
            htmlTable.append("</tr>");
        }

        htmlTable.append("</table>");
        htmlTable.append("</body></html>");

        emailSender.sendEmail(title, subject, htmlTable.toString());
    }


}
