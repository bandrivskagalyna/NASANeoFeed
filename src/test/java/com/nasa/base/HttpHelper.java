package com.nasa.base;

import com.nasa.configuration.PropertyLoader;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class HttpHelper {

    public static String baseUrl = PropertyLoader.baseUrl;

    public static String apiKey = PropertyLoader.apiKey;

    public String buildEndpointUrl(String apiEndpoint) throws Exception {
        if (StringUtils.isEmpty(baseUrl))
            throw new Exception("baseUrl should be present in ApplicationConfig.property file");
        return baseUrl.concat(apiEndpoint);
    }

    public String getErrorCode(Response response) throws Exception {
        Map<String, String> error = response.jsonPath().getMap("error");
        if (error == null | error.isEmpty()) {
            throw new Exception("No 'error' block in the response");
        }
        return error.get("message");
    }

    public Map<String, String> buildParameterMap(String startDate, String endDate) {
        return buildParameterMap(apiKey, startDate, endDate);
    }

    public Map<String, String> buildParameterMap(String apiKey, String startDate, String endDate) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("start_date", endDate);
        paramMap.put("end_date", startDate);
        paramMap.put("api_key", apiKey);
        return paramMap;
    }
}
