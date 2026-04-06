package org.sammy.hotelmanagement.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
public class DateUtils {



    public boolean isValidRange(LocalDate checkIn, LocalDate checkOut) {
        return checkIn != null && checkOut != null && checkOut.isAfter(checkIn);
    }

    public boolean isInThePast(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }
}
