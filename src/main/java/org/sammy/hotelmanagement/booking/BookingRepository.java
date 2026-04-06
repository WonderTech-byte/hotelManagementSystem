package org.sammy.hotelmanagement.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingCode(String bookingCode);
    List<Booking> findByGuestId(Long guestId);
    List<Booking> findByRoomId(Long roomId);
    List<Booking> findByStatus(BookingStatus status);


    @Query("SELECT b FROM Booking b WHERE b.status = 'CONFIRMED' AND b.checkInDate < :today")
    List<Booking> findNoShowBookings(@Param("today") LocalDate today);


    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.room.id = :roomId " +
           "AND b.status IN ('CONFIRMED', 'CHECKED_IN') " +
           "AND b.checkInDate < :checkOut AND b.checkOutDate > :checkIn")
    boolean isRoomBooked(@Param("roomId") Long roomId,
                         @Param("checkIn") LocalDate checkIn,
                         @Param("checkOut") LocalDate checkOut);
}
