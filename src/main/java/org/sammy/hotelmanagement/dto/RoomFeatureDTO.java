package org.sammy.hotelmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomFeatureDTO {
    public Long id;
    public FeatureTypeDTO featureType;
    public String customDescription;
    public int displayOrder;
}
