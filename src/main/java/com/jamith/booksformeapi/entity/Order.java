package com.jamith.booksformeapi.entity;

import com.jamith.booksformeapi.dto.requestDTO.OrderItem;
import com.jamith.booksformeapi.dto.requestDTO.PaymentDetailsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {
    private String recieverName;
    private String recieverAddress;
    private String recieverPhoneNumber;
    private String recieverEmail;
    private String customerId;
    private List<OrderItem> cartItems;
    private int itemCount;
    private double totalPrice;
    private String paymentStatus;
    private String status;
    private PaymentDetailsDTO paymentDetailsDTO;
    private Date orderDate;
    private Date createdAt;
    private Date updatedAt;
}
