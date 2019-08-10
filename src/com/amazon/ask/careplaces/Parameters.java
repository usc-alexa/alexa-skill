package com.amazon.ask.careplaces;

import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class Parameters {



    public static String BASE = "https://api-gateway.linkhealth.com";
    public static String OAUTH_TOKEN = BASE + "/oauth/token";
    public static String AVAILABLE_SLOTS_CONTEXT = BASE + "/alexa-scheduler/1/availableSlots";


    static Map<String,String> headerMap;

    public static Map<String,String> getHeaders(){
        headerMap = new HashMap<String,String>();
        headerMap.put("grant_type","client_credentials");
        headerMap.put("client_id","alexa-adapter-client-1");
        headerMap.put("client_secret","d3d8b7a4-a2c7-497f-812a-cb7b893d3f2b");
        return headerMap;
    }


}
