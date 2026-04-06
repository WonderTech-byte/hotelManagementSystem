package org.sammy.hotelmanagement.image;

import lombok.RequiredArgsConstructor;
import org.sammy.hotelmanagement.dto.AddImageDTO;
import org.sammy.hotelmanagement.dto.ImageDTO;
import org.sammy.hotelmanagement.room.Room;
import org.sammy.hotelmanagement.room.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final RoomRepository roomRepository;


    @Transactional
    public ImageDTO addImageToRoom(Long roomId, AddImageDTO dto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));

        if (dto.isPrimary) {
            imageRepository.findByRoomIdAndIsPrimaryTrue(roomId)
                    .ifPresent(existing -> {
                        existing.setPrimary(false);
                        imageRepository.save(existing);
                    });
        }

        Image image = Image.builder()
                .url(dto.url)
                .altText(dto.altText)
                .isPrimary(dto.isPrimary)
                .displayOrder(dto.displayOrder)
                .room(room)
                .build();

        return toDTO(imageRepository.save(image));
    }



    public List<ImageDTO> getImagesByRoom(Long roomId) {
        return imageRepository.findByRoomIdOrderByDisplayOrderAsc(roomId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }



    @Transactional
    public void deleteImage(Long imageId) {
        if (!imageRepository.existsById(imageId)) {
            throw new RuntimeException("Image not found: " + imageId);
        }
        imageRepository.deleteById(imageId);
    }


    @Transactional
    public ImageDTO setPrimary(Long roomId, Long imageId) {

        imageRepository.findByRoomIdAndIsPrimaryTrue(roomId)
                .ifPresent(existing -> {
                    existing.setPrimary(false);
                    imageRepository.save(existing);
                });

        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found: " + imageId));
        image.setPrimary(true);
        return toDTO(imageRepository.save(image));
    }



    public ImageDTO toDTO(Image image) {
        return ImageDTO.builder()
                .id(image.getId())
                .url(image.getUrl())
                .altText(image.getAltText())
                .isPrimary(image.isPrimary())
                .displayOrder(image.getDisplayOrder())
                .build();
    }
}
