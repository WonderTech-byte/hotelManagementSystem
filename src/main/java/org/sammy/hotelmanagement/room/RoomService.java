package org.sammy.hotelmanagement.room;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.sammy.hotelmanagement.exception.BadRequestException;
import org.sammy.hotelmanagement.exception.ConflictException;
import org.sammy.hotelmanagement.exception.NotFoundException;
import org.sammy.hotelmanagement.room.dto.*;
import org.sammy.hotelmanagement.feature.*;
import org.sammy.hotelmanagement.image.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomFeatureRepository roomFeatureRepository;
    private final ImageService imageService;
    private final ObjectMapper objectMapper;
    private final Validator validator;


    @Transactional
    public RoomDTO createRoom(CreateRoomDTO dto) {
        if (roomRepository.existsByRoomNumber(dto.roomNumber)) {
            throw new ConflictException("Room number already exists: " + dto.roomNumber);
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

        if (dto.imageFiles != null) {
            for (MultipartFile imageFile : dto.imageFiles) {
                if (imageFile == null || imageFile.isEmpty()) {
                    continue;
                }
                room.getImageUrls().add(imageService.uploadRoomImage(room.getId(), imageFile));
            }
        }

        for (AddRoomFeatureDTO featDto : parseFeatures(dto.features)) {
            if (roomFeatureRepository.existsByRoomAndNameIgnoreCase(room.getId(), featDto.roomFeature)) {
                throw new ConflictException("Duplicate feature provided for this room: " + featDto.roomFeature);
            }

            RoomFeature feature = RoomFeature.builder()
                    .room(room)
                    .name(featDto.roomFeature)
                    .description(featDto.description)
                    .imageUrl(resolveFeatureImageUrl(room.getId(), featDto))
                    .build();
            roomFeatureRepository.save(feature);
        }

        return toDTO(roomRepository.save(room));
    }

    private List<AddRoomFeatureDTO> parseFeatures(String featuresJson) {
        if (featuresJson == null || featuresJson.isBlank()) {
            return List.of();
        }

        List<AddRoomFeatureDTO> features;
        try {
            features = objectMapper.readValue(featuresJson, new TypeReference<>() {});
        } catch (JsonProcessingException ex) {
            throw new BadRequestException("Invalid features JSON. Expected a JSON array of feature objects.");
        }

        if (features == null) {
            return List.of();
        }

        for (int i = 0; i < features.size(); i++) {
            AddRoomFeatureDTO feature = features.get(i);
            if (feature == null) {
                throw new BadRequestException("Invalid features JSON. Feature at index " + i + " must be an object.");
            }

            Set<ConstraintViolation<AddRoomFeatureDTO>> violations = validator.validate(feature);
            if (!violations.isEmpty()) {
                String message = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .sorted()
                        .collect(Collectors.joining(", "));
                throw new BadRequestException("Invalid feature at index " + i + ": " + message);
            }
        }

        return features;
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
                .orElseThrow(() -> new NotFoundException("Room not found: " + id)));
    }



    @Transactional
    public RoomDTO updateRoom(Long id, UpdateRoomDTO dto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found: " + id));

        if (dto.roomType != null) room.setRoomType(dto.roomType);
        if (dto.price != null) room.setPrice(dto.price);
        if (dto.priceType != null) room.setPriceType(dto.priceType);
        if (dto.status != null) room.setStatus(dto.status);
        if (dto.description != null) room.setDescription(dto.description);

        return toDTO(roomRepository.save(room));
    }



    @Transactional
    public RoomDTO addImage(Long roomId, AddImageDTO dto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found: " + roomId));

        String uploadedImageUrl = imageService.uploadRoomImage(roomId, dto);

        List<String> imageUrls = room.getImageUrls();
        if (imageUrls == null) {
            imageUrls = new ArrayList<>();
            room.setImageUrls(imageUrls);
        }
        if (!imageUrls.contains(uploadedImageUrl)) {
            imageUrls.add(uploadedImageUrl);
        }

        return toDTO(roomRepository.save(room));
    }



    @Transactional
    public RoomFeatureDTO addFeature(Long roomId, AddRoomFeatureDTO dto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found: " + roomId));

        if (roomFeatureRepository.existsByRoomAndNameIgnoreCase(roomId, dto.roomFeature)) {
            throw new ConflictException("Feature already added to this room");
        }

        RoomFeature feature = RoomFeature.builder()
                .room(room)
                .name(dto.roomFeature)
                .description(dto.description)
                .imageUrl(resolveFeatureImageUrl(roomId, dto))
                .build();

        return toRoomFeatureDTO(roomFeatureRepository.save(feature));
    }



    @Transactional
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new NotFoundException("Room not found: " + id);
        }
        roomRepository.deleteById(id);
    }

    @Transactional
    public RoomDTO deleteImage(Long roomId, String imageUrl) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found: " + roomId));
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new BadRequestException("Image URL is required");
        }

        boolean removed = room.getImageUrls().removeIf(existingUrl -> existingUrl.equals(imageUrl));
        if (!removed) {
            throw new NotFoundException("Image URL not found for room: " + roomId);
        }

        return toDTO(roomRepository.save(room));
    }



    public RoomDTO toDTO(Room room) {
        List<RoomFeatureDTO> features = roomFeatureRepository
                .findByRoomId(room.getId())
                .stream().map(this::toRoomFeatureDTO).collect(Collectors.toList());

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
                .imageUrls(room.getImageUrls() == null ? new ArrayList<>() : new ArrayList<>(room.getImageUrls()))
                .build();
    }

    private RoomFeatureDTO toRoomFeatureDTO(RoomFeature rf) {
        return RoomFeatureDTO.builder()
                .id(rf.getId())
                .roomFeature(rf.getName())
                .description(rf.getDescription())
                .imageUrl(rf.getImageUrl())
                .build();
    }

    private String resolveFeatureImageUrl(Long roomId, AddRoomFeatureDTO dto) {
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            return imageService.uploadRoomFeatureImage(roomId, dto.getRoomFeature(), dto.getImageFile());
        }
        return null;
    }
}
