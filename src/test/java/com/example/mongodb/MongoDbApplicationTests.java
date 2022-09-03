package com.example.mongodb;

import com.example.mongodb.controller.dto.FTDto;
import com.example.mongodb.controller.dto.NFTDto;
import com.example.mongodb.controller.service.UserService;
import com.example.mongodb.entity.StakingInfo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JsonbTester;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

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
        Query query = new Query(Criteria.where("walletAddress").is("Harry"));
        Update update = new Update().set("status", "update");

        StakingInfo oldValue = mongoTemplate.update(StakingInfo.class)
                .matching(query)
                .apply(update)
                .withOptions(FindAndModifyOptions.options().upsert(true).returnNew(true))
                .findAndModifyValue(); // return's old person object

    }

    @Test
    void getStackingList() {

        JSONObject jsonObject = null;
        ArrayList<StakingInfo> arrayList = new ArrayList<>();

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


    }

    @Test
    void getTokenInfo(){
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
                jsonObject = (JSONObject) parser.parse(sb.toString());

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

            JSONObject obj = new JSONObject();
            org.json.JSONArray arr = new org.json.JSONArray();
            if (ftDTO.size() > 0){
                for (int i=0; i < ftDTO.size(); i++){
                    JSONObject objTemp = new JSONObject();
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


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void test1(){

        System.out.println(userService.getTokenOwnership());
        System.out.println("");


    }

}
