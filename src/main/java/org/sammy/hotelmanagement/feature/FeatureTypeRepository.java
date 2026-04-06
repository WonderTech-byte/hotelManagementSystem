package org.sammy.hotelmanagement.feature;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeatureTypeRepository extends JpaRepository<FeatureType, Long> {
    Optional<FeatureType> findByName(String name);
    List<FeatureType> findByCategory(FeatureCategory category);
    List<FeatureType> findByCategoryId(Long categoryId);
    boolean existsByName(String name);
}
