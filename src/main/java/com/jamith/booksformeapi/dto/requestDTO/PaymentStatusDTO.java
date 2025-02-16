package com.jamith.booksformeapi.dto.requestDTO;

import com.jamith.booksformeapi.utils.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PaymentStatusDTO {

    private String orderId;
    private PaymentStatus paymentStatus;
    private PaymentDetailsDTO paymentDetailsDTO;

}
