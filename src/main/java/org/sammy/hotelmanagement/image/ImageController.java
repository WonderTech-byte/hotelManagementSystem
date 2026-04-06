package org.sammy.hotelmanagement.image;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sammy.hotelmanagement.dto.AddImageDTO;
import org.sammy.hotelmanagement.dto.ImageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;


    @PostMapping("/room/{roomId}")
    public ResponseEntity<ImageDTO> addImageToRoom(
            @PathVariable Long roomId,
            @Valid @RequestBody AddImageDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(imageService.addImageToRoom(roomId, dto));
    }


    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ImageDTO>> getImagesByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(imageService.getImagesByRoom(roomId));
    }


    @PatchMapping("/room/{roomId}/primary/{imageId}")
    public ResponseEntity<ImageDTO> setPrimary(
            @PathVariable Long roomId,
            @PathVariable Long imageId) {
        return ResponseEntity.ok(imageService.setPrimary(roomId, imageId));
    }


    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        imageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }
}
