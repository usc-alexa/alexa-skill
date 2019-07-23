/**
 Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.

 Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

 http://aws.amazon.com/apache2.0/

 or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.amazon.ask.careplaces;

import com.amazon.ask.careplaces.handlers.services.AlertIntentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.OutputSpeech;

import java.util.Map;

/**
 * This sample shows how to create a simple speechlet for handling speechlet requests.
 */
public class CareplaceSpeechlet implements SpeechletV2 {
    private static final Logger log = LoggerFactory.getLogger(com.amazon.ask.careplaces.CareplaceSpeechlet.class);

    @Override
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        log.info("onSessionStarted requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
        // any initialization logic goes here
    }

    @Override
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        log.info("onLaunch requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
        return getWelcomeResponse();
    }

    @Override
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        IntentRequest request = requestEnvelope.getRequest();
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                requestEnvelope.getSession().getSessionId());

        Intent intent = request.getIntent();

        String intentName = (intent != null) ? intent.getName() : null;
        AlertIntentService service = new AlertIntentService();
        if ("MakeAppointment".equals(intentName) || "RespondInitQuestion".equals(intentName)) {
            String dateRangeStart = intent.getSlot("startDate").getValue();


            System.out.println("date passed start="+dateRangeStart);


            String speechText = service.getAppointmentSlots(dateRangeStart,requestEnvelope.getSession());

            return getAskResponse("Confirm Selection",speechText);
        }

        if("ConfirmOneSlotBooking".equals(intentName)){
            String slotNumber = "1";
            Map<String,String> slotvalue = (Map)requestEnvelope.getSession().getAttribute("SLOTMAP");
            String slotText = slotvalue.get(slotNumber);
            if(slotText==null){
                return getAskResponse("Slot error","Please say a valid slot number. You can say slot one or slot two etcetera.");
            }

            String responseText = service.createAppointment(slotNumber,slotText,requestEnvelope.getSession());
            return getResponse(responseText);
        }

        if ("ConfirmSlot".equals(intentName)) {
           // String speechText = service.getAppointmentSlots(dateRangeStart,dateRangeEnd,requestEnvelope.getSession());
            String slotNumber = intent.getSlot("slotValue").getValue();

            System.out.println("slot number="+slotNumber);
            System.out.println("stored slots="+requestEnvelope.getSession().getAttribute("SLOTMAP"));
            Map<String,String> slotvalue = (Map)requestEnvelope.getSession().getAttribute("SLOTMAP");
            String slotText = slotvalue.get(slotNumber);
            if(slotText==null){
                return getAskResponse("Slot error","Please say a valid slot number. You can say slot one or slot two etcetera.");
            }

            String responseText = service.createAppointment(slotNumber,slotText,requestEnvelope.getSession());
            return getResponse(responseText);
        }
        if ("AssertInitQuestion".equals(intentName)) {
            String speechText = "Alright. Which date would you prefer? you can say something like book it on July first or August second etcetera";
            return getAskResponse("Ask Back",speechText);
        }
        if ("CallDoctor".equals(intentName)) {
            String speechText = "if this is emergency, do you want me to call 911?";
            return getResponse(speechText);
        } if ("AlertInvestigationIntent".equals(intentName)) {

            String speechText = service.getAlerts(requestEnvelope.getSession());
            return getResponse(speechText);
        } if ("CallDoctorConfirmation".equals(intentName)) {
            String slotValue = intent.getSlot("doctorName").getValue();
            String speechText = "Calling doctor "+ slotValue;
            return getResponse(speechText);
        } if ("DoNotCallEmergency".equals(intentName)) {
            String speechText = "I have following doctors on file, please let me know which doctor you want me to call. Doctor Parekh, Doctor James, Doctor Peter and Doctor Smith";
            return getResponse(speechText);
        } if ("GetTaskIntent".equals(intentName)) {
            String speechText = "You do not have any task at this moment";
            return getResponse(speechText);
        } if ("GoodBye".equals(intentName)) {
            String speechText = "Take Care. Good bye";
            return getResponse(speechText);
        }
        else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelpResponse();
        } else {
            return getAskResponse("HelloWorld", "This is unsupported.  Please try something else.");
        }
    }

    @Override
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        log.info("onSessionEnded requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
        // any cleanup logic goes here
    }

    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getWelcomeResponse() {
        String speechText = "Hello Tim. Welcome to USC Appointment Service. Would you like to schedule an appointment with Dr. Tami Howdeshell?";
        return getAskResponse("HelloWorld", speechText);
    }

    /**
     * Creates a {@code SpeechletResponse} for the hello intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getResponse(String speechText) {


        return getAskResponse("Careplaces", speechText);
    }

    /**
     * Creates a {@code SpeechletResponse} for the help intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getHelpResponse() {
        String speechText = "You can say hello to me!";
        return getAskResponse("HelloWorld", speechText);
    }

    /**
     * Helper method that creates a card object.
     * @param title title of the card
     * @param content body of the card
     * @return SimpleCard the display card to be sent along with the voice response.
     */
    private SimpleCard getSimpleCard(String title, String content) {
        SimpleCard card = new SimpleCard();
        card.setTitle(title);
        card.setContent(content);

        return card;
    }

    /**
     * Helper method for retrieving an OutputSpeech object when given a string of TTS.
     * @param speechText the text that should be spoken out to the user.
     * @return an instance of SpeechOutput.
     */
    private PlainTextOutputSpeech getPlainTextOutputSpeech(String speechText) {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return speech;
    }

    /**
     * Helper method that returns a reprompt object. This is used in Ask responses where you want
     * the user to be able to respond to your speech.
     * @param outputSpeech The OutputSpeech object that will be said once and repeated if necessary.
     * @return Reprompt instance.
     */
    private Reprompt getReprompt(OutputSpeech outputSpeech) {
        Reprompt reprompt = new Reprompt();

        reprompt.setOutputSpeech(outputSpeech);

        return reprompt;
    }

    /**
     * Helper method for retrieving an Ask response with a simple card and reprompt included.
     * @param cardTitle Title of the card that you want displayed.
     * @param speechText speech text that will be spoken to the user.
     * @return the resulting card and speech text.
     */
    private SpeechletResponse getAskResponse(String cardTitle, String speechText) {
        SimpleCard card = getSimpleCard(cardTitle, speechText);
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText);
        Reprompt reprompt = getReprompt(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }


}