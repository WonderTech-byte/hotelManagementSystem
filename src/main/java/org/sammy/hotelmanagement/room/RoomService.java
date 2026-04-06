package org.sammy.hotelmanagement.room;

import lombok.RequiredArgsConstructor;
import org.sammy.hotelmanagement.dto.*;
import org.sammy.hotelmanagement.feature.*;
import org.sammy.hotelmanagement.image.Image;
import org.sammy.hotelmanagement.image.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final FeatureTypeRepository featureTypeRepository;
    private final RoomFeatureRepository roomFeatureRepository;
    private final ImageRepository imageRepository;


    @Transactional
    public RoomDTO createRoom(CreateRoomDTO dto) {
        if (roomRepository.existsByRoomNumber(dto.roomNumber)) {
            throw new RuntimeException("Room number already exists: " + dto.roomNumber);
        }

        Room room = Room.builder()
                .roomNumber(dto.roomNumber)
                .roomType(dto.roomType)
                .price(dto.price)
                .priceType(dto.priceType)
                .description(dto.description)
                .status(RoomStatus.AVAILABLE)
                .build();

        roomRepository.save(room);

        if (dto.images != null) {
            for (AddImageDTO imgDto : dto.images) {
                Image image = Image.builder()
                        .url(imgDto.url)
                        .altText(imgDto.altText)
                        .isPrimary(imgDto.isPrimary)
                        .displayOrder(imgDto.displayOrder)
                        .room(room)
                        .build();
                imageRepository.save(image);
            }
        }

        if (dto.features != null) {
            for (AddRoomFeatureDTO featDto : dto.features) {
                FeatureType featureType = featureTypeRepository.findById(featDto.featureTypeId)
                        .orElseThrow(() -> new RuntimeException(
                                "FeatureType not found: " + featDto.featureTypeId));

                RoomFeature feature = RoomFeature.builder()
                        .room(room)
                        .featureType(featureType)
                        .customDescription(featDto.customDescription)
                        .displayOrder(featDto.displayOrder)
                        .build();
                roomFeatureRepository.save(feature);
            }
        }

        return toDTO(roomRepository.findById(room.getId()).orElseThrow());
    }



    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<RoomDTO> getAvailableRooms() {
        return roomRepository.findByStatus(RoomStatus.AVAILABLE)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public RoomDTO getRoomById(Long id) {
        return toDTO(roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found: " + id)));
    }



    @Transactional
    public RoomDTO updateRoom(Long id, UpdateRoomDTO dto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found: " + id));

        if (dto.roomType != null) room.setRoomType(dto.roomType);
        if (dto.price != null) room.setPrice(dto.price);
        if (dto.priceType != null) room.setPriceType(dto.priceType);
        if (dto.status != null) room.setStatus(dto.status);
        if (dto.description != null) room.setDescription(dto.description);

        return toDTO(roomRepository.save(room));
    }



    @Transactional
    public ImageDTO addImage(Long roomId, AddImageDTO dto) {
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

        Image saved = imageRepository.save(image);
        return toImageDTO(saved);
    }



    @Transactional
    public RoomFeatureDTO addFeature(Long roomId, AddRoomFeatureDTO dto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));

        FeatureType featureType = featureTypeRepository.findById(dto.featureTypeId)
                .orElseThrow(() -> new RuntimeException(
                        "FeatureType not found: " + dto.featureTypeId));

        if (roomFeatureRepository.existsByRoomIdAndFeatureTypeId(roomId, dto.featureTypeId)) {
            throw new RuntimeException("Feature already added to this room");
        }

        RoomFeature feature = RoomFeature.builder()
                .room(room)
                .featureType(featureType)
                .customDescription(dto.customDescription)
                .displayOrder(dto.displayOrder)
                .build();

        return toRoomFeatureDTO(roomFeatureRepository.save(feature));
    }



    @Transactional
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Room not found: " + id);
        }
        roomRepository.deleteById(id);
    }

    public void deleteImage(Long imageId) {
        imageRepository.deleteById(imageId);
    }



    public RoomDTO toDTO(Room room) {
        List<RoomFeatureDTO> features = roomFeatureRepository
                .findByRoomIdOrderByDisplayOrderAsc(room.getId())
                .stream().map(this::toRoomFeatureDTO).collect(Collectors.toList());

        List<ImageDTO> images = imageRepository
                .findByRoomIdOrderByDisplayOrderAsc(room.getId())
                .stream().map(this::toImageDTO).collect(Collectors.toList());

        return RoomDTO.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType())
                .price(room.getPrice())
                .priceType(room.getPriceType())
                .status(room.getStatus())
                .description(room.getDescription())
                .createdAt(room.getCreatedAt())
                .features(features)
                .images(images)
                .build();
    }

    private RoomFeatureDTO toRoomFeatureDTO(RoomFeature rf) {
        FeatureCategory cat = rf.getFeatureType().getCategory();
        FeatureCategoryDTO catDTO = FeatureCategoryDTO.builder()
                .id(cat.getId()).name(cat.getName()).icon(cat.getIcon()).build();

        FeatureTypeDTO typeDTO = FeatureTypeDTO.builder()
                .id(rf.getFeatureType().getId())
                .name(rf.getFeatureType().getName())
                .category(catDTO)
                .build();

        return RoomFeatureDTO.builder()
                .id(rf.getId())
                .featureType(typeDTO)
                .customDescription(rf.getCustomDescription())
                .displayOrder(rf.getDisplayOrder())
                .build();
    }

    private ImageDTO toImageDTO(Image img) {
        return ImageDTO.builder()
                .id(img.getId())
                .url(img.getUrl())
                .altText(img.getAltText())
                .isPrimary(img.isPrimary())
                .displayOrder(img.getDisplayOrder())
                .build();
    }
}
