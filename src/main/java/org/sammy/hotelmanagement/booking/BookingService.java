package org.sammy.hotelmanagement.booking;

import lombok.RequiredArgsConstructor;
import org.sammy.hotelmanagement.booking.dto.BookingRequestDTO;
import org.sammy.hotelmanagement.booking.dto.BookingResponseDTO;
import org.sammy.hotelmanagement.exception.BadRequestException;
import org.sammy.hotelmanagement.exception.ForbiddenException;
import org.sammy.hotelmanagement.exception.NotFoundException;
import org.sammy.hotelmanagement.notification.EmailNotificationService;
import org.sammy.hotelmanagement.room.Room;
import org.sammy.hotelmanagement.room.RoomRepository;
import org.sammy.hotelmanagement.room.RoomStatus;
import org.sammy.hotelmanagement.room.dto.RoomDTO;
import org.sammy.hotelmanagement.user.dto.UserDTO;
import org.sammy.hotelmanagement.room.RoomService;
import org.sammy.hotelmanagement.user.User;
import org.sammy.hotelmanagement.user.UserRepository;
import org.sammy.hotelmanagement.util.BookingCodeGenerator;
import org.sammy.hotelmanagement.util.DateUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BookingCodeGenerator codeGenerator;
    private final EmailNotificationService emailService;
    private final RoomService roomService;
    private final DateUtils dateUtils;



    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO dto) {

        if (!dateUtils.isValidRange(dto.checkInDate, dto.checkOutDate)) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }
        if (dateUtils.isInThePast(dto.checkInDate)) {
            throw new BadRequestException("Check-in date cannot be in the past");
        }

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User guest = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Room room = roomRepository.findById(dto.roomId)
                .orElseThrow(() -> new NotFoundException("Room not found: " + dto.roomId));

        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new BadRequestException("Room is not available");
        }

        if (bookingRepository.isRoomBooked(dto.roomId, dto.checkInDate, dto.checkOutDate)) {
            throw new BadRequestException("Room is already booked for the selected dates");
        }

        Booking booking = Booking.builder()
                .bookingCode(codeGenerator.generate())
                .guest(guest)
                .room(room)
                .checkInDate(dto.checkInDate)
                .checkOutDate(dto.checkOutDate)
                .numberOfUnits(dto.numberOfUnits)
                .status(BookingStatus.CONFIRMED)
                .build();

        bookingRepository.save(booking);


        room.setStatus(RoomStatus.BOOKED);
        roomRepository.save(room);

        emailService.sendBookingConfirmation(guest, booking);

        return toDTO(booking);
    }


    public List<BookingResponseDTO> getMyBookings() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User guest = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return bookingRepository.findByGuestId(guest.getId())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }



    @Transactional
    public BookingResponseDTO cancelBooking(Long bookingId) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));

        if (!booking.getGuest().getUsername().equals(username)) {
            throw new ForbiddenException("You are not authorised to cancel this booking");
        }
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BadRequestException("Only CONFIRMED bookings can be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        booking.getRoom().setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(booking.getRoom());

        emailService.sendCancellationNotice(booking.getGuest(), booking);
        return toDTO(booking);
    }


    @Transactional
    public BookingResponseDTO checkIn(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingCode));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BadRequestException("Booking is not in CONFIRMED status");
        }

        booking.setStatus(BookingStatus.CHECKED_IN);
        bookingRepository.save(booking);

        booking.getRoom().setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(booking.getRoom());

        emailService.sendCheckedInNotice(booking.getGuest(), booking);
        return toDTO(booking);
    }


    @Transactional
    public BookingResponseDTO checkOut(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingCode));

        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new BadRequestException("Guest is not currently checked in");
        }

        booking.setStatus(BookingStatus.CHECKED_OUT);
        bookingRepository.save(booking);

        booking.getRoom().setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(booking.getRoom());

        emailService.sendCheckOutReminder(booking.getGuest(), booking);
        return toDTO(booking);
    }


    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public BookingResponseDTO getByBookingCode(String code) {
        return toDTO(bookingRepository.findByBookingCode(code)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + code)));
    }


    private BookingResponseDTO toDTO(Booking booking) {
        UserDTO guestDTO = UserDTO.builder()
                .id(booking.getGuest().getId())
                .fullName(booking.getGuest().getFullName())
                .username(booking.getGuest().getUsername())
                .email(booking.getGuest().getEmail())
                .phoneNumber(booking.getGuest().getPhoneNumber())
                .userType(booking.getGuest().getUserType())
                .build();

        RoomDTO roomDTO = roomService.toDTO(booking.getRoom());

        return BookingResponseDTO.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .guest(guestDTO)
                .room(roomDTO)
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .numberOfUnits(booking.getNumberOfUnits())
                .numberOfNights(booking.getNumberOfNights())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .build();
    }
}
