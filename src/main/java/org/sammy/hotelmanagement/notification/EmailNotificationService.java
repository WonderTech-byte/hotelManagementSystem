package org.sammy.hotelmanagement.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sammy.hotelmanagement.booking.Booking;
import org.sammy.hotelmanagement.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromEmail;



    @Async
    protected void send(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to {} | Subject: {}", toEmail, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {} | Error: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendPasswordResetEmail(User user, String resetLink) {
        String subject = "Password Reset Request";
        String body = String.format("""
                Dear %s,

                We received a request to reset your password.

                Reset link:
                %s

                If you did not request this, you can ignore this email.

                Hotel Management Team
                """,
                user.getFullName(),
                resetLink
        );
        send(user.getEmail(), subject, body);
    }


    @Async
    public void sendBookingConfirmation(User guest, Booking booking) {
        String subject = "Booking Confirmed — " + booking.getBookingCode();
        String body = String.format("""
                Dear %s,

                Your booking has been confirmed. Here are your details:

                Booking Code : %s
                Room         : %s (%s)
                Check-in     : %s
                Check-out    : %s
                Nights       : %d
                Total Price  : $%.2f

                Please present your booking code at the front desk on arrival.

                Thank you for choosing us.
                Hotel Management Team
                """,
                guest.getFullName(),
                booking.getBookingCode(),
                booking.getRoom().getRoomNumber(),
                booking.getRoom().getRoomType(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getNumberOfNights(),
                booking.getTotalPrice()
        );
        send(guest.getEmail(), subject, body);
    }


    @Async
    public void sendCheckInReminder(User guest, Booking booking) {
        String subject = "Reminder: Check-in Tomorrow — " + booking.getBookingCode();
        String body = String.format("""
                Dear %s,

                This is a reminder that your check-in is scheduled for tomorrow.

                Booking Code : %s
                Room         : %s
                Check-in     : %s

                We look forward to welcoming you.

                Hotel Management Team
                """,
                guest.getFullName(),
                booking.getBookingCode(),
                booking.getRoom().getRoomNumber(),
                booking.getCheckInDate()
        );
        send(guest.getEmail(), subject, body);
    }



    @Async
    public void sendCheckedInNotice(User guest, Booking booking) {
        String subject = "Welcome! You're Checked In — " + booking.getBookingCode();
        String body = String.format("""
                Dear %s,

                You have been successfully checked in.

                Room         : %s
                Check-out    : %s

                Enjoy your stay!

                Hotel Management Team
                """,
                guest.getFullName(),
                booking.getRoom().getRoomNumber(),
                booking.getCheckOutDate()
        );
        send(guest.getEmail(), subject, body);
    }



    @Async
    public void sendCheckOutReminder(User guest, Booking booking) {
        String subject = "Check-out Today — " + booking.getBookingCode();
        String body = String.format("""
                Dear %s,

                This is a reminder that your check-out is scheduled for today (%s).

                Please return your room key at the front desk.
                We hope you had a wonderful stay!

                Hotel Management Team
                """,
                guest.getFullName(),
                booking.getCheckOutDate()
        );
        send(guest.getEmail(), subject, body);
    }


    @Async
    public void sendCancellationNotice(User guest, Booking booking) {
        String subject = "Booking Cancelled — " + booking.getBookingCode();
        String body = String.format("""
                Dear %s,

                Your booking has been cancelled.

                Booking Code : %s
                Room         : %s
                Check-in     : %s
                Check-out    : %s

                If you did not request this cancellation, please contact us immediately.

                Hotel Management Team
                """,
                guest.getFullName(),
                booking.getBookingCode(),
                booking.getRoom().getRoomNumber(),
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        );
        send(guest.getEmail(), subject, body);
    }


    @Async
    public void sendNoShowNoticeToGuest(User guest, Booking booking) {
        String subject = "Booking Marked as No-Show — " + booking.getBookingCode();
        String body = String.format("""
                Dear %s,

                Your booking (%s) has been marked as a no-show because we did not
                receive you on your scheduled check-in date of %s.

                If you believe this is an error, please contact the hotel.

                Hotel Management Team
                """,
                guest.getFullName(),
                booking.getBookingCode(),
                booking.getCheckInDate()
        );
        send(guest.getEmail(), subject, body);
    }



    @Async
    public void sendNoShowAlertToAdmin(User admin, User guest, Booking booking) {
        String subject = "[ADMIN] No-Show Detected — " + booking.getBookingCode();
        String body = String.format("""
                Admin Alert,

                A no-show has been detected and automatically recorded.

                Booking Code : %s
                Guest        : %s (%s)
                Room         : %s
                Check-in     : %s

                The booking status has been updated to NO_SHOW.

                Hotel Management System
                """,
                booking.getBookingCode(),
                guest.getFullName(),
                guest.getEmail(),
                booking.getRoom().getRoomNumber(),
                booking.getCheckInDate()
        );
        send(admin.getEmail(), subject, body);
    }
}
