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
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    public PageResponse<ReviewResponse> getProductReviews(Long productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable);
        return PageResponse.<ReviewResponse>builder()
                .content(reviews.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .page(reviews.getNumber()).size(reviews.getSize())
                .totalElements(reviews.getTotalElements()).totalPages(reviews.getTotalPages())
                .first(reviews.isFirst()).last(reviews.isLast()).build();
    }

    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        User user = userService.getCurrentUser();
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId()));

        if (reviewRepository.existsByUserIdAndProductId(user.getId(), product.getId())) {
            throw new BadRequestException("You have already reviewed this product");
        }

        Review review = Review.builder()
                .user(user).product(product)
                .rating(request.getRating()).title(request.getTitle())
                .comment(request.getComment())
                .build();
        if (request.getImages() != null) review.setImages(request.getImages());

        review = reviewRepository.save(review);
        updateProductRating(product);
        return mapToResponse(review);
    }

    @Transactional
    public void deleteReview(Long id) {
        User user = userService.getCurrentUser();
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", id));
        if (!review.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new UnauthorizedException("Cannot delete this review");
        }
        Product product = review.getProduct();
        reviewRepository.delete(review);
        updateProductRating(product);
    }

    private void updateProductRating(Product product) {
        Double avg = reviewRepository.getAverageRating(product.getId());
        Long count = reviewRepository.countByProductId(product.getId());
        product.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        product.setTotalReviews(count != null ? count.intValue() : 0);
        productRepository.save(product);
    }

    public ReviewResponse mapToResponse(Review r) {
        return ReviewResponse.builder()
                .id(r.getId()).productId(r.getProduct().getId())
                .user(UserResponse.builder().id(r.getUser().getId())
                        .firstName(r.getUser().getFirstName()).lastName(r.getUser().getLastName()).build())
                .rating(r.getRating()).title(r.getTitle()).comment(r.getComment())
                .images(r.getImages()).verified(r.isVerified()).helpfulCount(r.getHelpfulCount())
                .createdAt(r.getCreatedAt()).build();
    }
}
