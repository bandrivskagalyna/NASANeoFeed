package com.nasa.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyLoader {

    public static String baseUrl;

    public static String apiKey;

    static {
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream("./src/main/resources/ApplicationConfig.properties"));

            baseUrl = prop.getProperty("baseUrl");
            apiKey = prop.getProperty("APIKey");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
