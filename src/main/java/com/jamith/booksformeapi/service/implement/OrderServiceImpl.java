package com.jamith.booksformeapi.service.implement;

import com.google.api.core.ApiFuture;
import com.google.apps.card.v1.OnClick;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.jamith.booksformeapi.dto.requestDTO.OrderDTO;
import com.jamith.booksformeapi.dto.requestDTO.OrderStatusDTO;
import com.jamith.booksformeapi.dto.requestDTO.PaymentStatusDTO;
import com.jamith.booksformeapi.dto.responseDTO.OrderResponseDTO;
import com.jamith.booksformeapi.entity.BookStock;
import com.jamith.booksformeapi.entity.Order;
import com.jamith.booksformeapi.service.NotificationService;
import com.jamith.booksformeapi.service.OrderService;
import com.jamith.booksformeapi.utils.DateUtil;
import com.jamith.booksformeapi.enums.OrderStatus;
import com.jamith.booksformeapi.enums.PaymentStatus;
import com.jamith.booksformeapi.utils.ResponseUtil;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Log4j2
@Service
public class OrderServiceImpl implements OrderService {
    ModelMapper modelMapper = new ModelMapper();
    Firestore db = FirestoreClient.getFirestore();

    @Autowired
    NotificationService notificationService;


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

    @Override
    public ResponseEntity<Object> paymentStatus(PaymentStatusDTO paymentStatusDTO) {
        try {
            Order order;
            String orderId = paymentStatusDTO.getOrderId();

            DocumentReference docRef = db.collection("orders").document(orderId);
            ApiFuture<DocumentSnapshot> future = docRef.get();

            DocumentSnapshot document = future.get();
            if (document.exists()) {
                order = document.toObject(Order.class);
                order.setPaymentDetailsDTO(paymentStatusDTO.getPaymentDetailsDTO());
                log.debug("order object: " + order);
            } else {
                System.out.println("No such document!");
                return ResponseUtil.generateErrorResponse("Order Not Found", HttpStatus.BAD_REQUEST);
            }


            if (paymentStatusDTO.getPaymentStatus().equals(PaymentStatus.PAYMENT_STATUS_COMPLETED)) {
                order.setStatus(OrderStatus.ORDER_CONFIRMED.name());
                order.setPaymentStatus(PaymentStatus.PAYMENT_STATUS_COMPLETED.name());
                order.setUpdatedAt(DateUtil.fromFirestoreTimestamp());
                order.getCartItems().stream().forEach(cartItemDTO -> {
                    cartItemDTO.setOrderId(orderId);
                    cartItemDTO.setStatus(OrderStatus.ORDER_CONFIRMED.name());
                    String documentId = db.collection("orders").document(orderId).collection("orderItems")
                            .document().getId();
                    db.collection("orders").document(orderId).collection("orderItems")
                            .document(documentId).set(cartItemDTO);
                    db.collection("customers").document(order.getCustomerId()).collection("orderItems")
                            .document(documentId).set(cartItemDTO);
                    db.collection("sellers").document(cartItemDTO.getSellerId()).collection("orderItems")
                            .document(documentId).set(cartItemDTO);
                    db.collection("customers").document(order.getCustomerId()).collection("cart")
                            .document(cartItemDTO.getCartItemId()).delete();
                    try {
                        DocumentReference sellerDocRef = db.collection("sellers").document(cartItemDTO.getSellerId());
                        ApiFuture<DocumentSnapshot> sellerFuture = sellerDocRef.get();
                        DocumentSnapshot sellerDoc = sellerFuture.get();
                        String sellerFcmToken = sellerDoc.getString("fcmToken");

                        if (sellerFcmToken != null) {
                            notificationService.sendNotificationToToken(
                                    sellerFcmToken,
                                    "New Order Confirmed",
                                    "You have received a new order. Order ID: " + orderId
                            );
                        }
                    } catch (FirestoreException | ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });

                DocumentReference customerDocRef = db.collection("customers").document(order.getCustomerId());
                ApiFuture<DocumentSnapshot> customerFuture = customerDocRef.get();
                DocumentSnapshot customerDoc = customerFuture.get();
                String customerFcmToken = customerDoc.getString("fcmToken");

                if (customerFcmToken != null) {
                    notificationService.sendNotificationToToken(
                            customerFcmToken,
                            "Order Confirmed",
                            "Your order has been confirmed. Order ID: " + orderId
                    );
                }

                order.setCartItems(null);
                ApiFuture<WriteResult> orders = db.collection("orders").document(orderId).set(order, SetOptions.merge());
                OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
                orderResponseDTO.setId(orderId);
                orderResponseDTO.setCreatedTime(orders.get().getUpdateTime().toDate());
                return ResponseUtil.generateSuccessResponse("Order Update Successful.", orderResponseDTO);
            } else if (paymentStatusDTO.getPaymentStatus().equals(PaymentStatus.PAYMENT_STATUS_CANCELLED)) {
                order.setStatus(OrderStatus.PAYMENT_PENDING.name());
                order.setPaymentStatus(PaymentStatus.PAYMENT_STATUS_CANCELLED.name());
                order.setUpdatedAt(DateUtil.fromFirestoreTimestamp());

                ApiFuture<WriteResult> orders = db.collection("orders").document(orderId).set(order, SetOptions.merge());
                OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
                orderResponseDTO.setId(orderId);
                orderResponseDTO.setCreatedTime(orders.get().getUpdateTime().toDate());
                return ResponseUtil.generateSuccessResponse("Order Update Successful.", orderResponseDTO);
            } else {
                order.setStatus(OrderStatus.PAYMENT_PENDING.name());
                order.setPaymentStatus(PaymentStatus.PAYMENT_STATUS_FAILED.name());
                order.setUpdatedAt(DateUtil.fromFirestoreTimestamp());

                ApiFuture<WriteResult> orders = db.collection("orders").document(orderId).set(order, SetOptions.merge());
                OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
                orderResponseDTO.setId(orderId);
                orderResponseDTO.setCreatedTime(orders.get().getUpdateTime().toDate());
                return ResponseUtil.generateSuccessResponse("Order Update Successful.", orderResponseDTO);
            }

        } catch (FirestoreException | ExecutionException | InterruptedException e) {
            return ResponseUtil.generateErrorResponse("Firestore Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.generateErrorResponse("Invalid Input: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Internal Server Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> orderStatus(OrderStatusDTO orderStatusDTO) {
        try {
            Order order;
            String orderId = orderStatusDTO.getOrderId();

            DocumentReference docRef = db.collection("orders").document(orderId);
            ApiFuture<DocumentSnapshot> future = docRef.get();

            DocumentSnapshot document = future.get();
            if (document.exists()) {
                order = document.toObject(Order.class);
                log.debug("order object: " + order);
            } else {
                System.out.println("No such document!");
                return ResponseUtil.generateErrorResponse("Order Not Found", HttpStatus.BAD_REQUEST);
            }

            DocumentReference sellerorderRef = db
                    .collection("sellers")
                    .document(orderStatusDTO.getSellerId())
                    .collection("orderItems")
                    .document(orderStatusDTO.getOrderItemId());

            if (orderStatusDTO.getOrderStatus().equals(OrderStatus.ORDER_APPROVED)) {
                ApiFuture<DocumentSnapshot> sellerOrderItem = sellerorderRef.get();
                DocumentSnapshot sellerOrderItemDocument = sellerOrderItem.get();
                if (sellerOrderItemDocument.exists()) {
                    String bookStockId = sellerOrderItemDocument.getString("bookStockId");
                    BookStock bookStock;
                    DocumentReference bookRef = db.collection("bookStocks").document(bookStockId);
                    ApiFuture<DocumentSnapshot> bookStockRef = bookRef.get();
                    DocumentSnapshot bookStockDocument = bookStockRef.get();
                    if (bookStockDocument.exists()) {
                        bookStock = bookStockDocument.toObject(BookStock.class);
                        int orderQuantity = sellerOrderItemDocument.get("quantity", int.class);
                        Map<String, Object> stocks = new HashMap<>();
                        stocks.put("stock", bookStock.getStock() - orderQuantity);
                        bookRef.update(stocks);
                    } else {
                        return ResponseUtil.generateErrorResponse("Book Stock Not Found", HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return ResponseUtil.generateErrorResponse("Order Item Not Found", HttpStatus.BAD_REQUEST);
                }
            }


            DocumentReference orderRef = db
                    .collection("orders")
                    .document(orderStatusDTO.getOrderId())
                    .collection("orderItems")
                    .document(orderStatusDTO.getOrderItemId());
            Map<String, Object> updates = new HashMap<>();
            updates.put("status", orderStatusDTO.getOrderStatus());

            ApiFuture<WriteResult> writeResult = orderRef.update(updates);
            WriteResult result = writeResult.get();
            log.debug("Order status updated at: " + result.getUpdateTime());


            Map<String, Object> sellerupdates = new HashMap<>();
            sellerupdates.put("status", orderStatusDTO.getOrderStatus());

            ApiFuture<WriteResult> sellerwriteResult = sellerorderRef.update(sellerupdates);
            WriteResult sellerresult = sellerwriteResult.get();

            log.debug("Seller status updated at: " + sellerresult.getUpdateTime());

            DocumentReference customerorderRef = db
                    .collection("customers")
                    .document(order.getCustomerId())
                    .collection("orderItems")
                    .document(orderStatusDTO.getOrderItemId());
            Map<String, Object> customerupdates = new HashMap<>();
            customerupdates.put("status", orderStatusDTO.getOrderStatus());

            ApiFuture<WriteResult> customerwriteResult = customerorderRef.update(customerupdates);
            WriteResult customerresult = customerwriteResult.get();
            log.debug("Customer status updated at: " + customerresult.getUpdateTime());

            DocumentReference customerDocRef = db.collection("customers").document(order.getCustomerId());
            ApiFuture<DocumentSnapshot> customerFuture = customerDocRef.get();
            DocumentSnapshot customerDoc = customerFuture.get();
            String customerFcmToken = customerDoc.getString("fcmToken");

            notificationService.sendNotificationToToken(
                    customerFcmToken,
                    "Order Status Updated",
                    "Your order status has updated. Order ID: " + orderId
            );

            OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
            orderResponseDTO.setId(orderId);
            orderResponseDTO.setCreatedTime(result.getUpdateTime().toDate());
            return ResponseUtil.generateSuccessResponse("Order Status Update Successful.", orderResponseDTO);
        } catch (FirestoreException | ExecutionException | InterruptedException e) {
            return ResponseUtil.generateErrorResponse("Firestore Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.generateErrorResponse("Invalid Input: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Internal Server Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
