package com.nasa.base;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateHelper {

    public String convertDateToString(LocalDate date) {
        DateTimeFormatter dateFormat = getFormatter();
        return dateFormat.format(date);
    }

    public DateTimeFormatter getFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }
}
