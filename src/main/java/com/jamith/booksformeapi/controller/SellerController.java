package com.jamith.booksformeapi.controller;

import com.jamith.booksformeapi.dto.responseDTO.SellerSignUpDTO;
import com.jamith.booksformeapi.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/seller")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @PostMapping("/register")
    public String registerSeller(@RequestBody SellerSignUpDTO sellerSignUpDTO) {
        try {
            return sellerService.registerSeller(sellerSignUpDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}

