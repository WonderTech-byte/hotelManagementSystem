package org.sammy.hotelmanagement.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequestDTO {

    @NotNull(message = "Room ID is required")
    public Long roomId;

    @NotNull(message = "Check-in date is required")
    @Future(message = "Check-in date must be in the future")
    public LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    public LocalDate checkOutDate;

    @Min(value = 1, message = "At least 1 unit required")
    public int numberOfUnits = 1;
}
