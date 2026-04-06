package org.sammy.hotelmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateFeatureCategoryDTO {
    @NotBlank public String name;
    public String icon;
}
