package com.flipkart.service;

import com.flipkart.dto.*;
import com.flipkart.exception.*;
import com.flipkart.model.*;
import com.flipkart.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final CategoryService categoryService;

    public PageResponse<ProductResponse> getAllProducts(int page, int size, String sortBy) {
        Pageable pageable = buildPageable(page, size, sortBy);
        Page<Product> products = productRepository.findAll(pageable);
        return mapToPageResponse(products);
    }

    public PageResponse<ProductResponse> searchProducts(String query, int page, int size, String sortBy) {
        Pageable pageable = buildPageable(page, size, sortBy);
        Page<Product> products = productRepository.searchProducts(query, pageable);
        return mapToPageResponse(products);
    }

    public PageResponse<ProductResponse> getProductsByCategory(Long categoryId, int page, int size, String sortBy) {
        Pageable pageable = buildPageable(page, size, sortBy);
        Page<Product> products = productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable);
        return mapToPageResponse(products);
    }

    public PageResponse<ProductResponse> filterProducts(ProductFilterRequest filter) {
        Pageable pageable = buildPageable(filter.getPage(), filter.getSize(), filter.getSortBy());
        Page<Product> products = productRepository.findWithFilters(
                filter.getCategoryId(),
                filter.getMinPrice(),
                filter.getMaxPrice(),
                filter.getBrand(),
                pageable
        );
        return mapToPageResponse(products);
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        if (!product.isActive()) throw new ResourceNotFoundException("Product", id);
        return mapToResponse(product);
    }

    public List<ProductResponse> getFeaturedProducts() {
        return productRepository.findByFeaturedTrueAndActiveTrue(PageRequest.of(0, 12))
                .getContent().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<ProductResponse> getBestSellerProducts() {
        return productRepository.findTop10ByActiveTrueOrderBySoldCountDesc()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<ProductResponse> getNewArrivals() {
        return productRepository.findTop8ByActiveTrueOrderByCreatedAtDesc()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<String> getBrandsByCategory(Long categoryId) {
        return productRepository.findBrandsByCategoryId(categoryId);
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        User seller = userService.getCurrentUser();
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .originalPrice(request.getOriginalPrice())
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .category(category)
                .seller(seller)
                .brand(request.getBrand())
                .sku(request.getSku())
                .featured(request.isFeatured())
                .build();

        if (request.getImages() != null) product.setImages(request.getImages());
        if (request.getSpecifications() != null) product.setSpecifications(request.getSpecifications());

        return mapToResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        User currentUser = userService.getCurrentUser();
        if (!product.getSeller().getId().equals(currentUser.getId())
                && currentUser.getRole() != User.Role.ADMIN) {
            throw new UnauthorizedException("You don't have permission to update this product");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);
        product.setBrand(request.getBrand());
        product.setSku(request.getSku());
        product.setFeatured(request.isFeatured());
        if (request.getImages() != null) product.setImages(request.getImages());
        if (request.getSpecifications() != null) product.setSpecifications(request.getSpecifications());

        return mapToResponse(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        product.setActive(false);
        productRepository.save(product);
    }

    public void updateProductRating(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        // Rating update is handled in ReviewService
        productRepository.save(product);
    }

    private Pageable buildPageable(int page, int size, String sortBy) {
        Sort sort = switch (sortBy != null ? sortBy : "newest") {
            case "price_asc" -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            case "rating" -> Sort.by("averageRating").descending();
            case "popular" -> Sort.by("soldCount").descending();
            default -> Sort.by("createdAt").descending();
        };
        return PageRequest.of(page, Math.min(size, 100), sort);
    }

    private PageResponse<ProductResponse> mapToPageResponse(Page<Product> page) {
        return PageResponse.<ProductResponse>builder()
                .content(page.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    public ProductResponse mapToResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .originalPrice(p.getOriginalPrice())
                .discountPercentage(p.getDiscountPercentage())
                .stockQuantity(p.getStockQuantity())
                .category(p.getCategory() != null ? categoryService.mapToResponse(p.getCategory()) : null)
                .brand(p.getBrand())
                .images(p.getImages())
                .averageRating(p.getAverageRating())
                .totalReviews(p.getTotalReviews())
                .active(p.isActive())
                .featured(p.isFeatured())
                .specifications(p.getSpecifications())
                .sku(p.getSku())
                .soldCount(p.getSoldCount())
                .createdAt(p.getCreatedAt())
                .seller(p.getSeller() != null ? UserResponse.builder()
                        .id(p.getSeller().getId())
                        .firstName(p.getSeller().getFirstName())
                        .lastName(p.getSeller().getLastName())
                        .email(p.getSeller().getEmail())
                        .build() : null)
                .build();
    }
}
