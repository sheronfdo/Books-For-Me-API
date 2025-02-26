package com.jamith.booksformeapi.dto.requestDTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class PaymentDetailsDTO implements Serializable
{
    private String paymentId;
    private String paymentStatus;
    private String createdTime;
    private String intent;
}
