package com.jamith.booksformeapi.service.implement;

import com.google.api.core.ApiFuture;
import com.google.apps.card.v1.OnClick;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.jamith.booksformeapi.dto.requestDTO.OrderDTO;
import com.jamith.booksformeapi.dto.requestDTO.OrderItem;
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
        log.info("Processing payment status: " + paymentStatusDTO);
        try {
            String orderId = paymentStatusDTO.getOrderId();
            if (orderId == null || orderId.isEmpty()) {
                return ResponseUtil.generateErrorResponse("Invalid Order ID", HttpStatus.BAD_REQUEST);
            }

            DocumentReference docRef = db.collection("orders").document(orderId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (!document.exists()) {
                log.warn("Order not found for ID: " + orderId);
                return ResponseUtil.generateErrorResponse("Order Not Found", HttpStatus.BAD_REQUEST);
            }

            Order order = document.toObject(Order.class);
            order.setPaymentDetailsDTO(paymentStatusDTO.getPaymentDetailsDTO());
            order.setUpdatedAt(DateUtil.fromFirestoreTimestamp());

            if (PaymentStatus.PAYMENT_STATUS_COMPLETED.equals(paymentStatusDTO.getPaymentStatus())) {
                handleCompletedPayment(order, orderId);
            } else if (PaymentStatus.PAYMENT_STATUS_CANCELLED.equals(paymentStatusDTO.getPaymentStatus())) {
                order.setStatus(OrderStatus.PAYMENT_PENDING.name());
                order.setPaymentStatus(PaymentStatus.PAYMENT_STATUS_CANCELLED.name());
            } else {
                order.setStatus(OrderStatus.PAYMENT_PENDING.name());
                order.setPaymentStatus(PaymentStatus.PAYMENT_STATUS_FAILED.name());
            }

            ApiFuture<WriteResult> orders = db.collection("orders").document(orderId).set(order, SetOptions.merge());
            OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
            orderResponseDTO.setId(orderId);
            orderResponseDTO.setCreatedTime(orders.get().getUpdateTime().toDate());

            return ResponseUtil.generateSuccessResponse("Order Update Successful.", orderResponseDTO);

        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();  // Restore thread status
            log.error("Firestore Error: ", e);
            return ResponseUtil.generateErrorResponse("Firestore Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            log.error("Invalid Input: ", e);
            return ResponseUtil.generateErrorResponse("Invalid Input: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Unexpected Error: ", e);
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
    private void handleCompletedPayment(Order order, String orderId) {
        order.setStatus(OrderStatus.ORDER_CONFIRMED.name());
        order.setPaymentStatus(PaymentStatus.PAYMENT_STATUS_COMPLETED.name());

        for (OrderItem cartItemDTO : order.getCartItems()) {
            processCartItem(order, orderId, cartItemDTO);
        }

        notifyCustomer(order.getCustomerId(), orderId);
        order.setCartItems(null);  // Clear cart items after processing
    }

    private void processCartItem(Order order, String orderId, OrderItem cartItemDTO) {
        cartItemDTO.setOrderId(orderId);
        cartItemDTO.setStatus(OrderStatus.ORDER_CONFIRMED.name());

        String documentId = db.collection("orders").document(orderId).collection("orderItems").document().getId();
        db.collection("orders").document(orderId).collection("orderItems").document(documentId).set(cartItemDTO);
        db.collection("customers").document(order.getCustomerId()).collection("orderItems").document(documentId).set(cartItemDTO);
        db.collection("sellers").document(cartItemDTO.getSellerId()).collection("orderItems").document(documentId).set(cartItemDTO);

        safelyDeleteCartItem(order.getCustomerId(), cartItemDTO.getCartItemId());
        notifySeller(cartItemDTO.getSellerId(), orderId);
    }

    private void safelyDeleteCartItem(String customerId, String cartItemId) {
        if (customerId == null || cartItemId == null || cartItemId.isEmpty()) {
            log.warn("Skipping cart item deletion due to null/empty values: customerId=" + customerId + ", cartItemId=" + cartItemId);
            return;
        }

        DocumentReference customerRef = db.collection("customers").document(customerId).collection("cart").document(cartItemId);
        try {
            ApiFuture<DocumentSnapshot> cartFuture = customerRef.get();
            DocumentSnapshot cartDoc = cartFuture.get();
            if (cartDoc.exists()) {
                customerRef.delete();
                log.info("Deleted cart item: " + cartItemId);
            } else {
                log.warn("Cart item not found: " + cartItemId);
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error while deleting cart item: " + cartItemId, e);
        }
    }

    private void notifySeller(String sellerId, String orderId) {
        if (sellerId == null || sellerId.isEmpty()) {
            log.warn("Seller ID is null or empty. Skipping notification.");
            return;
        }

        DocumentReference sellerDocRef = db.collection("sellers").document(sellerId);
        try {
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
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error while notifying seller: " + sellerId, e);
        }
    }

    private void notifyCustomer(String customerId, String orderId) {
        if (customerId == null || customerId.isEmpty()) {
            log.warn("Customer ID is null or empty. Skipping notification.");
            return;
        }

        DocumentReference customerDocRef = db.collection("customers").document(customerId);
        try {
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
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error while notifying customer: " + customerId, e);
        }
    }



}
