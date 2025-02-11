package com.jamith.booksformeapi.dto.requestDTO;

import lombok.Data;

@Data
public class CustomerSignUpDTO {
    String uid;
    String displayName;
    String imageUri;
    String firstName;
    String lastName;
    String phoneNumber;
    String email;
}
