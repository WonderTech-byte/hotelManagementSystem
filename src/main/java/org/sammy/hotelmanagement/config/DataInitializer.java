package org.sammy.hotelmanagement.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sammy.hotelmanagement.room.*;
import org.sammy.hotelmanagement.user.User;
import org.sammy.hotelmanagement.user.UserRepository;
import org.sammy.hotelmanagement.user.UserType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        clearData();
        seedAdminUser();
        seedRooms();
    }

    private void clearData() {
        log.info("Clearing existing data before seeding...");
        // Order matters due to foreign key constraints if they exist
        // Currently Room has RoomFeature and image_urls which are CascadeType.ALL or ElementCollection
        // User has no direct relations that would block deletion here
        roomRepository.deleteAll();
        // userRepository.deleteAll(); // Optionally clear users too, but maybe only if you want a full reset
        // To be safe and clear everything as requested:
        userRepository.deleteAll();
        log.info("Data cleared.");
    }

    private void seedAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .fullName("System Administrator")
                    .username("admin")
                    .email("admin@hotel.com")
                    .password(passwordEncoder.encode("admin123"))
                    .phoneNumber("1234567890")
                    .userType(UserType.ADMIN)
                    .isActive(true)
                    .build();
            userRepository.save(admin);
            log.info("Admin user seeded.");
        }
    }

    private void seedRooms() {
        if (roomRepository.count() < 30) {
            List<Room> rooms = new ArrayList<>();

            // Image lists for different room types
            List<String> singleRoomImages = List.of(
                    "https://images.unsplash.com/photo-1631049307264-da0ec9d70304",
                    "https://images.unsplash.com/photo-1598928506311-c55ded91a20c",
                    "https://images.unsplash.com/photo-1505691938895-1758d7eaa511",
                    "https://images.unsplash.com/photo-1540518614846-7eded433c457",
                    "https://images.unsplash.com/photo-1566665797739-1674de7a421a",
                    "https://images.unsplash.com/photo-1595576508898-0ad5c879a061",
                    "https://images.unsplash.com/photo-1591088398332-8a7701972843",
                    "https://images.unsplash.com/photo-1512918766671-ad650b9b73ad",
                    "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b",
                    "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6"
            );

            List<String> doubleRoomImages = List.of(
                    "https://images.unsplash.com/photo-1590490360182-c33d57733427",
                    "https://images.unsplash.com/photo-1566195992011-5f6b21e539aa",
                    "https://images.unsplash.com/photo-1578683010236-d716f9a3f461",
                    "https://images.unsplash.com/photo-1590490359683-658d3d23f972",
                    "https://images.unsplash.com/photo-1568494106960-385506128897",
                    "https://images.unsplash.com/photo-1611892440504-42a792e24d32",
                    "https://images.unsplash.com/photo-1584132967334-10e028bd69f7",
                    "https://images.unsplash.com/photo-1566195992011-5f6b21e539aa",
                    "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af",
                    "https://images.unsplash.com/photo-1560185007-cde436f6a4d0"
            );

            List<String> suiteRoomImages = List.of(
                    "https://images.unsplash.com/photo-1590490360182-c33d57733427",
                    "https://images.unsplash.com/photo-1618773928121-c32242e63f39",
                    "https://images.unsplash.com/photo-1592229505726-ca121723b8ea",
                    "https://images.unsplash.com/photo-1591088398332-8a7701972843",
                    "https://images.unsplash.com/photo-1582719508461-905c673771fd",
                    "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6",
                    "https://images.unsplash.com/photo-1560448204-603b3fc33ddc",
                    "https://images.unsplash.com/photo-1551882547-ff43c6395c24",
                    "https://images.unsplash.com/photo-1631049552057-403cdb8f0658",
                    "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b"
            );

            for (int i = 1; i <= 30; i++) {
                String roomNumber = String.format("%03d", i);
                if (!roomRepository.existsByRoomNumber(roomNumber)) {
                    RoomType type;
                    double price;
                    String imageUrl;

                    if (i <= 10) {
                        type = RoomType.SINGLE;
                        price = 100.0;
                        imageUrl = singleRoomImages.get((i - 1) % singleRoomImages.size());
                    } else if (i <= 20) {
                        type = RoomType.DOUBLE;
                        price = 180.0;
                        imageUrl = doubleRoomImages.get((i - 11) % doubleRoomImages.size());
                    } else {
                        type = RoomType.SUITE;
                        price = 350.0;
                        imageUrl = suiteRoomImages.get((i - 21) % suiteRoomImages.size());
                    }

                    Room room = Room.builder()
                            .roomNumber(roomNumber)
                            .roomType(type)
                            .price(price)
                            .priceType(PriceType.PER_NIGHT)
                            .status(RoomStatus.AVAILABLE)
                            .description("Comfortable " + type + " room number " + roomNumber)
                            .imageUrls(List.of(imageUrl))
                            .build();
                    rooms.add(room);
                }
            }
            if (!rooms.isEmpty()) {
                roomRepository.saveAll(rooms);
                log.info("{} rooms seeded.", rooms.size());
            }
        }
    }
}
