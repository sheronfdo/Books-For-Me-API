package com.jamith.booksformeapi.service;


import com.jamith.booksformeapi.dto.requestDTO.SellerSignUpDTO;
import org.springframework.http.ResponseEntity;


public interface SellerService {
     ResponseEntity<Object> registerSeller(SellerSignUpDTO seller);
}
