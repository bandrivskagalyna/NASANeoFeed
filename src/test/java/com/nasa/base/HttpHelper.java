package com.nasa.base;

import com.nasa.configuration.PropertyLoader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

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
        if (error == null || error.isEmpty()) {
            throw new Exception("No 'error' block in the response");
        }
        return error.get("message");
    }

    public Map<String, String> buildParameterMap(String startDate, String endDate) {
        return buildParameterMap(apiKey, startDate, endDate);
    }

    public Map<String, String> buildParameterMap(String apiKey, String startDate, String endDate) {
        Map<String, String> paramMap = new HashMap<>();
        if (startDate != null) {
            paramMap.put("start_date", startDate);
        }

        if (endDate != null) {
            paramMap.put("end_date", endDate);
        }

        paramMap.put("api_key", apiKey);
        return paramMap;
    }

    public Response doGetRequest(String endpoint, Map<String, String> params) {
        RestAssured.defaultParser = Parser.JSON;

        RequestSpecification requestSpecification = given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON);
        if (params != null && !params.isEmpty()) {
            for (var entry : params.entrySet()) {
                requestSpecification.queryParam(entry.getKey(), entry.getValue());
            }
        }
        return requestSpecification.
                when().get(endpoint).
                then().log().all().contentType(ContentType.JSON).extract().response();

    }
}
