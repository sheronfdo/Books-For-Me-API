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
}
