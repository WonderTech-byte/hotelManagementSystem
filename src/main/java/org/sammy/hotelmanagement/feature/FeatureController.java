package org.sammy.hotelmanagement.feature;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sammy.hotelmanagement.dto.CreateFeatureCategoryDTO;
import org.sammy.hotelmanagement.dto.CreateFeatureTypeDTO;
import org.sammy.hotelmanagement.dto.FeatureCategoryDTO;
import org.sammy.hotelmanagement.dto.FeatureTypeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/features")
@RequiredArgsConstructor
public class FeatureController {

    private final FeatureService featureService;


    @PostMapping("/categories")
    public ResponseEntity<FeatureCategoryDTO> createCategory(
            @Valid @RequestBody CreateFeatureCategoryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(featureService.createCategory(dto));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<FeatureCategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(featureService.getAllCategories());
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        featureService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }



    @PostMapping("/types")
    public ResponseEntity<FeatureTypeDTO> createFeatureType(
            @Valid @RequestBody CreateFeatureTypeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(featureService.createFeatureType(dto));
    }

    @GetMapping("/types")
    public ResponseEntity<List<FeatureTypeDTO>> getAllFeatureTypes() {
        return ResponseEntity.ok(featureService.getAllFeatureTypes());
    }

    @GetMapping("/types/category/{categoryId}")
    public ResponseEntity<List<FeatureTypeDTO>> getByCategory(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(featureService.getFeatureTypesByCategory(categoryId));
    }

    @DeleteMapping("/types/{id}")
    public ResponseEntity<Void> deleteFeatureType(@PathVariable Long id) {
        featureService.deleteFeatureType(id);
        return ResponseEntity.noContent().build();
    }
}
