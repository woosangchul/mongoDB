package com.example.mongodb.controller.service;


import com.example.mongodb.dto.TokenDTO;

import java.util.Map;

public interface UserService {

    org.json.JSONArray getTokenOwnership();

    org.json.JSONArray getTokenOwnershipWithWalletAddress(String walletAddress);

    org.json.JSONArray getNFTTokenOwnershipWithWalletAddress(String walletAddress);

    Map<String, TokenDTO> getTokenPrice();





}
