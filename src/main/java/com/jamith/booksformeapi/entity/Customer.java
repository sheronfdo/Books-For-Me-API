package com.jamith.booksformeapi.entity;

import com.jamith.booksformeapi.enums.UserRole;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class Customer {
    String uid;
    String displayName;
    String imageUri;
    String firstName;
    String lastName;
    String phoneNumber;
    String email;
    private Map<String, String> address;
    private Date createdAt;
    private UserRole role;
    private Date updatedAt;
}
