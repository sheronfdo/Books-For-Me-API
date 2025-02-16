package com.jamith.booksformeapi.service.implement;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreException;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.jamith.booksformeapi.dto.requestDTO.OrderDTO;
import com.jamith.booksformeapi.dto.responseDTO.CustomerSignUpResponseDTO;
import com.jamith.booksformeapi.dto.responseDTO.OrderResponseDTO;
import com.jamith.booksformeapi.entity.Customer;
import com.jamith.booksformeapi.entity.Order;
import com.jamith.booksformeapi.enums.UserRole;
import com.jamith.booksformeapi.service.OrderService;
import com.jamith.booksformeapi.utils.DateUtil;
import com.jamith.booksformeapi.utils.OrderStatus;
import com.jamith.booksformeapi.utils.PaymentStatus;
import com.jamith.booksformeapi.utils.ResponseUtil;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ExecutionException;

@Service
public class OrderServiceImpl implements OrderService {
    ModelMapper modelMapper = new ModelMapper();
    Firestore db = FirestoreClient.getFirestore();


    @Override
    public ResponseEntity<Object> makeOrder(OrderDTO orderDTO) {
        try {
            Order order = modelMapper.map(orderDTO, Order.class);
            order.setStatus(OrderStatus.PAYMENT_PENDING.name());
            order.setPaymentStatus(PaymentStatus.PAYMENT_PENDING.name());
            order.setOrderDate(DateUtil.fromFirestoreTimestamp());
            order.setCreatedAt(DateUtil.fromFirestoreTimestamp());
            order.setUpdatedAt(DateUtil.fromFirestoreTimestamp());
            String documentId = db.collection("orders").document().getId();
            ApiFuture<WriteResult> orders = db.collection("orders").document(documentId).set(order);
//            orderDTO.getCartItems().stream().forEach(cartItemDTO -> {
//                cartItemDTO.setOrderId(documentId);
//                db.collection("customers").document(orderDTO.getCustomerId()).collection("orderItems").add(cartItemDTO);
//                db.collection("sellers").document(cartItemDTO.getSellerId()).collection("orderItems").add(cartItemDTO);
//            });
            OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
            orderResponseDTO.setId(documentId);
            orderResponseDTO.setCreatedTime(orders.get().getUpdateTime().toDate());
            return ResponseUtil.generateSuccessResponse("Order Create Successful.", orderResponseDTO);
        } catch (FirestoreException | ExecutionException | InterruptedException e) {
            return ResponseUtil.generateErrorResponse("Firestore Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.generateErrorResponse("Invalid Input: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Internal Server Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
