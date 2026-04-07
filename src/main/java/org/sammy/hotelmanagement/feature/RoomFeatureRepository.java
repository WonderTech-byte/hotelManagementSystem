package org.sammy.hotelmanagement.feature;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomFeatureRepository extends JpaRepository<RoomFeature, Long> {
    List<RoomFeature> findByRoomId(Long roomId);

    @Query("""
            select count(rf) > 0
            from RoomFeature rf
            where rf.room.id = :roomId
              and lower(rf.name) = lower(:roomFeature)
            """)
    boolean existsByRoomAndNameIgnoreCase(@Param("roomId") Long roomId,
                                          @Param("roomFeature") String roomFeature);

}
