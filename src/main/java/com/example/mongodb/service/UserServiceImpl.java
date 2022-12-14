package com.example.mongodb.service;



import com.example.mongodb.dto.TokenDTO;
import com.example.mongodb.entity.StakingInfo;
import com.example.mongodb.entity.TimeStamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
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

                // ??????????????? ????????? ????????????
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = br.readLine()) != null) { // ?????? ??? ?????? ??? ?????? ??????
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

                // ??????????????? ????????? ????????????
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = br.readLine()) != null) { // ?????? ??? ?????? ??? ?????? ??????
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

                // ??????????????? ????????? ????????????
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = br.readLine()) != null) { // ?????? ??? ?????? ??? ?????? ??????
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
                    else objTemp.put("price", "????????????");

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

            // ??????????????? ????????? ????????????
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = br.readLine()) != null) { // ?????? ??? ?????? ??? ?????? ??????
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

    @Override
    public void updateStackingStatus() {
        log.info("update StackingStatus..."+ LocalDateTime.now());

        JSONObject jsonObject = null;
        ArrayList<StakingInfo> arrayList = new ArrayList<>();
        Long newTimeStamp = null;
        TimeStamp prevStamp = mongoTemplate.findOne(query(where("status").is("updated")), TimeStamp.class);

        String userCredentials = "KASKWIM459K2J82E1N7JY2HZ:cBr-HHL0S5AenhZqeendbpw4vbr3oEW2bBxMIJEr";
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));

        String walletAddress = null;
        String status = null;
        String cursur = "";

        Map<String, StakingInfo> map1 = new HashMap<String, StakingInfo>();
        try {
            while (true) {
                String request_url = "https://th-api.klaytnapi.com/v2/transfer/account/0x72e534e9f167dd72fec2d327f4b96fba2da79469?kind=nft&size=1000";

                URL url = new URL(request_url + "&cursor=" + cursur);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Authorization", basicAuth);
                con.setRequestMethod("GET");
                con.setRequestProperty("x-chain-id", "8217");

                // ??????????????? ????????? ????????????
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = br.readLine()) != null) { // ?????? ??? ?????? ??? ?????? ??????
                    sb.append(line);
                }

                JSONParser parser = new JSONParser();
                jsonObject = (JSONObject) parser.parse(sb.toString());

                cursur = jsonObject.get("cursor").toString();
                JSONArray array = (JSONArray) jsonObject.get("items");
                Long prevTimeStamp = prevStamp.getTimestamp();
                if ( newTimeStamp == null) newTimeStamp = (Long)((JSONObject)((JSONObject)array.get(0)).get("transaction")).get("timestamp");

                for (int i = 0; i < array.size(); i++) {
                    Long nowTimeStamp = (Long)((JSONObject)((JSONObject)array.get(i)).get("transaction")).get("timestamp");

                    if (prevTimeStamp >= nowTimeStamp) {
                        cursur = "";
                        break;
                    }

                    if ( ((JSONObject)array.get(i)).get("to").equals("0x72e534e9f167dd72fec2d327f4b96fba2da79469")) {
                        walletAddress = ((JSONObject)array.get(i)).get("from").toString();
                        status = "stake";
                    } else {
                        walletAddress = ((JSONObject)array.get(i)).get("to").toString();
                        status = "unStake";
                    }

                    if (map1.containsKey(walletAddress.toString())) continue;


                    map1.put(walletAddress, StakingInfo.builder()
                            .walletAddress(walletAddress)
                            .status(status)
                            .name(((JSONObject)((JSONObject)array.get(i)).get("contract")).get("name").toString())
                            .tokenID(((JSONObject)array.get(i)).get("tokenId").toString())
                            .timestamp((Long) ((JSONObject)((JSONObject)array.get(i)).get("transaction")).get("timestamp")).build());

                }


                if (cursur.equals("")) {
                    break;
                }

            }


            for (Map.Entry<String,StakingInfo> entry: map1.entrySet()){
                Query query = new Query(where("walletAddress").is(entry.getValue().getWalletAddress()));
                Update update = new Update().set("status", entry.getValue().getStatus())
                        .set("timestamp", entry.getValue().getTimestamp());

                StakingInfo oldValue = mongoTemplate.update(StakingInfo.class)
                        .matching(query)
                        .apply(update)
                        .withOptions(FindAndModifyOptions.options().upsert(true).returnNew(true))
                        .findAndModifyValue(); // return's old person object

            }

            Query query = new Query(where("status").is("updated"));
            Update update = new Update().set("timestamp", newTimeStamp);
            TimeStamp oldValue = mongoTemplate.update(TimeStamp.class)
                    .matching(query)
                    .apply(update)
                    .withOptions(FindAndModifyOptions.options().upsert(true).returnNew(true))
                    .findAndModifyValue(); // return's old person object




        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
