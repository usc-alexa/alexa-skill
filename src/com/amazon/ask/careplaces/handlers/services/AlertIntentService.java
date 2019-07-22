package com.amazon.ask.careplaces.handlers.services;

import com.amazon.speech.speechlet.Session;
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
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

public class AlertIntentService {
	
	RestTemplate restTemplate = new RestTemplate();
	
	public String getAlert(){
		return "You have one Alert";
	}
	
	public AlertIntentService(){}

	public String getAccessToken(Session session){


		if(!StringUtils.isEmpty(session.getAttribute("access_token"))){
			return session.getAttribute("access_token").toString();
		}

		String url = "https://dev1.careplaces.us/ubercare-system/api/login";
		String requestJson = "{\"username\":\"sathima@msn.com\",\"password\":\"C.p@2017#\"}";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<String>(requestJson,headers);
		Map<String,String> answer = new HashMap<String,String>();
		StringBuffer alertMessage = new StringBuffer();
		JSONObject alertobj =null;
		String access_token = "";
		try {
			answer = getRestTemplate().postForObject(url, entity, Map.class);
			access_token = answer.get("access_token");
		}catch (RestClientException e) {
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
		session.setAttribute("access_token",access_token);
		return access_token;

	}
	
	public String getAlerts(Session session){
		
		RestTemplate restTemplate = new RestTemplate();


		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String,String> answer = new HashMap<String,String>();
		StringBuffer alertMessage = new StringBuffer();
		JSONObject alertobj =null;
		try {



			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> alertRequestmap= new LinkedMultiValueMap<String, String>();
			alertRequestmap.add("access_token", getAccessToken(session));
			
			
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

	public String createAppointment(String slot,String slotText,Session session){

		RestTemplate restTemplate = new RestTemplate();


		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String,String> answer = new HashMap<String,String>();
		StringBuffer alertMessage = new StringBuffer();
		JSONObject alertobj =null;

		try {



			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> slotRequestMap= new LinkedMultiValueMap<String, String>();
			slotRequestMap.add("access_token", getAccessToken(session));
			slotRequestMap.add("orgID","2");

			String slotRefId = slotText.split("#")[0];
			slotRequestMap.add("slotID",slotRefId);

			slotText = slotText.split("#")[1];



			String slotUrl = "https://dev1.careplaces.us/ubercare-system/api/v1/patientemr/bookPhysicianAppointment";


			HttpEntity<MultiValueMap<String, String>> slotEntity = new HttpEntity<MultiValueMap<String, String>>(slotRequestMap, headers);


			String response = getRestTemplate().postForObject(slotUrl, slotEntity, String.class);

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

		return "Your appoint is booked successfully for "+slotText;
	}


	/**
	 * This method will return appoint slots for a physician
	 * @return
	 */
	public String getAppointmentSlots(String dateRangeStart, Session session){
		RestTemplate restTemplate = new RestTemplate();


		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String,String> answer = new HashMap<String,String>();
		StringBuffer slotMessage = new StringBuffer();
		JSONObject slotObject =null;
		try {



			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> slotRequestMap= new LinkedMultiValueMap<String, String>();
			slotRequestMap.add("access_token", getAccessToken(session));
			slotRequestMap.add("physicianUserID","45");
			slotRequestMap.add("orgID","2");

			slotRequestMap.add("startDate", StringUtils.isEmpty(dateRangeStart)?dateRangeStart:"2019-07-01");
			slotRequestMap.add("endDate",StringUtils.isEmpty(dateRangeStart)?dateRangeStart:"2019-07-01");



			String slotUrl = "https://dev1.careplaces.us/ubercare-system/api/v1/patientemr/getPhysicianSlots";


			HttpEntity<MultiValueMap<String, String>> slotEntity = new HttpEntity<MultiValueMap<String, String>>(slotRequestMap, headers);


			String slotData = getRestTemplate().postForObject(slotUrl, slotEntity, String.class);

			slotObject = new JSONObject(slotData);

			System.out.println("AlertIntentService.login()11"+slotObject.toString());

			if(!slotObject.has("slots")){
				return "No slots available for "+dateRangeStart;
			}

			JSONArray slotsArray = slotObject.getJSONArray("slots");

			if(slotsArray.length() == 0){
				return "No slots available for "+dateRangeStart;
			}




			slotMessage.append("Following slots are available for your requested date.");


			Map<String,String> slotmap = new HashMap<String,String>();

			for (int i = 0; i < slotsArray.length(); i++)
			{
				StringBuffer slotFrag = new StringBuffer();

				String start = slotsArray.getJSONObject(i).getString("start");
				String startTime = start.split(",")[1];
				String end = slotsArray.getJSONObject(i).getString("end");
				String endTime = end.split(",")[1];
				String startDate = start.split(",")[0];
				String endDate = end.split(",")[0];
				slotFrag.append(" Slot "+(i+1)+": on " + startDate + " from "+ startTime);
				slotFrag.append(" to "+endTime +".");
				slotmap.put((i+1)+"",slotsArray.getJSONObject(i).getString("scheduleReferenceID")+"#"+slotFrag.toString());
				slotMessage.append(slotFrag.toString());
			}

			slotMessage.append(" you can say book slot 1 or book slot 2 or book slot 3");
			if(session!=null) {
				session.setAttribute("SLOTMAP", slotmap);
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

		return slotMessage.toString();

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
		//System.out.println("AlertIntentService.main()"+service.getAlerts());


		System.out.println("AlertIntentService.main()"+service.getAppointmentSlots("2019-07-01",null));
		
	}
	

}
