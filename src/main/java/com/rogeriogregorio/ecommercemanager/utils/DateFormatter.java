package com.rogeriogregorio.ecommercemanager.utils;

import org.springframework.stereotype.Component;

@Component
public interface DateFormatter {

    String toISO8601(String date);
}
