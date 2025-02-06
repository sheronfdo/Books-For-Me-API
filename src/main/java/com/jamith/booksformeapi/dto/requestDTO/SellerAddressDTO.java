package com.jamith.booksformeapi.dto.requestDTO;

import lombok.Data;

@Data
public class SellerAddressDTO {
    private String id;
    private String addressStreet;
    private String addressCity;
    private String addressState;
    private String addressPostalCode;
    private String addressCountry;
}
