package org.sammy.hotelmanagement.booking;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sammy.hotelmanagement.room.Room;
import org.sammy.hotelmanagement.user.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String bookingCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private User guest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Column(nullable = false)
    private int numberOfUnits = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = BookingStatus.CONFIRMED;
        }
    }


    @Transient
    public long getNumberOfNights() {
        if (checkInDate == null || checkOutDate == null) return 0;
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }


    @Transient
    public double getTotalPrice() {
        if (room == null) return 0;
        return room.getPrice() * getNumberOfNights() * numberOfUnits;
    }
}
