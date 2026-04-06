package org.sammy.hotelmanagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddRoomFeatureDTO {
    @NotNull public Long featureTypeId;
    public String customDescription;
    public int displayOrder = 0;
}
