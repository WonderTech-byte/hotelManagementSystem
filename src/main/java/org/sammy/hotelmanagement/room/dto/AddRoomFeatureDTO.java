package org.sammy.hotelmanagement.room.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AddRoomFeatureDTO {
    @NotBlank public String roomFeature;
    public String description;
    public MultipartFile imageFile;
}
