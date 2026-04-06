package org.sammy.hotelmanagement.image;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sammy.hotelmanagement.feature.RoomFeature;
import org.sammy.hotelmanagement.room.Room;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    private String altText;

    @Column(nullable = false)
    private boolean isPrimary = false;


    @Column(nullable = false)
    private int displayOrder = 0;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_feature_id")
    private RoomFeature roomFeature;
}
