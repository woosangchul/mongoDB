package com.example.mongodb.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenDTO {
    private String symbol;
    private String name;
    private Double price;
}
