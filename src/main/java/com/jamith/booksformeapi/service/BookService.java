package com.jamith.booksformeapi.service;

import com.jamith.booksformeapi.dto.requestDTO.AddNewBookDTO;
import com.jamith.booksformeapi.dto.requestDTO.SellerImageDTO;
import org.springframework.http.ResponseEntity;

public interface BookService {
    ResponseEntity<Object> addNewBook(AddNewBookDTO addNewBookDTO);
}
