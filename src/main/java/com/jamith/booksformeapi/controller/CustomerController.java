package com.jamith.booksformeapi.controller;

import com.jamith.booksformeapi.dto.requestDTO.CustomerSignUpDTO;
import com.jamith.booksformeapi.dto.requestDTO.CustomerUpdateDTO;
import com.jamith.booksformeapi.service.CustomerService;
import com.jamith.booksformeapi.utils.ResponseUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<Object> registerCustomer(@RequestBody CustomerSignUpDTO customerSignUpDTO) {
        try {
            log.info("Customer : " + customerSignUpDTO);
            return customerService.customerSignUp(customerSignUpDTO);
        } catch (Exception e) {
            log.error("Unexpected error during customer registration: ", e);
            return ResponseUtil.generateErrorResponse("Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<Object> updateCustomer(@RequestBody CustomerUpdateDTO customerUpdateDTO) {
        try {
            log.info("Customer update : " + customerUpdateDTO);
            return customerService.updateCustomer(customerUpdateDTO);
        } catch (Exception e) {
            log.error("Unexpected error during customer registration: ", e);
            return ResponseUtil.generateErrorResponse("Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
