package com.example.mongodb.connection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;
import java.util.Properties;

public class KlaytonServerConnection {
    private HttpURLConnection con;

    @Value("${klayton.authkey")
    private String authKey;

    public KlaytonServerConnection(URL url) throws IOException {
        con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Authorization", "Basic " + new String(Base64.getEncoder().encode(authKey.getBytes())));
    }

    public KlaytonServerConnection method(String method) throws ProtocolException{
        con.setRequestMethod(method);
        return this;
    }

    public KlaytonServerConnection requestProperty(String key, String value) {
        con.setRequestProperty(key, value);
        return this;
    }

    public KlaytonServerConnection output() {
        con.setDoOutput(true);
        return this;
    }

    public JSONObject getJson() throws IOException, ParseException {
        // 서버로부터 데이터 읽어오기
        BufferedReader br = new BufferedReader(new InputStreamReader(this.con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line = null;

        while ((line = br.readLine()) != null) { // 읽을 수 있을 때 까지 반복
            sb.append(line);
        }

        JSONParser parser = new JSONParser();
        return (org.json.simple.JSONObject) parser.parse(sb.toString());

    }






}
