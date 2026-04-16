package com.flipkart.service;

import com.flipkart.dto.*;
import com.flipkart.exception.*;
import com.flipkart.model.*;
import com.flipkart.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final ProductService productService;

    public List<ProductResponse> getWishlist() {
        User user = userService.getCurrentUser();
        return wishlistRepository.findByUserIdOrderByAddedAtDesc(user.getId())
                .stream().map(w -> productService.mapToResponse(w.getProduct()))
                .collect(Collectors.toList());
    }

    @Transactional
    public String toggleWishlist(Long productId) {
        User user = userService.getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        if (wishlistRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            wishlistRepository.deleteByUserIdAndProductId(user.getId(), productId);
            return "removed";
        } else {
            Wishlist wishlist = Wishlist.builder().user(user).product(product).build();
            wishlistRepository.save(wishlist);
            return "added";
        }
    }

    public boolean isInWishlist(Long productId) {
        try {
            User user = userService.getCurrentUser();
            return wishlistRepository.existsByUserIdAndProductId(user.getId(), productId);
        } catch (Exception e) {
            return false;
        }
    }
}
