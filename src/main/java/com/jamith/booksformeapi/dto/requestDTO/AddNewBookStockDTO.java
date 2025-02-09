package com.jamith.booksformeapi.dto.requestDTO;

import lombok.Data;

@Data
public class AddNewBookStockDTO {
    private String bookId;
    private String sellerId;
    private int stock;
    private double price;
    private String condition;
}
