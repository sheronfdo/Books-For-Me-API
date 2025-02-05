package com.jamith.booksformeapi.controller;

import com.jamith.booksformeapi.dto.requestDTO.SellerSignUpDTO;
import com.jamith.booksformeapi.service.SellerService;
import com.jamith.booksformeapi.utils.ResponseUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/api/seller")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @PostMapping("/register")
    public ResponseEntity<Object> registerSeller(@RequestBody SellerSignUpDTO sellerSignUpDTO) {
        try {
            log.info("Register seller: " + sellerSignUpDTO);
            return sellerService.registerSeller(sellerSignUpDTO);
        } catch (Exception e) {
            log.error("Unexpected error during seller registration: ", e);
            return ResponseUtil.generateErrorResponse("Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

