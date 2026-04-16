package com.flipkart.dto;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;
@Data @NoArgsConstructor @AllArgsConstructor
public class ReviewRequest {
    @NotNull private Long productId;
    @Min(1) @Max(5) @NotNull private Integer rating;
    @Size(max=100) private String title;
    private String comment;
    private List<String> images;
}
