package com.flipkart.service;

import com.flipkart.dto.*;
import com.flipkart.exception.*;
import com.flipkart.model.*;
import com.flipkart.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;

    public List<AddressResponse> getUserAddresses() {
        User user = userService.getCurrentUser();
        return addressRepository.findByUserId(user.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse addAddress(AddressRequest request) {
        User user = userService.getCurrentUser();

        if (request.isDefault()) {
            // Unset other defaults
            addressRepository.findByUserIdAndIsDefaultTrue(user.getId()).ifPresent(a -> {
                a.setDefault(false);
                addressRepository.save(a);
            });
        }

        Address address = Address.builder()
                .user(user).fullName(request.getFullName()).phone(request.getPhone())
                .addressLine1(request.getAddressLine1()).addressLine2(request.getAddressLine2())
                .city(request.getCity()).state(request.getState())
                .pincode(request.getPincode())
                .country(request.getCountry() != null ? request.getCountry() : "India")
                .type(request.getType() != null ? request.getType() : Address.AddressType.HOME)
                .isDefault(request.isDefault())
                .build();

        return mapToResponse(addressRepository.save(address));
    }

    @Transactional
    public AddressResponse updateAddress(Long id, AddressRequest request) {
        User user = userService.getCurrentUser();
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", id));
        if (!address.getUser().getId().equals(user.getId())) throw new UnauthorizedException("Not your address");

        address.setFullName(request.getFullName()); address.setPhone(request.getPhone());
        address.setAddressLine1(request.getAddressLine1()); address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity()); address.setState(request.getState());
        address.setPincode(request.getPincode());
        if (request.getCountry() != null) address.setCountry(request.getCountry());
        if (request.getType() != null) address.setType(request.getType());

        if (request.isDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(user.getId()).ifPresent(a -> {
                if (!a.getId().equals(id)) { a.setDefault(false); addressRepository.save(a); }
            });
            address.setDefault(true);
        }

        return mapToResponse(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(Long id) {
        User user = userService.getCurrentUser();
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", id));
        if (!address.getUser().getId().equals(user.getId())) throw new UnauthorizedException("Not your address");
        addressRepository.delete(address);
    }

    public AddressResponse mapToResponse(Address a) {
        return AddressResponse.builder()
                .id(a.getId()).fullName(a.getFullName()).phone(a.getPhone())
                .addressLine1(a.getAddressLine1()).addressLine2(a.getAddressLine2())
                .city(a.getCity()).state(a.getState()).pincode(a.getPincode()).country(a.getCountry())
                .type(a.getType() != null ? a.getType().name() : null).isDefault(a.isDefault()).build();
    }
}
