package com.nasa.test;

import com.nasa.base.DateHelper;
import com.nasa.base.HttpHelper;
import com.nasa.configuration.PropertyLoader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.BeforeSuite;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class BaseTest {

    protected HttpHelper httpHelper;

    protected DateHelper dateHelper;

    @BeforeSuite
    public void beforeSuite() {
        httpHelper = new HttpHelper();
        dateHelper = new DateHelper();
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
