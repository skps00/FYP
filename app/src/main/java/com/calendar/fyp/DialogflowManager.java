package com.calendar.fyp;

import android.content.Context;
import android.util.Log;

import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.v2.*;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

public class DialogflowManager {
    // Replace this with the name of your Dialogflow agent.
    private static final String AGENT_NAME = "test-aviv";
    private SessionsClient sessionsClient;
    private SessionName sessionName;
    private String TAG = "DialogflowAI";
    private response response;




    public DialogflowManager(Context context) {
        try {
            InputStream stream = context.getAssets().open("credential.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream);
            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings settings = settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(settings);
            sessionName = SessionName.of(AGENT_NAME, "115847436105478637338");
            stream.close();
        } catch (Exception e) {
            Log.v(TAG, ""+e);
        }
    }

    public response sendQuery(String message, String lang) {
        try {
            TextInput.Builder textInput = TextInput.newBuilder()
                    .setText(message)
                    .setLanguageCode(lang);

            QueryInput queryInput = QueryInput.newBuilder()
                    .setText(textInput)
                    .build();

            DetectIntentRequest detectIntentRequest = DetectIntentRequest.newBuilder()
                    .setSession(sessionName.toString())
                    .setQueryInput(queryInput)
                    .build();
            DetectIntentResponse responsefromdialog = sessionsClient.detectIntent(detectIntentRequest);
            QueryResult queryResult = responsefromdialog.getQueryResult();
            Log.v(TAG, "user sended: "+ queryResult.getQueryText());

            response = new response(queryResult.getIntent().getDisplayName(), queryResult.getFulfillmentText());
//            Log.v(TAG, "Fulfill: " + response.getFulfillmentText());
            Log.v(TAG, "Intent: " +queryResult.getIntent().getDisplayName());

            response.setParameters(queryResult.getParameters().getFieldsMap());

            // Extract entity values

            Log.v(TAG, "parameters: " + response.getParameters());



            //get start date time
            if (response.getParameters().containsKey("date-time")) {
                Value dateTimeValue = response.getParameters().get("date-time");
                Log.v(TAG, "get Start time:" + dateTimeValue.getStringValue());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx");


                if (dateTimeValue.hasStructValue()){

                    Struct struct = dateTimeValue.getStructValue();
                    switch (struct.getFieldsCount()){
                        case 1:
                            if (dateTimeValue.hasListValue()){
                                Map<String, Value> valueData = dateTimeValue.getListValue().getValues(0).getStructValue().getFieldsMap();
                                // 进一步处理或使用valueData
                                Map.Entry<String, Value> entry = valueData.entrySet().iterator().next();

                                if (DateChecker(entry.getValue().getStringValue()).getresultboolean()){
                                    response.setStartDateTime(LocalDateTime.parse(entry.getValue().getStringValue(), formatter));
                                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(entry.getValue().getStringValue(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                                    response.setStartDateTimeWithZone(zonedDateTime);
                                }else {
                                    Log.v(TAG, "Fuck Up1");
                                }
                            }
                            if (dateTimeValue.hasStructValue()) {
                                Map<String, Value> valueData = dateTimeValue.getStructValue().getFieldsMap();
                                // 进一步处理或使用valueData
                                Map.Entry<String, Value> entry = valueData.entrySet().iterator().next();
                                if (DateChecker(entry.getValue().getStringValue()).getresultboolean()){
                                    response.setStartDateTime(LocalDateTime.parse(entry.getValue().getStringValue(), formatter));
                                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(entry.getValue().getStringValue(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                                    response.setStartDateTimeWithZone(zonedDateTime);
                                }else {
                                    Log.v(TAG, "Fuck Up2");
                                }
//                                Log.v(TAG, "struct Value");
                            }
                            break;
                        case 2:
//                        Log.v(TAG, "time2: " + struct.getFieldsMap());
                            Map<String, Value> timePeriod = struct.getFieldsMap();
//                            Log.v(TAG, "test: " + timePeriod);
                            if (timePeriod.containsKey("startDateTime") && timePeriod.containsKey("endDateTime")){

                                if (DateChecker(timePeriod.get("startDateTime").getStringValue()).getresultboolean() && DateChecker(timePeriod.get("endDateTime").getStringValue()).getresultboolean()){
                                    response.setStartDateTime(LocalDateTime.parse(timePeriod.get("startDateTime").getStringValue(), formatter));
                                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(timePeriod.get("startDateTime").getStringValue(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                                    response.setStartDateTimeWithZone(zonedDateTime);
                                    response.setEndDateTime(LocalDateTime.parse(timePeriod.get("endDateTime").getStringValue(), formatter));
                                    zonedDateTime = ZonedDateTime.parse(timePeriod.get("endDateTime").getStringValue(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                                    response.setEndDateTimeWithZone(zonedDateTime);
                                }else {
                                    Log.v(TAG, "Fuck Up3");
                                }


                            } else if (timePeriod.containsKey("startDate") && timePeriod.containsKey("endDate")) {
                                if (DateChecker(timePeriod.get("startDate").getStringValue()).getresultboolean() && DateChecker(timePeriod.get("endDate").getStringValue()).getresultboolean()){
                                    response.setStartDateTime(LocalDateTime.parse(timePeriod.get("startDate").getStringValue(), formatter));
                                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(timePeriod.get("startDate").getStringValue(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                                    response.setStartDateTimeWithZone(zonedDateTime);
                                    response.setEndDateTime(LocalDateTime.parse(timePeriod.get("endDate").getStringValue(), formatter));
                                    zonedDateTime = ZonedDateTime.parse(timePeriod.get("endDate").getStringValue(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                                    response.setEndDateTimeWithZone(zonedDateTime);
                                }else {
                                    Log.v(TAG, "Fuck Up4");
                                }
                            }
                            break;
                    }
                }
                if(dateTimeValue.getStringValue() != "" || !dateTimeValue.getStringValue().isEmpty()){
                    if (DateChecker(dateTimeValue.getStringValue()).getresultboolean()){
                        response.setStartDateTime(LocalDateTime.parse(dateTimeValue.getStringValue(), formatter));
                        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTimeValue.getStringValue(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                        response.setStartDateTimeWithZone(zonedDateTime);
                    }else {
                        Log.v(TAG, "Fuck Up5");
                    }
                }

            }else {
//                Log.v(TAG, "FUCKKKKKKKKK");
            }





            //get updated start date time
            if (response.getParameters().containsKey("UpDate")) {
                Value dateTimeValue = response.getParameters().get("UpDate");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx");
                Struct struct = dateTimeValue.getStructValue();
                ZonedDateTime zonedDateTime;
//                Log.v(TAG, "Done uptime: " + dateTimeValue);
                if (dateTimeValue.hasListValue()){
                    Map<String, Value> valueData = dateTimeValue.getListValue().getValues(0).getStructValue().getFieldsMap();
                    // 进一步处理或使用valueData
                    Map.Entry<String, Value> entry = valueData.entrySet().iterator().next();
                    response.setUpdateStartDateTime(LocalDateTime.parse(entry.getValue().getStringValue(), formatter));
                    zonedDateTime = ZonedDateTime.parse(entry.getValue().getStringValue(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                    response.setUpdateStartDateTimeWithZone(zonedDateTime);
                }
                if (dateTimeValue.hasStructValue()) {
                    switch (struct.getFieldsCount()){
                        case 1:
                            Map<String, Value> valueData = dateTimeValue.getStructValue().getFieldsMap();
                            // 进一步处理或使用valueData
                            Map.Entry<String, Value> entry = valueData.entrySet().iterator().next();
                            response.setUpdateStartDateTime(LocalDateTime.parse(entry.getValue().getStringValue(), formatter));
                            zonedDateTime = ZonedDateTime.parse(entry.getValue().getStringValue(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                            response.setUpdateStartDateTimeWithZone(zonedDateTime);
                            break;
                        case 2:
//                            Log.v(TAG, "" + struct.getFieldsMap().getClass().getSimpleName());
                            Map<String, Value> timePeriod = struct.getFieldsMap();
                            response.setUpdateStartDateTime(LocalDateTime.parse(timePeriod.get("startDateTime").getStringValue(), formatter));
                            zonedDateTime = ZonedDateTime.parse(timePeriod.get("startDateTime").getStringValue(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                            response.setUpdateStartDateTimeWithZone(zonedDateTime);
                            response.setUpdateEndDateTime(LocalDateTime.parse(timePeriod.get("endDateTime").getStringValue(), formatter));
                            zonedDateTime = ZonedDateTime.parse(timePeriod.get("endDateTime").getStringValue(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                            response.setUpdateEndDateTimeWithZone(zonedDateTime);
//                            Log.v(TAG, "Done uptime");

                            break;
                    }
                }


            }

            //get update Title
            if (response.getParameters().containsKey("UpTitle")) {
                Value titleValue = response.getParameters().get("UpTitle");
                response.setUpdateTitle(titleValue.getStringValue());
                Log.v(TAG, "Done uptitle");
            }

            //get category
            if (response.getParameters().containsKey("category")) {
                Value categoryValue = response.getParameters().get("category");
                response.setCategory(categoryValue.getStringValue());
//                Log.v(TAG, "Done category");
//                Log.v(TAG, categoryValue.toString());
            }

            if (response.getParameters().containsKey("updatecategory")) {
                Value categoryValue = response.getParameters().get("updatecategory");
                response.setUpdateCategory(categoryValue.getStringValue());
//                Log.v(TAG, "Done category");
//                Log.v(TAG, categoryValue.toString());
            }

//            Log.v(TAG, "title: " + response.getParameters().containsKey("title"));
            //get title
            if (response.getParameters().containsKey("title")) {
                Value titleValue = response.getParameters().get("title");
                response.setTitle(titleValue.getStringValue());
            }

//            Log.v(TAG, "return: " + response);
            return response;
        }catch (Exception e){
            Log.v(TAG, "Error: " + e);
            return response;
        }
    }

    public class Result {
        private String stringValue;
        private boolean resultboolean;

        public Result(String stringValue, Boolean resultboolean) {
            this.stringValue = stringValue;
            this.resultboolean = resultboolean;
        }

        public String getStringValue() {
            return stringValue;
        }

        public boolean getresultboolean() {
            return resultboolean;
        }
    }
    public Result DateChecker (String input) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx");
        try {
            LocalDateTime.parse(input, formatter);
            return new Result(input, true);
        } catch (DateTimeParseException e) {
            return new Result(input, false);
        }

    }
}
