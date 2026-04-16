package com.flipkart.dto;
import jakarta.validation.constraints.*;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor
public class UpdateUserRequest {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    private String phone;
    private String profileImage;
}
