package com.flipkart.dto;
import com.flipkart.model.Address;
import jakarta.validation.constraints.*;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor
public class AddressRequest {
    @NotBlank private String fullName;
    @NotBlank private String phone;
    @NotBlank private String addressLine1;
    private String addressLine2;
    @NotBlank private String city;
    @NotBlank private String state;
    @NotBlank private String pincode;
    private String country = "India";
    private Address.AddressType type = Address.AddressType.HOME;
    private boolean isDefault;
}
