package com.amazon.ask.careplaces.handlers.services;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class AlertIntentService {
	
	RestTemplate restTemplate = new RestTemplate();
	
	public String getAlert(){
		return "You have one Alert";
	}
	
	public AlertIntentService(){}
	
	public String login(){
		
		RestTemplate restTemplate = new RestTemplate();

		String url = "https://dev1.careplaces.us/ubercare-system/api/login";
		String requestJson = "{\"username\":\"sathima@msn.com\",\"password\":\"C.p@2017#\"}";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<String>(requestJson,headers);
		Map<String,String> answer = new HashMap<String,String>();
		StringBuffer alertMessage = new StringBuffer();
		JSONObject alertobj =null;
		try {
			answer = getRestTemplate().postForObject(url, entity, Map.class);
			
			System.out.println("AlertIntentService.login()"+answer.get("access_token"));
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			
			MultiValueMap<String, String> alertRequestmap= new LinkedMultiValueMap<String, String>();
			alertRequestmap.add("access_token", answer.get("access_token"));
			
			
			String alertUrl = "https://dev1.careplaces.us/ubercare-system/api/v1/patient/getUserAlerts";
			
			
			HttpEntity<MultiValueMap<String, String>> alertentity = new HttpEntity<MultiValueMap<String, String>>(alertRequestmap, headers);

			
			String alertData = getRestTemplate().postForObject(alertUrl, alertentity, String.class);
			
			alertobj = new JSONObject(alertData);
			
			System.out.println("AlertIntentService.login()11"+alertobj.getJSONArray("alerts"));
			
			JSONArray alertsArray = alertobj.getJSONArray("alerts");
			
			
			
			for (int i = 0; i < alertsArray.length(); i++)
			{
			    String alertMsg = alertsArray.getJSONObject(i).getString("alertMessage");
			    alertMessage.append(", ");
			    alertMessage.append(alertMsg);
			    
			}
			
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return "You have "+alertobj.getInt("totalCount")+ " alert messages. Here are your messages "+ alertMessage.toString();
	}
	
	
	
		public RestTemplate getRestTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		    TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

		    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
		                    .loadTrustMaterial(null, acceptingTrustStrategy)
		                    .build();

		    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

		    CloseableHttpClient httpClient = HttpClients.custom()
		                    .setSSLSocketFactory(csf)
		                    .build();

		    HttpComponentsClientHttpRequestFactory requestFactory =
		                    new HttpComponentsClientHttpRequestFactory();

		    requestFactory.setHttpClient(httpClient);
		    RestTemplate restTemplate = new RestTemplate(requestFactory);
		    return restTemplate;
		 }
		
	
	
	
	public static void main(String args[]){
		
		AlertIntentService service = new AlertIntentService();
		System.out.println("AlertIntentService.main()"+service.login());
		
	}
	

}
