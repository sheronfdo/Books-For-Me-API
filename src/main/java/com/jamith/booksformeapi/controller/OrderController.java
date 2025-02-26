package com.jamith.booksformeapi.controller;


import com.jamith.booksformeapi.dto.requestDTO.OrderDTO;
import com.jamith.booksformeapi.dto.requestDTO.OrderStatusDTO;
import com.jamith.booksformeapi.dto.requestDTO.PaymentStatusDTO;
import com.jamith.booksformeapi.service.OrderService;
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
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @PostMapping("/makeorder")
    ResponseEntity<Object> makeOrder(@RequestBody OrderDTO orderDTO) {
        try {
            log.info("Make order: " + orderDTO);
            return orderService.makeOrder(orderDTO);
        } catch (Exception e) {
            log.error("Unexpected error during seller registration: ", e);
            return ResponseUtil.generateErrorResponse("Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/paymentStatus")
    ResponseEntity<Object> paymentStatusUpdate(@RequestBody PaymentStatusDTO paymentStatusDTO) {
        try {
            log.info("Make update order: " + paymentStatusDTO);

            ResponseEntity<Object> response = orderService.paymentStatus(paymentStatusDTO);

            log.debug("After calling orderService.paymentStatus()");

            return response;

        } catch (Exception e) {
            log.error("Unexpected error during seller registration: ", e);
            return ResponseUtil.generateErrorResponse("Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/orderStatus")
    ResponseEntity<Object> orderStatusUpdate(@RequestBody OrderStatusDTO orderStatusDTO) {
        try {
            log.info("Make status update order: " + orderStatusDTO);
            return orderService.orderStatus(orderStatusDTO);
        } catch (Exception e) {
            log.error("Unexpected error during seller registration: ", e);
            return ResponseUtil.generateErrorResponse("Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
