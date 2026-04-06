package org.sammy.hotelmanagement.util;

import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.UUID;

@Component
public class BookingCodeGenerator {


    public String generate() {
        String year = String.valueOf(Year.now().getValue());
        String suffix = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();
        return "BK-" + year + "-" + suffix;
    }
}
