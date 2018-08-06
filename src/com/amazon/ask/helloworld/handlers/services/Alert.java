package com.amazon.ask.helloworld.handlers.services;

import java.util.LinkedList;
import java.util.List;

import org.springframework.util.MultiValueMap;

public class Alert {
	int totalCount;
	List<MultiValueMap<String, String>> alerts= new LinkedList<MultiValueMap<String, String>>();
	public List<MultiValueMap<String, String>> getAlerts() {
		return alerts;
	}
	public void setAlerts(List<MultiValueMap<String, String>> alerts) {
		this.alerts = alerts;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	
	public String getAlertMsg(){
		StringBuffer alertMessage = new StringBuffer();
		
		for(MultiValueMap<String,String> alert : alerts){
			alertMessage.append(" ");
			alertMessage.append(alert.get("alertMessage"));
			
		}
		return alertMessage.toString();
	}
	@Override
	public String toString() {
		return "Alert [totalCount=" + totalCount + ", alerts=" + alerts + "]";
	}
	
	
	
	
}
