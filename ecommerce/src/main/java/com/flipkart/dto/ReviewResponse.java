package com.flipkart.dto;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReviewResponse {
    private Long id;
    private Long productId;
    private UserResponse user;
    private Integer rating;
    private String title;
    private String comment;
    private List<String> images;
    private boolean verified;
    private int helpfulCount;
    private LocalDateTime createdAt;
}
