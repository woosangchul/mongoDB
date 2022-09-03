package com.example.mongodb.controller.service;


import com.example.mongodb.controller.dto.FTDto;
import com.example.mongodb.controller.dto.NFTDto;
import com.example.mongodb.entity.StakingInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;


@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {

    @Override
    public org.json.JSONArray getTokenOwnership() {
        JSONObject jsonObject = null;
        ArrayList<StakingInfo> arrayList = new ArrayList<>();

        String userCredentials = "KASKWIM459K2J82E1N7JY2HZ:cBr-HHL0S5AenhZqeendbpw4vbr3oEW2bBxMIJEr";
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));

        String walletAddress = "0x8d5516d63213304647d2702f8027f0eef1a2480b";
        String cursur = "";

        ArrayList<FTDto> ftDTO = new ArrayList<>();
        ArrayList<NFTDto> nftDTO = new ArrayList<>();
        try {
            while (true) {
                String request_url = "https://th-api.klaytnapi.com/v2/account/"+walletAddress+ "/token?size=1000";

                URL url = new URL(request_url + "&cursor=" + cursur);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Authorization", basicAuth);
                con.setRequestMethod("GET");
                con.setRequestProperty("x-chain-id", "8217");

                // 서버로부터 데이터 읽어오기
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = br.readLine()) != null) { // 읽을 수 있을 때 까지 반복
                    sb.append(line);
                }

                JSONParser parser = new JSONParser();
                jsonObject = (org.json.simple.JSONObject) parser.parse(sb.toString());

                cursur = jsonObject.get("cursor").toString();
                JSONArray array = (JSONArray) jsonObject.get("items");

                for (int i=0; i < array.size(); i++){
                    String kind = ((JSONObject)array.get(i)).get("kind").toString();

                    if (kind.equals("nft")){
                        nftDTO.add( NFTDto.builder()
                                .kind(kind)
                                .contractAddress(((JSONObject)array.get(i)).get("contractAddress").toString())
                                .tokenId(((JSONObject)((JSONObject)array.get(i)).get("extras")).get("tokenId").toString()).build());

                    }else if(kind.equals("ft")){
                        ftDTO.add( FTDto.builder()
                                .kind(kind)
                                .contractAddress(((JSONObject)array.get(i)).get("contractAddress").toString())
                                .symbol(((JSONObject)((JSONObject)array.get(i)).get("extras")).get("symbol").toString()).build());


                    }
                }

                if (cursur.equals("")) {
                    break;
                }

            }

            org.json.JSONObject obj = new org.json.JSONObject();
            org.json.JSONArray arr = new org.json.JSONArray();
            if (ftDTO.size() > 0){
                for (int i=0; i < ftDTO.size(); i++){
                    org.json.JSONObject objTemp = new org.json.JSONObject();
                    objTemp.put("address", walletAddress);
                    objTemp.put(ftDTO.get(i).getSymbol(), ftDTO.get(i).getContractAddress());
                    arr.put(objTemp);
                }
            }

            if (nftDTO.size() >0){
                for (int i=0; i < nftDTO.size(); i++){
                    JSONObject objTemp = new JSONObject();
                    objTemp.put("address", walletAddress);
                    objTemp.put("nft_contract", nftDTO.get(i).getContractAddress());
                    objTemp.put("nft_id", nftDTO.get(i).getTokenId());
                    arr.put(objTemp);
                }
            }

            System.out.println(arr);
            return arr;


        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;


    }
}
