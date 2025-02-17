package com.jamith.booksformeapi.dto.requestDTO;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderItem implements Serializable {
    private String orderItemId;
    private String cartItemId;
    private String orderId;
    private String bookId;
    private String bookStockId;
    private String sellerId;
    private String imageUrl;
    private double price;
    private int quantity;
    private String title;
    private String status;
}
