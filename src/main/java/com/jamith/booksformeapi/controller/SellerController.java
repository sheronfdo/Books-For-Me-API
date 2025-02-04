package com.jamith.booksformeapi.controller;

import com.jamith.booksformeapi.dto.requestDTO.SellerSignUpDTO;
import com.jamith.booksformeapi.service.SellerService;
import com.jamith.booksformeapi.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @PostMapping("/register")
    public ResponseEntity<Object> registerSeller(@RequestBody SellerSignUpDTO sellerSignUpDTO) {
        try {
            return sellerService.registerSeller(sellerSignUpDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.generateErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

