package com.jamith.booksformeapi.dto.responseDTO;

import lombok.Data;

import java.util.Date;

@Data
public class BookAddResponseDTO {
    private String id;
    private Date createdTime;
}
