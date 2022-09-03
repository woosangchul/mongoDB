package com.example.mongodb.controller.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;



public class NftDTO extends ResultDTO{

    private String tokenId;

    @Builder
    public NftDTO(String address, String kind, String contractAddress, String tokenId) {
        super(address, kind, contractAddress);
        this.tokenId = tokenId;
    }
}
