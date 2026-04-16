package com.flipkart.dto;
import jakarta.validation.constraints.*;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoryRequest {
    @NotBlank private String name;
    private String description;
    private String imageUrl;
    private String icon;
    private String slug;
    private Long parentId;
    private Integer sortOrder;
}
