package com.flipkart.repository;

import com.flipkart.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();
    List<Category> findByParentId(Long parentId);
    Optional<Category> findBySlug(String slug);
    List<Category> findByActiveTrue();
    Optional<Category> findByName(String name);
}
