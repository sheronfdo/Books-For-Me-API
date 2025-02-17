package com.jamith.booksformeapi.service;

import com.jamith.booksformeapi.dto.requestDTO.OrderDTO;
import com.jamith.booksformeapi.dto.requestDTO.OrderStatusDTO;
import com.jamith.booksformeapi.dto.requestDTO.PaymentStatusDTO;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    ResponseEntity<Object> makeOrder(OrderDTO orderDTO);
    ResponseEntity<Object> paymentStatus(PaymentStatusDTO paymentStatusDTO);
    ResponseEntity<Object> orderStatus(OrderStatusDTO paymentStatusDTO);
}
