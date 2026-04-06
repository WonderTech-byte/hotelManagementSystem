package org.sammy.hotelmanagement.room;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sammy.hotelmanagement.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;


    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomDTO>> getAvailableRooms() {
        return ResponseEntity.ok(roomService.getAvailableRooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }


    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@Valid @RequestBody CreateRoomDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDTO> updateRoom(
            @PathVariable Long id,
            @RequestBody UpdateRoomDTO dto) {
        return ResponseEntity.ok(roomService.updateRoom(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{roomId}/images")
    public ResponseEntity<ImageDTO> addImage(
            @PathVariable Long roomId,
            @Valid @RequestBody AddImageDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.addImage(roomId, dto));
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        roomService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{roomId}/features")
    public ResponseEntity<RoomFeatureDTO> addFeature(
            @PathVariable Long roomId,
            @Valid @RequestBody AddRoomFeatureDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roomService.addFeature(roomId, dto));
    }
}
