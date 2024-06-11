package com.rogeriogregorio.ecommercemanager.utils.impl;

import com.rogeriogregorio.ecommercemanager.utils.DateFormatter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class DateFormatterImpl implements DateFormatter {

    private static final DateTimeFormatter INPUT = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);

    private static final DateTimeFormatter OUTPUT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public String toISO8601(String date) {

        return LocalDate.parse(date, INPUT)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .atOffset(ZoneOffset.UTC)
                .format(OUTPUT);
    }
}
