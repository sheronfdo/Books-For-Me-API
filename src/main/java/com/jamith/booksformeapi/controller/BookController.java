package com.jamith.booksformeapi.controller;

import com.jamith.booksformeapi.dto.requestDTO.AddNewBookDTO;
import com.jamith.booksformeapi.dto.requestDTO.AddNewBookStockDTO;
import com.jamith.booksformeapi.dto.requestDTO.SellerSignUpDTO;
import com.jamith.booksformeapi.service.BookService;
import com.jamith.booksformeapi.service.SellerService;
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
@RequestMapping("/api/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping("/addNewBook")
    public ResponseEntity<Object> addNewBook(@RequestBody AddNewBookDTO addNewBookDTO) {
        try {
            log.info("add new book: " + addNewBookDTO);
            return bookService.addNewBook(addNewBookDTO);
        } catch (Exception e) {
            log.error("Unexpected error during book registration: ", e);
            return ResponseUtil.generateErrorResponse("Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addNewBookStock")
    public ResponseEntity<Object> addNewBookStock(@RequestBody AddNewBookStockDTO addNewBookStockDTO) {
        try {
            log.info("add new book stock: " + addNewBookStockDTO);
            return bookService.addNewBookStock(addNewBookStockDTO);
        } catch (Exception e) {
            log.error("Unexpected error during book registration: ", e);
            return ResponseUtil.generateErrorResponse("Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
