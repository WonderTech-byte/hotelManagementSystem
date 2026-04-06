package org.sammy.hotelmanagement.image;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByRoomIdOrderByDisplayOrderAsc(Long roomId);
    Optional<Image> findByRoomIdAndIsPrimaryTrue(Long roomId);

}
