package org.sammy.hotelmanagement.room.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AddImageDTO {
    @NotNull(message = "Please select a file to upload")
    private MultipartFile file;
}
