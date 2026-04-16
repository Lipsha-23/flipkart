package com.flipkart.dto;
import lombok.*;
import java.util.List;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String icon;
    private String slug;
    private Long parentId;
    private String parentName;
    private List<CategoryResponse> subcategories;
    private Integer sortOrder;
    private boolean active;
}
