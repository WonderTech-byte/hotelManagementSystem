package org.sammy.hotelmanagement.feature;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeatureCategoryRepository extends JpaRepository<FeatureCategory, Long> {
    boolean existsByName(String name);
}
