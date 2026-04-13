package org.sammy.hotelmanagement.room.dto;

import lombok.Data;
import org.sammy.hotelmanagement.room.PriceType;
import org.sammy.hotelmanagement.room.RoomStatus;
import org.sammy.hotelmanagement.room.RoomType;

@Data
public class UpdateRoomDTO {
    public RoomType roomType;
    public Double price;
    public PriceType priceType;
    public RoomStatus status;
    public String description;
}
