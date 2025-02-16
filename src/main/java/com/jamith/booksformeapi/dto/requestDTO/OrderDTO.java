package com.jamith.booksformeapi.dto.requestDTO;

import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class OrderDTO {

    private String recieverName;
    private String recieverAddress;
    private String recieverPhoneNumber;
    private String recieverEmail;
    private String customerId;
    private List<OrderItem> cartItems;
    private int itemCount;
    private double totalPrice;
}
