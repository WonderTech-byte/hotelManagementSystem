package org.sammy.hotelmanagement.room;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sammy.hotelmanagement.room.dto.*;
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


    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<RoomDTO> createRoom(@Valid CreateRoomDTO dto) {
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


    @PostMapping(value = "/{roomId}/images", consumes = {"multipart/form-data"})
    public ResponseEntity<RoomDTO> addImage(
            @PathVariable Long roomId,
            @Valid AddImageDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.addImage(roomId, dto));
    }

    @DeleteMapping("/{roomId}/images")
    public ResponseEntity<RoomDTO> deleteImage(
            @PathVariable Long roomId,
            @RequestParam String imageUrl) {
        return ResponseEntity.ok(roomService.deleteImage(roomId, imageUrl));
    }


    @PostMapping(value = "/{roomId}/features", consumes = {"multipart/form-data"})
    public ResponseEntity<RoomFeatureDTO> addFeature(
            @PathVariable Long roomId,
            @Valid AddRoomFeatureDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roomService.addFeature(roomId, dto));
    }
}
