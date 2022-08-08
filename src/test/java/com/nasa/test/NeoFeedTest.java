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
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeClass
    public void initClass() throws Exception {
        endpointUrl = getHttpHelper().buildEndpointUrl("neo/rest/v1/feed");
        startDate = LocalDate.of(2015, 9, 1);
        endDate = LocalDate.of(2015, 9, 8);
    }

    @Test
    public void testSuccessful() {
        String startDateStr = getDateHelper().convertDateToString(startDate);
        String endDateStr = getDateHelper().convertDateToString(endDate);
        LinkedHashMap nearEarthObjectsMap = RestAssured.given()
                .queryParam("start_date", startDateStr)
                .queryParam("end_date", endDateStr)
                .queryParam("api_key", HttpHelper.apiKey)
                .when().get(endpointUrl).then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .body("", notNullValue())
                .body("links", notNullValue())
                .body("element_count", notNullValue())
                .body("near_earth_objects", notNullValue())
                .extract()
                .path("near_earth_objects");

        Assert.assertNotNull(nearEarthObjectsMap.get(startDateStr));
        Assert.assertNotNull(nearEarthObjectsMap.get(endDateStr));
    }

    @Test
    public void testInvalidAPIKey() throws Exception {
        Map<String, String> paramMap = getHttpHelper().buildParameterMap("invalid api key",
                getDateHelper().convertDateToString(startDate), getDateHelper().convertDateToString(endDate));

        Response response = getHttpHelper().doGetRequest(endpointUrl, paramMap);

        Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_FORBIDDEN);
        Assert.assertEquals(getHttpHelper().getErrorCode(response), ErrorMessage.INVALID_API_KEY);
    }

    @Test
    public void testInvalidDateFormat() {
        String invalidDate = "dddd-dd-dd";
        Map<String, String> paramMap = getHttpHelper().buildParameterMap(getDateHelper().convertDateToString(startDate), invalidDate);
        Response response = getHttpHelper().doGetRequest(endpointUrl, paramMap);

        Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_BAD_REQUEST);

        String errorCode = response.jsonPath().getString("error_message");
        Assert.assertEquals(errorCode, ErrorMessage.INVALID_DATE_FORMAT.concat(invalidDate));
    }

    @Test
    public void testExceededFeedDateLimit() {
        Map<String, String> paramMap = getHttpHelper().buildParameterMap(getDateHelper().convertDateToString(startDate),
                getDateHelper().convertDateToString(startDate.plusDays(10)));
        Response response = getHttpHelper().doGetRequest(endpointUrl, paramMap);

        Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_BAD_REQUEST);

        String errorCode = response.jsonPath().getString("error_message");
        Assert.assertEquals(errorCode, ErrorMessage.INVALID_DATE_FORMAT_DATE_LIMIT_EXCEEDED);
    }

    @Test
    @Ignore("BUG: Start date and End date switched after sending request")
    public void testStartDateAfterEndDate() {
        Map<String, String> paramMap = getHttpHelper().buildParameterMap(getDateHelper().convertDateToString(endDate),
                getDateHelper().convertDateToString(startDate));
        Response response = getHttpHelper().doGetRequest(endpointUrl, paramMap);

        Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void testAbsentDateParametersReturnsDataForUpcomingWeek() {
        Map<String, String> paramMap = getHttpHelper().buildParameterMap(null, null);
        Response response = getHttpHelper().doGetRequest(endpointUrl, paramMap);

        Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK);

        LocalDate today = getDateHelper().getCurrentLocalDateInUTC();
        LocalDate endDate = today.plusDays(7);

        String startDateDefault = getDateHelper().convertDateToString(today);
        String endDateDefault = getDateHelper().convertDateToString(endDate);

        String body = response.getBody().asString();
        Assert.assertTrue(body.contains(startDateDefault));
        Assert.assertTrue(body.contains(endDateDefault));
    }

    @Test
    @Ignore("BUG: Should be either valid response with start date= end date-7 or more specific error like 'Start date parameter is missing'")
    public void testAbsentStartDateParameter() {
        Map<String, String> paramMap = getHttpHelper().buildParameterMap(null, getDateHelper().convertDateToString(endDate));
        Response response = getHttpHelper().doGetRequest(endpointUrl, paramMap);

        Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK);

        String startDateDefault = getDateHelper().convertDateToString(endDate.minusDays(7));
        String endDateStr = getDateHelper().convertDateToString(endDate);

        String body = response.getBody().asString();
        Assert.assertTrue(body.contains(startDateDefault));
        Assert.assertTrue(body.contains(endDateStr));
    }

    @Test
    public void testAbsentEndDateParameter() {
        Map<String, String> paramMap = getHttpHelper().buildParameterMap(getDateHelper().convertDateToString(startDate), null);
        Response response = getHttpHelper().doGetRequest(endpointUrl, paramMap);

        Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK);

        String startDateStr = getDateHelper().convertDateToString(startDate);
        String endDateDefault = getDateHelper().convertDateToString(startDate.plusDays(7));

        String body = response.getBody().asString();
        Assert.assertTrue(body.contains(startDateStr));
        Assert.assertTrue(body.contains(endDateDefault));
    }

}
