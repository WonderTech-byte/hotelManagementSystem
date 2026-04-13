package org.sammy.hotelmanagement.image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.sammy.hotelmanagement.exception.BadRequestException;
import org.sammy.hotelmanagement.room.dto.AddImageDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final Cloudinary cloudinary;

    public String uploadRoomImage(Long roomId, AddImageDTO dto) {
        return uploadImage("hotel/room_" + roomId, dto.getFile());
    }

    public String uploadRoomImage(Long roomId, MultipartFile file) {
        return uploadImage("hotel/room_" + roomId, file);
    }

    public String uploadRoomFeatureImage(Long roomId, String featureName, MultipartFile file) {
        return uploadImage("hotel/room_" + roomId + "/features/" + normalizeFolderSegment(featureName), file);
    }

    private String uploadImage(String folder, MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new BadRequestException("Please select a file to upload");
            }

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", folder));

            Object secureUrl = uploadResult.get("secure_url");
            String generatedUrl = secureUrl == null ? null : secureUrl.toString();
            if (generatedUrl == null || generatedUrl.isBlank()) {
                throw new BadRequestException("Cloudinary did not return an image URL");
            }
            return generatedUrl;
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload image to Cloudinary");
        }
    }

    private String normalizeFolderSegment(String value) {
        if (value == null || value.isBlank()) {
            return "general";
        }
        return value.trim().toLowerCase().replaceAll("[^a-z0-9]+", "_");
    }
}
