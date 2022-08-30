package com.example.mongodb.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;

@Data
@Builder
@Document(collection = "stackingInfo")
public class StakingInfo {

    @Id
    private String id;

    //stack or unstack
    private String status;

    private String name;

    private String symbol;

    private Timestamp timestamp;

}
