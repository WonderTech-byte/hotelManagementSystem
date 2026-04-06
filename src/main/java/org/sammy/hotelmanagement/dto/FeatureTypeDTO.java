package org.sammy.hotelmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureTypeDTO {
    public Long id;
    public String name;
    public FeatureCategoryDTO category;
}
