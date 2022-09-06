package com.example.mongodb;


import com.example.mongodb.connection.CreateConnection;
import com.example.mongodb.connection.KlaytonServerConnection;
import com.example.mongodb.dto.TokenDTO;
import com.example.mongodb.service.UserService;
import com.example.mongodb.entity.StakingInfo;
import com.example.mongodb.entity.TimeStamp;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@SpringBootTest
class MongoDbApplicationTests {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserService userService;

    @Test
    void insertMongoDB() {
        StakingInfo userInfo = StakingInfo.builder()
                .walletAddress("0x11111")
                .status("stake")
                .name("KlayCity District")
                .timestamp(1662034961L)
                .tokenID("0x11")
                .build();

        mongoTemplate.insert(userInfo);


    }

    @Test
    void getMongoDB() {
        Query query = new Query(where("walletAddress").is("Harry"));
        Update update = new Update().set("status", "update");

        StakingInfo oldValue = mongoTemplate.update(StakingInfo.class)
                .matching(query)
                .apply(update)
                .withOptions(FindAndModifyOptions.options().upsert(true).returnNew(true))
                .findAndModifyValue(); // return's old person object
    }

    @Test
    void getMongoDB1() {
        String walletAddress = "0x8D5516d63213304647D2702f8027f0eEF1a2480b";
        //Query query = new Query(Criteria.where("walletAddress").is(walletAddress.toLowerCase()));
        //Update update = new Update().set("status", "update");

        /*
        StakingInfo oldValue = mongoTemplate.update(StakingInfo.class)
                .matching(query)
                .apply(update)
                .withOptions(FindAndModifyOptions.options().upsert(true).returnNew(true))
                .findAndModifyValue(); // return's old person object


         */
        StakingInfo oldValue = mongoTemplate.findOne(query(where("walletAddress").is(walletAddress.toLowerCase())), StakingInfo.class);
        System.out.println(oldValue.toString());



    }

    @Test
    void initialMongoDB() {

        JSONObject jsonObject = null;
        ArrayList<StakingInfo> arrayList = new ArrayList<>();

        String userCredentials = "KASKWIM459K2J82E1N7JY2HZ:cBr-HHL0S5AenhZqeendbpw4vbr3oEW2bBxMIJEr";
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));

        String walletAddress = null;
        String status = null;
        String cursur = "";
        TimeStamp newTimestamp = null;
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

                // 서버로부터 데이터 읽어오기
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = br.readLine()) != null) { // 읽을 수 있을 때 까지 반복
                    sb.append(line);
                }

                JSONParser parser = new JSONParser();
                jsonObject = (JSONObject) parser.parse(sb.toString());

                cursur = jsonObject.get("cursor").toString();
                JSONArray array = (JSONArray) jsonObject.get("items");

                newTimestamp = TimeStamp.builder()
                        .status("updated")
                        .timestamp((Long) ((JSONObject)((JSONObject)array.get(0)).get("transaction")).get("timestamp")).build();

                for (int i = 0; i < array.size(); i++) {
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Map.Entry<String,StakingInfo> entry: map1.entrySet()){
            mongoTemplate.insert(entry.getValue());
        }

        assert newTimestamp != null;
        mongoTemplate.insert(newTimestamp);


    }

    @Test
    void initialMongoDBWithKlaytonServerConnection() throws IOException, ParseException {

        
        ArrayList<StakingInfo> arrayList = new ArrayList<>();

        String walletAddress = null;
        String status = null;

        TimeStamp newTimestamp = null;
        Map<String, StakingInfo> map1 = new HashMap<String, StakingInfo>();

        String cursur = "";
        String request_url = "https://th-api.klaytnapi.com/v2/transfer/account/0x72e534e9f167dd72fec2d327f4b96fba2da79469?kind=nft&size=1000"+"&cursur="+cursur.toString();
        JSONObject jsonObject = CreateConnection.open(request_url)
                                                    .method("GET")
                                                    .requestProperty("Content-Type", "application/json")
                                                    .requestProperty("Accept", "application/json")
                                                    .requestProperty("Accept", "application/json")
                                                    .requestProperty("x-chain-id", "8217")
                                                    .output()
                                                    .getJson();

        cursur = jsonObject.get("cursor").toString();
        JSONArray array = (JSONArray) jsonObject.get("items");

        newTimestamp = TimeStamp.builder()
                .status("updated")
                .timestamp((Long) ((JSONObject)((JSONObject)array.get(0)).get("transaction")).get("timestamp")).build();

        for (int i = 0; i < array.size(); i++) {
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




        for (Map.Entry<String,StakingInfo> entry: map1.entrySet()){
            mongoTemplate.insert(entry.getValue());
        }

        assert newTimestamp != null;
        mongoTemplate.insert(newTimestamp);


    }

    @Test
    void getTokenPrice(){

        Map<String, TokenDTO> map1 =  userService.getTokenPrice();

        System.out.println(map1);

    }

    @Test
    void getTokenInfo(){
        String walletAddress = "0x8D5516d63213304647D2702f8027f0eEF1a2480b";

        userService.getTokenOwnershipWithWalletAddress(walletAddress);

    }

    @Test
    void insertStamp(){
        mongoTemplate.insert(TimeStamp.builder()
                    .status("updated")
                    .timestamp(1662130510L).build());

    }

    @Test
    void batchTest(){
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

                // 서버로부터 데이터 읽어오기
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = br.readLine()) != null) { // 읽을 수 있을 때 까지 반복
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
