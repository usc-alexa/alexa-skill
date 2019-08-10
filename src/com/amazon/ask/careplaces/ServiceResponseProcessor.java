package com.amazon.ask.careplaces;

import com.amazon.speech.speechlet.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class ServiceResponseProcessor {


    public static String getAppointmentSpeech(JSONObject slotObject, String dateRangeStart, Session session){
        if(!slotObject.has("entry")){
            return "No slots available for "+dateRangeStart+". Would you like to try another date?";
        }

        JSONArray slotsArray = slotObject.getJSONArray("entry");

        if(slotsArray.length() == 0){
            return "No slots available for "+dateRangeStart+". Would you like to try another date?";
        }

        StringBuffer slotMessage = new StringBuffer();


        slotMessage.append("Following slots are available for Dr. Tami Howdeshell on your requested date.");


        Map<String,String> slotmap = new HashMap<String,String>();

        for (int i = 0; i < slotsArray.length(); i++)
        {
            if(i == 3){
                break;
            }
            StringBuffer slotFrag = new StringBuffer();

            String start = slotsArray.getJSONObject(i).getJSONObject("resource").getString("start");
            System.out.println(start);

            String startTime = start.split("T")[1];
            String end = slotsArray.getJSONObject(i).getJSONObject("resource").getString("end");
            String endTime = end.split("T")[1];
            String startDate = start.split("T")[0];
            String endDate = end.split("T")[0];
            slotFrag.append(" Slot "+(i+1)+": on " + startDate + " from "+ startTime);
            slotFrag.append(" to "+endTime +".");
            slotmap.put((i+1)+"",slotsArray.getJSONObject(i).getJSONObject("resource").getJSONObject("schedule").getString("reference")+"#"+slotFrag.toString());
            slotMessage.append(slotFrag.toString());
        }
        if(slotsArray.length() == 1){
            slotMessage.append(" Would you like to book this slot? Please say book this slot to confirm this appointment");
        } else {

            slotMessage.append(" you can say book slot. followed by slot number. something like book slot 1");
        }
        if(session!=null) {
            session.setAttribute("SLOTMAP", slotmap);
        }
        return slotMessage.toString();
    }

    public static String convertToPst(String date) {
        try {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date myDateInSimpleDateFormat = simpleDateFormat.parse(date);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
            return simpleDateFormat.format(myDateInSimpleDateFormat);

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

    }
}
