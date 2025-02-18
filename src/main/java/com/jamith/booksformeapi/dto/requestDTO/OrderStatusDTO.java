package com.jamith.booksformeapi.dto.requestDTO;

import com.jamith.booksformeapi.enums.OrderStatus;
import com.jamith.booksformeapi.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderStatusDTO {
    private String orderId;
    private String orderItemId;
    private String sellerId;
    private OrderStatus orderStatus;
}
