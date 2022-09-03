package com.example.mongodb.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NFTDto {
    private String kind;
    private String contractAddress;
    private String tokenId;
}
