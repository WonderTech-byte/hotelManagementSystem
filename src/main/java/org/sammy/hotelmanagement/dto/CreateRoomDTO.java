package org.sammy.hotelmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.sammy.hotelmanagement.room.PriceType;
import org.sammy.hotelmanagement.room.RoomType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CreateRoomDTO {
    @NotBlank public String roomNumber;

    @NotNull public RoomType roomType;

    @Positive public double price;

    @NotNull public PriceType priceType;

    public String description;

    public List<MultipartFile> imageFiles;
    public String features;
}
