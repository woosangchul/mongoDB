package com.example.mongodb.controller.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;



public class FtDTO extends ResultDTO{
    private String symbol;

    @Builder
    public FtDTO(String address, String kind, String contractAddress, String symbol) {
        super(address, kind, contractAddress);
        this.symbol = symbol;
    }
}
