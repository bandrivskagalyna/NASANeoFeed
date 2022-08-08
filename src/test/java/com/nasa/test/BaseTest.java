package com.nasa.test;

import com.nasa.base.DateHelper;
import com.nasa.base.HttpHelper;
import org.testng.annotations.BeforeSuite;

public class BaseTest {

    private HttpHelper httpHelper;
    private DateHelper dateHelper;

    @BeforeSuite
    public void beforeSuite() {
        httpHelper = new HttpHelper();
        dateHelper = new DateHelper();
    }

    protected HttpHelper getHttpHelper() {
        return httpHelper;
    }

    protected DateHelper getDateHelper() {
        return dateHelper;
    }
}
