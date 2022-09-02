package com.example.rest;

import lombok.Builder;
import lombok.Data;
import java.net.HttpURLConnection;

@Data
@Builder
public class KaikasRequest {
    private HttpURLConnection con;


}
