package org.sammy.hotelmanagement.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sammy.hotelmanagement.booking.Booking;
import org.sammy.hotelmanagement.booking.BookingRepository;
import org.sammy.hotelmanagement.booking.BookingStatus;
import org.sammy.hotelmanagement.room.RoomStatus;
import org.sammy.hotelmanagement.room.RoomRepository;
import org.sammy.hotelmanagement.user.User;
import org.sammy.hotelmanagement.user.UserRepository;
import org.sammy.hotelmanagement.user.UserType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailScheduler {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final EmailNotificationService emailService;


    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void checkNoShows() {
        LocalDate today = LocalDate.now();
        log.info("NoShowScheduler running for date: {}", today);

        List<Booking> noShows = bookingRepository.findNoShowBookings(today);

        if (noShows.isEmpty()) {
            log.info("No no-shows found.");
            return;
        }

        List<User> admins = userRepository.findByUserType(UserType.ADMIN);

        for (Booking booking : noShows) {
            log.info("Marking booking {} as NO_SHOW", booking.getBookingCode());


            booking.setStatus(BookingStatus.NO_SHOW);
            bookingRepository.save(booking);


            booking.getRoom().setStatus(RoomStatus.AVAILABLE);
            roomRepository.save(booking.getRoom());


            emailService.sendNoShowNoticeToGuest(booking.getGuest(), booking);


            for (User admin : admins) {
                emailService.sendNoShowAlertToAdmin(admin, booking.getGuest(), booking);
            }
        }

        log.info("NoShowScheduler completed. {} no-show(s) processed.", noShows.size());
    }
}
