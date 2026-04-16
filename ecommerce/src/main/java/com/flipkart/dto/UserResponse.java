package com.flipkart.dto;
import lombok.*;
import java.time.LocalDateTime;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role;
    private String profileImage;
    private boolean emailVerified;
    private LocalDateTime createdAt;
    public String getFullName() { return firstName + " " + lastName; }
}
