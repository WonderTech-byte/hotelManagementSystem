package org.sammy.hotelmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateFeatureTypeDTO {
    @NotBlank public String name;
    @NotNull public Long categoryId;
}
