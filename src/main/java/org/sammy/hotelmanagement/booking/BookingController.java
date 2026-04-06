package org.sammy.hotelmanagement.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sammy.hotelmanagement.dto.BookingRequestDTO;
import org.sammy.hotelmanagement.dto.BookingResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;


    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(
            @Valid @RequestBody BookingRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(dto));
    }


    @GetMapping("/my")
    public ResponseEntity<List<BookingResponseDTO>> getMyBookings() {
        return ResponseEntity.ok(bookingService.getMyBookings());
    }


    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponseDTO> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }


    @PatchMapping("/{bookingCode}/checkin")
    public ResponseEntity<BookingResponseDTO> checkIn(@PathVariable String bookingCode) {
        return ResponseEntity.ok(bookingService.checkIn(bookingCode));
    }


    @PatchMapping("/{bookingCode}/checkout")
    public ResponseEntity<BookingResponseDTO> checkOut(@PathVariable String bookingCode) {
        return ResponseEntity.ok(bookingService.checkOut(bookingCode));
    }


    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }


    @GetMapping("/{bookingCode}")
    public ResponseEntity<BookingResponseDTO> getByCode(@PathVariable String bookingCode) {
        return ResponseEntity.ok(bookingService.getByBookingCode(bookingCode));
    }
}
