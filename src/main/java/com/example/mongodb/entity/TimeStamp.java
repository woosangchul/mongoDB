package com.example.mongodb.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Data
@Document(collection = "stackingInfo")
public class TimeStamp {
    private String status;
    private long timestamp;
}
