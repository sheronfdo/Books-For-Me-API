package com.jamith.booksformeapi.service;

import com.jamith.booksformeapi.dto.requestDTO.OrderDTO;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    ResponseEntity<Object> makeOrder(OrderDTO orderDTO);
}
