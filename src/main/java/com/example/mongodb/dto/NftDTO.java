package com.example.mongodb.dto;

import lombok.Builder;


public class NftDTO extends ResultDTO{

    private String tokenId;

    @Builder
    public NftDTO(String address, String kind, String contractAddress, String tokenId) {
        super(address, kind, contractAddress);
        this.tokenId = tokenId;
    }
}
