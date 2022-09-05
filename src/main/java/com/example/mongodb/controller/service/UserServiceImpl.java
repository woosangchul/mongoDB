package com.example.mongodb.controller.service;



import com.example.mongodb.dto.TokenDTO;
import com.example.mongodb.entity.StakingInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;


@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {

    private final MongoTemplate mongoTemplate;

    @Override
    public org.json.JSONArray getTokenOwnership() {
        JSONObject jsonObject = null;
        ArrayList<StakingInfo> arrayList = new ArrayList<>();

        String userCredentials = "KASKWIM459K2J82E1N7JY2HZ:cBr-HHL0S5AenhZqeendbpw4vbr3oEW2bBxMIJEr";
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));

        String walletAddress = "0x8d5516d63213304647d2702f8027f0eef1a2480b";
        String cursur = "";

        org.json.JSONArray arr = new org.json.JSONArray();

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
                        JSONObject objTemp = new JSONObject();
                        objTemp.put("address", walletAddress);
                        objTemp.put("nft_contract", ((JSONObject)array.get(i)).get("contractAddress").toString());
                        objTemp.put("nft_id", ((JSONObject)((JSONObject)array.get(i)).get("extras")).get("tokenId").toString());
                        arr.put(objTemp);

                    }else if(kind.equals("ft")){
                        org.json.JSONObject objTemp = new org.json.JSONObject();
                        objTemp.put("address", walletAddress);
                        objTemp.put(((JSONObject)((JSONObject)array.get(i)).get("extras")).get("symbol").toString(), ((JSONObject)array.get(i)).get("contractAddress").toString());
                        arr.put(objTemp);


                    }
                }

                if (cursur.equals("")) {
                    break;
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return arr;


    }

    @Override
    public org.json.JSONArray getNFTTokenOwnershipWithWalletAddress(String walletAddress) {
        JSONObject jsonObject = null;
        ArrayList<StakingInfo> arrayList = new ArrayList<>();

        String userCredentials = "KASKWIM459K2J82E1N7JY2HZ:cBr-HHL0S5AenhZqeendbpw4vbr3oEW2bBxMIJEr";
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));

        String cursur = "";

        org.json.JSONArray arr = new org.json.JSONArray();

        try {
            while (true) {
                String request_url = "https://th-api.klaytnapi.com/v2/account/"+walletAddress+ "/token?kind=nft&size=1000";

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
                        JSONObject objTemp = new JSONObject();
                        objTemp.put("address", walletAddress);
                        objTemp.put("nft_contract", ((JSONObject)array.get(i)).get("contractAddress").toString());
                        objTemp.put("nft_id", ((JSONObject)((JSONObject)array.get(i)).get("extras")).get("tokenId").toString());
                        arr.put(objTemp);

                    }
                }

                StakingInfo oldValue = mongoTemplate.findOne(query(where("walletAddress").is(walletAddress.toLowerCase())), StakingInfo.class);
                if (oldValue.getStatus().equals("stake")){
                    JSONObject objTemp = new JSONObject();
                    objTemp.put("address", oldValue.getWalletAddress());
                    objTemp.put("nft_contract", "0x7561e492f075c4e49939772f6aa8eaf85ec60019");
                    objTemp.put("nft_id", oldValue.getTokenID());
                    arr.put(objTemp);

                }


                if (cursur.equals("")) {
                    break;
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return arr;

    }

    @Override
    public org.json.JSONArray getTokenOwnershipWithWalletAddress(String walletAddress) {
        JSONObject jsonObject = null;

        String userCredentials = "KASKWIM459K2J82E1N7JY2HZ:cBr-HHL0S5AenhZqeendbpw4vbr3oEW2bBxMIJEr";
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));

        String cursur = "";

        org.json.JSONArray arr = new org.json.JSONArray();
        Map<String, TokenDTO> map1 = getTokenPrice();

        try {
            while (true) {
                String request_url = "https://th-api.klaytnapi.com/v2/account/"+walletAddress+ "/token?kind=ft&size=1000";

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
                    double price = 0;

                    org.json.JSONObject objTemp = new org.json.JSONObject();

                    String symbol =((JSONObject)((JSONObject)array.get(i)).get("extras")).get("symbol").toString();
                    objTemp.put("address", walletAddress);
                    objTemp.put(symbol, ((JSONObject)array.get(i)).get("contractAddress").toString());
                    if (map1.containsKey(symbol)) objTemp.put("price", map1.get(symbol).getPrice());
                    else objTemp.put("price", "정보없음");

                    arr.put(objTemp);

                }






                if (cursur.equals("")) {
                    break;
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return arr;



    }

    @Override
    public Map<String, TokenDTO> getTokenPrice() {
        JSONArray jsonArray = null;
        Map<String, TokenDTO> map1 = new HashMap<String, TokenDTO>();

        try {
            String request_url = "https://s.klayswap.com/stat/tokenInfo.min.json";

            URL url = new URL(request_url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");
            //con.setRequestProperty("Accept", "application/json");
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
            jsonArray = (org.json.simple.JSONArray) parser.parse(sb.toString());
            System.out.println(jsonArray);
            for (int i=1; i<jsonArray.size(); i++){
                map1.put(((JSONArray)jsonArray.get(i)).get(2).toString(), TokenDTO.builder()
                                                                            .symbol(((JSONArray)jsonArray.get(i)).get(2).toString())
                                                                            .name(((JSONArray)jsonArray.get(i)).get(3).toString())
                                                                            .price(Math.round(Double.parseDouble(((JSONArray)jsonArray.get(i)).get(14).toString())*100)/100.0).build());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map1;
    }
}
