package org.sammy.hotelmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddImageDTO {
    @NotBlank public String url;
    public String altText;
    public boolean isPrimary = false;
    public int displayOrder = 0;
}
