package com.example.mongodb.controller.dto;



import lombok.AllArgsConstructor;

import lombok.Data;


@AllArgsConstructor
@Data
public class ResultDTO {
    private String address;
    private String kind;
    private String contractAddress;
}
