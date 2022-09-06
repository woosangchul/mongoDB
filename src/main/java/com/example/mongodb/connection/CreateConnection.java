package com.example.mongodb.connection;

import java.io.IOException;
import java.net.URL;

public class CreateConnection {
    public static KlaytonServerConnection open(String url) throws IOException{
        return new KlaytonServerConnection(new URL(url));
    }
}
