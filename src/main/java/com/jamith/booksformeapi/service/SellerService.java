package com.jamith.booksformeapi.service;


import com.jamith.booksformeapi.dto.requestDTO.*;
import org.springframework.http.ResponseEntity;


public interface SellerService {
    ResponseEntity<Object> registerSeller(SellerSignUpDTO seller);

    ResponseEntity<Object> setSellerAddress(SellerAddressDTO sellerAddress);

    ResponseEntity<Object> setSellerBusinessRegisterDetail(SellerSignUpBrDTO sellerSignUpBrDTO);

    ResponseEntity<Object> setSellerImage(SellerImageDTO sellerImageDTO);

    ResponseEntity<Object> updateSeller(SellerUpdateDTO sellerUpdateDTO);
}
