package com.rogeriogregorio.ecommercemanager.util;

import org.springframework.stereotype.Component;

@Component
public interface DateFormatter {

    String toISO8601(String date);
}
