package org.sammy.hotelmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sammy.hotelmanagement.booking.BookingStatus;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    public Long id;
    public String bookingCode;
    public UserDTO guest;
    public RoomDTO room;
    public LocalDate checkInDate;
    public LocalDate checkOutDate;
    public int numberOfUnits;
    public long numberOfNights;   // computed
    public double totalPrice;     // computed
    public BookingStatus status;
}
