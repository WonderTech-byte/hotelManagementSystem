package org.sammy.hotelmanagement.room.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sammy.hotelmanagement.room.PriceType;
import org.sammy.hotelmanagement.room.RoomStatus;
import org.sammy.hotelmanagement.room.RoomType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    public Long id;
    public String roomNumber;
    public RoomType roomType;
    public double price;
    public PriceType priceType;
    public RoomStatus status;
    public String description;
    public LocalDateTime createdAt;
    public List<RoomFeatureDTO> features;
    public List<String> imageUrls;
}
