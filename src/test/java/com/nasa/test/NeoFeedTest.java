package com.nasa.test;

import com.nasa.base.ErrorMessage;
import com.nasa.base.HttpHelper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;

@Test(testName = "NeoFeed")
public class NeoFeedTest extends BaseTest {

    private String endpointUrl;
    private String startDate;
    private String endDate;

    @BeforeClass
    public void initClass() throws Exception {
        endpointUrl = httpHelper.buildEndpointUrl("neo/rest/v1/feed");
        startDate = dateHelper.convertDateToString(LocalDate.of(2015, 9, 1));
        endDate = dateHelper.convertDateToString(LocalDate.of(2015, 9, 8));
    }

    @Test
    public void testSuccessful() {
        LinkedHashMap nearEarthObjectsMap = RestAssured.given()
                .queryParam("start_date", startDate)
                .queryParam("end_date", endDate)
                .queryParam("api_key", HttpHelper.apiKey)
                .when().get(endpointUrl).then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .body("", notNullValue())
                .body("links", notNullValue())
                .body("element_count", notNullValue())
                .body("near_earth_objects", notNullValue())
                .extract()
                .path("near_earth_objects");

        Assert.assertNotNull(nearEarthObjectsMap.get(startDate));
        Assert.assertNotNull(nearEarthObjectsMap.get(endDate));
    }

    @Test
    public void testInvalidAPIKey() throws Exception {
        Map<String, String> paramMap = httpHelper.buildParameterMap("invalid api key", startDate, endDate);

        Response response = super.doGetRequest(endpointUrl, paramMap);

        Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_FORBIDDEN);
        Assert.assertEquals(httpHelper.getErrorCode(response), ErrorMessage.INVALID_API_KEY);
    }

    @Test
    public void testInvalidDateFormat() {
        String invalidDate = "dddd-dd-dd";
        Map<String, String> paramMap = httpHelper.buildParameterMap(startDate, invalidDate);
        Response response = super.doGetRequest(endpointUrl, paramMap);

        Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_BAD_REQUEST);

        String errorCode = response.jsonPath().getString("error_message");
        Assert.assertEquals(errorCode, ErrorMessage.INVALID_DATE_FORMAT.concat(invalidDate));
    }

    @Test
    public void testExceededFeedDateLimit() {
        String exceededDate = dateHelper.convertDateToString(LocalDate.of(2022, 9, 8));
        Map<String, String> paramMap = httpHelper.buildParameterMap(startDate, exceededDate);
        Response response = super.doGetRequest(endpointUrl, paramMap);

        Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_BAD_REQUEST);

        String errorCode = response.jsonPath().getString("error_message");
        Assert.assertEquals(errorCode, ErrorMessage.INVALID_DATE_FORMAT_DATE_LIMIT_EXCEEDED);
    }

    @Test
    @Ignore("BUG: Start date and End date switched after sending request")
    public void testStartDateAfterEndDate() {
        Map<String, String> paramMap = httpHelper.buildParameterMap(endDate, startDate);
        Response response = super.doGetRequest(endpointUrl, paramMap);

        Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }
}
