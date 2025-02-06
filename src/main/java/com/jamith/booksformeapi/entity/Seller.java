package com.jamith.booksformeapi.entity;

import com.jamith.booksformeapi.enums.SellerType;
import com.jamith.booksformeapi.enums.UserRole;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class Seller {
    private String sellerId;
    private SellerType sellerType;
    private String fullNameOrRepresentative;
    private String email;
    private String phoneNumber;
    private Map<String, String> address;
    private Map<String, String> businessDetails;
    private boolean verified;
    private Date createdAt;
    private UserRole role;
    private Date updatedAt;
    private String imageUrl;
}
