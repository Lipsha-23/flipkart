package com.flipkart.repository;

import com.flipkart.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.*;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);
    Page<Product> findBySellerIdAndActiveTrue(Long sellerId, Pageable pageable);
    Page<Product> findByFeaturedTrueAndActiveTrue(Pageable pageable);
    long countByActiveTrue();

    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%',:q,'%')))")
    Page<Product> searchProducts(@Param("q") String query, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:brand IS NULL OR LOWER(p.brand) = LOWER(:brand))")
    Page<Product> findWithFilters(@Param("categoryId") Long categoryId,
                                   @Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   @Param("brand") String brand,
                                   Pageable pageable);

    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.category.id = :cid AND p.brand IS NOT NULL")
    List<String> findBrandsByCategoryId(@Param("cid") Long categoryId);

    List<Product> findTop10ByActiveTrueOrderBySoldCountDesc();
    List<Product> findTop8ByActiveTrueOrderByCreatedAtDesc();
}
