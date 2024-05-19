package com.rogeriogregorio.ecommercemanager.util.impl;

import com.rogeriogregorio.ecommercemanager.util.DateFormatter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class DateFormatterImpl implements DateFormatter {

    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter
            .ofPattern("dd-MM-yyyy", Locale.ENGLISH);

    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public String toISO8601(String date) {

        LocalDate localDate = LocalDate.parse(date, INPUT_FORMATTER);

        return localDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .atOffset(ZoneOffset.UTC)
                .format(OUTPUT_FORMATTER);
    }
}
