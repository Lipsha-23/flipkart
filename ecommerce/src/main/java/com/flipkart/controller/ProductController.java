package com.flipkart.controller;

import com.flipkart.dto.*;
import com.flipkart.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product browsing, search, and management")
public class ProductController {

    private final ProductService productService;

    // ─── PUBLIC ENDPOINTS ───────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Get all products (paginated)")
    public ApiResponse<PageResponse<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "newest") String sortBy) {
        return ApiResponse.success(productService.getAllProducts(page, size, sortBy));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ApiResponse<ProductResponse> getProduct(@PathVariable Long id) {
        return ApiResponse.success(productService.getProductById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by name or brand")
    public ApiResponse<PageResponse<ProductResponse>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "newest") String sortBy) {
        return ApiResponse.success(productService.searchProducts(q, page, size, sortBy));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category")
    public ApiResponse<PageResponse<ProductResponse>> getByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "newest") String sortBy) {
        return ApiResponse.success(productService.getProductsByCategory(categoryId, page, size, sortBy));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter products by price, brand, rating etc.")
    public ApiResponse<PageResponse<ProductResponse>> filter(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String brand,
            @RequestParam(defaultValue = "newest") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        ProductFilterRequest filter = ProductFilterRequest.builder()
                .categoryId(categoryId).minPrice(minPrice).maxPrice(maxPrice)
                .brand(brand).sortBy(sortBy).page(page).size(size).build();
        return ApiResponse.success(productService.filterProducts(filter));
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured products")
    public ApiResponse<List<ProductResponse>> getFeatured() {
        return ApiResponse.success(productService.getFeaturedProducts());
    }

    @GetMapping("/best-sellers")
    @Operation(summary = "Get best-selling products")
    public ApiResponse<List<ProductResponse>> getBestSellers() {
        return ApiResponse.success(productService.getBestSellerProducts());
    }

    @GetMapping("/new-arrivals")
    @Operation(summary = "Get new arrival products")
    public ApiResponse<List<ProductResponse>> getNewArrivals() {
        return ApiResponse.success(productService.getNewArrivals());
    }

    @GetMapping("/category/{categoryId}/brands")
    @Operation(summary = "Get all brands in a category")
    public ApiResponse<List<String>> getBrands(@PathVariable Long categoryId) {
        return ApiResponse.success(productService.getBrandsByCategory(categoryId));
    }

    // ─── SELLER / ADMIN ENDPOINTS ────────────────────────────────────────────

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Create a new product (Seller/Admin)")
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ApiResponse.success(productService.createProduct(request), "Product created successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Update a product (Seller/Admin)")
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ApiResponse.success(productService.updateProduct(id, request), "Product updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Delete (deactivate) a product (Seller/Admin)")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success(null, "Product deleted successfully");
    }
}
