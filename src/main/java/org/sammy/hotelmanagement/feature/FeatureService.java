package org.sammy.hotelmanagement.feature;

import lombok.RequiredArgsConstructor;
import org.sammy.hotelmanagement.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeatureService {

    private final FeatureCategoryRepository categoryRepository;
    private final FeatureTypeRepository typeRepository;



    public FeatureCategoryDTO createCategory(CreateFeatureCategoryDTO dto) {
        if (categoryRepository.existsByName(dto.name)) {
            throw new RuntimeException("Category already exists: " + dto.name);
        }
        FeatureCategory saved = categoryRepository.save(
                FeatureCategory.builder()
                        .name(dto.name)
                        .icon(dto.icon)
                        .build()
        );
        return toCategoryDTO(saved);
    }

    public List<FeatureCategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream().map(this::toCategoryDTO).collect(Collectors.toList());
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    // ─── Feature Types ────────────────────────────

    public FeatureTypeDTO createFeatureType(CreateFeatureTypeDTO dto) {
        if (typeRepository.existsByName(dto.name)) {
            throw new RuntimeException("Feature type already exists: " + dto.name);
        }
        FeatureCategory category = categoryRepository.findById(dto.categoryId)
                .orElseThrow(() -> new RuntimeException(
                        "Category not found: " + dto.categoryId));

        FeatureType saved = typeRepository.save(
                FeatureType.builder()
                        .name(dto.name)
                        .category(category)
                        .build()
        );
        return toTypeDTO(saved);
    }

    public List<FeatureTypeDTO> getAllFeatureTypes() {
        return typeRepository.findAll()
                .stream().map(this::toTypeDTO).collect(Collectors.toList());
    }

    public List<FeatureTypeDTO> getFeatureTypesByCategory(Long categoryId) {
        return typeRepository.findByCategoryId(categoryId)
                .stream().map(this::toTypeDTO).collect(Collectors.toList());
    }

    public void deleteFeatureType(Long id) {
        typeRepository.deleteById(id);
    }

    // ─── Mappers ──────────────────────────────────

    public FeatureCategoryDTO toCategoryDTO(FeatureCategory c) {
        return FeatureCategoryDTO.builder()
                .id(c.getId()).name(c.getName()).icon(c.getIcon()).build();
    }

    public FeatureTypeDTO toTypeDTO(FeatureType t) {
        return FeatureTypeDTO.builder()
                .id(t.getId())
                .name(t.getName())
                .category(toCategoryDTO(t.getCategory()))
                .build();
    }
}
