package org.sammy.hotelmanagement.feature;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomFeatureRepository extends JpaRepository<RoomFeature, Long> {
    List<RoomFeature> findByRoomIdOrderByDisplayOrderAsc(Long roomId);
    boolean existsByRoomIdAndFeatureTypeId(Long roomId, Long featureTypeId);
}
