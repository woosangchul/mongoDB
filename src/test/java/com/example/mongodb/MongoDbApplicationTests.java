package com.example.mongodb;

import com.example.mongodb.entity.StakingInfo;
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
import java.util.Base64;
import java.util.Stack;

@SpringBootTest
class MongoDbApplicationTests {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void insertMongoDB(){
        StakingInfo userInfo = StakingInfo.builder()
                        .walletAddress("0x676ab3ecdd7ad65f7a9ccfd4ec919d1c5b7c98af")
                        .status("stake")
                        .name("KlayCity District")
                        .symbol("KCDT")
                        .timestamp(1662034961L)
                        .build();

        mongoTemplate.insert(userInfo);

    }

    @Test
    void getMongoDB(){
        Query query = new Query(Criteria.where("walletAddress").is("Harry"));
        Update update = new Update().set("status","update");

        StakingInfo oldValue = mongoTemplate.update(StakingInfo.class)
                .matching(query)
                .apply(update)
                .withOptions(FindAndModifyOptions.options().upsert(true).returnNew(true))
                .findAndModifyValue(); // return's old person object


    }





    @Test
    void getStackingList() {
        JSONObject jsonObject = null;

        try{
            URL url = new URL("https://th-api.klaytnapi.com/v2/transfer/account/0x72e534e9f167dd72fec2d327f4b96fba2da79469?kind=nft");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            String userCredentials = "KASKWIM459K2J82E1N7JY2HZ:cBr-HHL0S5AenhZqeendbpw4vbr3oEW2bBxMIJEr";
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));

            con.setDoOutput(true);
            //con.setDoInput(False);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", basicAuth);
            con.setRequestMethod("GET");
            con.setRequestProperty("x-chain-id", "8217");

            // 서버로부터 데이터 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = br.readLine()) != null) { // 읽을 수 있을 때 까지 반복
                sb.append(line);
            }

            JSONParser parser = new JSONParser();
            jsonObject = (JSONObject)parser.parse(sb.toString());

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
