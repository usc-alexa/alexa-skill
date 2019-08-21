package com.amazon.ask.unitedhealth.handlers.services;

import com.amazon.ask.unitedhealth.Parameters;
import com.amazon.ask.unitedhealth.ServiceResponseProcessor;
import com.amazon.speech.speechlet.Session;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
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
import java.util.*;

public class Service {
	
	RestTemplate restTemplate = new RestTemplate();
	
	public String getAlert(){
		return "You have one Alert";
	}
	
	public Service(){}

	/**
	 *
	 * @param session
	 * @return
	 *
	 * Call the OAuth service to get the token.
	 * TODO create an API GateWay proxy and provide credential their instead of hardcoding in the code
	 */
	public String getAccessToken(Session session){


		if(session!=null && !StringUtils.isEmpty(session.getAttribute("access_token"))){
			return session.getAttribute("access_token").toString();
		}

		String url = Parameters.OAUTH_TOKEN;

		MultiValueMap<String, String> authRequestMap= new LinkedMultiValueMap<String, String>();
		authRequestMap.setAll(Parameters.getHeaders());



		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		headers.setAll(Parameters.getHeaders());

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(authRequestMap,headers);
		Map<String,String> answer = new HashMap<String,String>();

		String access_token = "";
		try {
			answer = getRestTemplate().postForObject(url, entity, Map.class);
			access_token = answer.get("access_token");
			System.out.println("access_token="+access_token);
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
		if(session!=null) {
			session.setAttribute("access_token", access_token);
		}
		return access_token;

	}
	


	public String createAppointment(String slot,String slotText,Session session){

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		try {

			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(getAccessToken(session));

			String slotRefId = slotText.split("#")[0];

			slotText = slotText.split("#")[1];



			String createAppointmentUrl = Parameters.MAKE_APPOINTMENT_CONTEXT+ "/4342008/"+slotRefId; // todo make call to pull patient id

			String slotRequest = "{\"practitionerId\":\"605926\"}";


			HttpEntity<String> appointmentEntity = new HttpEntity<String>(slotRequest, headers);
			System.out.println(createAppointmentUrl);


			String appointmentResponse = getRestTemplate().postForObject(createAppointmentUrl, appointmentEntity, String.class);

			JSONObject appointmentResponseJson = new JSONObject(appointmentResponse);
			System.out.println("appointment response = "+appointmentResponseJson.toString());

			if("accepted".equals(appointmentResponseJson.getString("participantStatus"))) {

				return "Your appointment is booked successfully for " + slotText + ". Thank you for using our service.";
			} else {
				return "error";
			}

		} catch (RestClientException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			return "Sorry Tim, I was not able to book the appointment for technical reason. We are investigating the problem.  Please try later.";
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Sorry Tim, I was not able to book the appointment for technical reason. We are investigating the problem.  Please try later.";
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Sorry Tim, I was not able to book the appointment for technical reason. We are investigating the problem.  Please try later.";
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Sorry Tim, I was not able to book the appointment for technical reason. We are investigating the problem.  Please try later.";
		}


	}


	/**
	 * This method will return appoint slots for a physician
	 * @return
	 */
	public String getAppointmentSlots(String dateRangeStart, Session session) throws Exception{

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(getAccessToken(session));
		Map<String,String> answer = new HashMap<String,String>();

		JSONObject slotObject =null;
		try {



			// todo externalize practitionerId, this should be a service based on zip code provided by the patient

			String slotRequest = "{\"practitionerId\":\"605926\"}";


			String slotUrl = Parameters.AVAILABLE_SLOTS_CONTEXT + "/" + dateRangeStart+"/10";


			HttpEntity<String> slotEntity = new HttpEntity<String>(slotRequest, headers);
			System.out.println(slotUrl);


			String slotData = getRestTemplate().postForObject(slotUrl, slotEntity, String.class);

			slotObject = new JSONObject(slotData);
			System.out.println("Slot returned = "+slotObject.toString());

			return ServiceResponseProcessor.getAppointmentSpeech(slotObject,dateRangeStart,session);

		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
			//return "Sorry Tim, we are having a technical difficulty. We are investigating the problem.  Please try again later.";

		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
			//return "Sorry Tim, we are having a technical difficulty. We are investigating the problem.  Please try again later.";
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
			//return "Sorry Tim, we are having a technical difficulty. We are investigating the problem.  Please try again later.";
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
			//return "Sorry Tim, we are having a technical difficulty. We are investigating the problem.  Please try again later.";
		}

		//return null;

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
		
		Service service = new Service();
		//System.out.println("Service.main()"+service.getAlerts());


		try {
			System.out.println("Service.main()"+service.getAppointmentSlots("2019-07-19",null));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}




	

}
