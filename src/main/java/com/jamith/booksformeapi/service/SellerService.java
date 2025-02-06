package com.jamith.booksformeapi.service;


import com.jamith.booksformeapi.dto.requestDTO.SellerAddressDTO;
import com.jamith.booksformeapi.dto.requestDTO.SellerImageDTO;
import com.jamith.booksformeapi.dto.requestDTO.SellerSignUpBrDTO;
import com.jamith.booksformeapi.dto.requestDTO.SellerSignUpDTO;
import org.springframework.http.ResponseEntity;


public interface SellerService {
     ResponseEntity<Object> registerSeller(SellerSignUpDTO seller);
     ResponseEntity<Object> setSellerAddress(SellerAddressDTO sellerAddress);
     ResponseEntity<Object> setSellerBusinessRegisterDetail(SellerSignUpBrDTO sellerSignUpBrDTO);
     ResponseEntity<Object> setSellerImage(SellerImageDTO sellerImageDTO);
}
