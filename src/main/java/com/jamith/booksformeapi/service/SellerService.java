package com.jamith.booksformeapi.service;


import com.jamith.booksformeapi.dto.responseDTO.SellerSignUpDTO;
import com.jamith.booksformeapi.entity.Seller;
import org.springframework.stereotype.Service;


public interface SellerService {
     String registerSeller(SellerSignUpDTO seller);
}
