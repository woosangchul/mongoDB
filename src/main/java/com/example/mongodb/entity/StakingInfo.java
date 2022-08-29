package com.example.mongodb.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "stackingInfo")
public class StakingInfo {

    @Id
    private String id;
    private String status;


}
