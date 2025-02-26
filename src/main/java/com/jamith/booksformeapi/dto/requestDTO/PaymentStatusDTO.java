package com.jamith.booksformeapi.dto.requestDTO;

import com.jamith.booksformeapi.enums.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PaymentStatusDTO implements Serializable {
    @NotBlank(message = "Order ID cannot be empty")
    private String orderId;
    @NotNull(message = "Payment status cannot be null")
    private PaymentStatus paymentStatus;
    @NotNull(message = "Payment details cannot be null")
    private PaymentDetailsDTO paymentDetailsDTO;

}
