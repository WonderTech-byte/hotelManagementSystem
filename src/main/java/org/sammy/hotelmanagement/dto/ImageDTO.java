package org.sammy.hotelmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO {
    public Long id;
    public String url;
    public String altText;
    public boolean isPrimary;
    public int displayOrder;
}
