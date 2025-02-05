package com.jamith.booksformeapi.dto.requestDTO;

import lombok.Data;

import java.util.Date;

@Data
public class SellerSignUpDTO {
    private String sellerType;
    private String fullNameOrRepresentative;
    private String email;
    private String passwordHash;
    private String phoneNumber;
    private String addressStreet;
    private String addressCity;
    private String addressState;
    private String addressPostalCode;
    private String addressCountry;
    private String companyName;
    private String businessRegistrationNumber;
}
