package com.jamith.booksformeapi.service;

import com.jamith.booksformeapi.dto.requestDTO.CustomerSignUpDTO;
import org.springframework.http.ResponseEntity;

public interface CustomerService {
    ResponseEntity<Object> customerSignUp(CustomerSignUpDTO customerSignUpDTO);
}
