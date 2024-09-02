package com.calendar.fyp;

import com.google.protobuf.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;

public class response {
    private String Action;
    private String FulfillmentText;
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private ZonedDateTime startDateTimeWithZone;
    private ZonedDateTime EndDateTimeWithZone;



    private Map<String, Value> parameters;



    private String updateTitle;
    private String updateDescription;
    private LocalDateTime updateStartDateTime;
    private LocalDateTime updateEndDateTime;
    private ZonedDateTime updateStartDateTimeWithZone;
    private ZonedDateTime updateEndDateTimeWithZone;



    private String category;
    private String updateCategory;


    private LocalDateTime FilterStartTime;
    private LocalDateTime FilterEndTime;


    public response(String action, String fulfillmentText) {
        this.Action = action;
        this.FulfillmentText = fulfillmentText;
    }

    public String getUpdateCategory() {
        return updateCategory;
    }

    public void setUpdateCategory(String updateCategory) {
        this.updateCategory = updateCategory;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getFilterStartTime() {
        return FilterStartTime;
    }

    public void setFilterStartTime(LocalDateTime filterStartTime) {
        FilterStartTime = filterStartTime;
    }

    public LocalDateTime getFilterEndTime() {
        return FilterEndTime;
    }

    public void setFilterEndTime(LocalDateTime filterEndTime) {
        FilterEndTime = filterEndTime;
    }

    public String getUpdateTitle() {
        return updateTitle;
    }

    public void setUpdateTitle(String updateTitle) {
        this.updateTitle = updateTitle;
    }

    public String getUpdateDescription() {
        return updateDescription;
    }

    public void setUpdateDescription(String updateDescription) {
        this.updateDescription = updateDescription;
    }

    public LocalDateTime getUpdateStartDateTime() {
        return updateStartDateTime;
    }

    public void setUpdateStartDateTime(LocalDateTime updateStartDateTime) {
        this.updateStartDateTime = updateStartDateTime;
    }

    public LocalDateTime getUpdateEndDateTime() {
        return updateEndDateTime;
    }

    public void setUpdateEndDateTime(LocalDateTime updateEndDateTime) {
        this.updateEndDateTime = updateEndDateTime;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public Map<String, Value> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Value> parameters) {
        this.parameters = parameters;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        this.Action = action;
    }

    public String getFulfillmentText() {
        return FulfillmentText;
    }

    public void setFulfillmentText(String fulfillmentText) {
        this.FulfillmentText = fulfillmentText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getStartDateTimeWithZone() {
        return startDateTimeWithZone;
    }

    public void setStartDateTimeWithZone(ZonedDateTime startDateTimeWithZone) {
        this.startDateTimeWithZone = startDateTimeWithZone;
    }

    public ZonedDateTime getEndDateTimeWithZone() {
        return EndDateTimeWithZone;
    }

    public void setEndDateTimeWithZone(ZonedDateTime endDateTimeWithZone) {
        EndDateTimeWithZone = endDateTimeWithZone;
    }

    public ZonedDateTime getUpdateStartDateTimeWithZone() {
        return updateStartDateTimeWithZone;
    }

    public void setUpdateStartDateTimeWithZone(ZonedDateTime updateStartDateTimeWithZone) {
        this.updateStartDateTimeWithZone = updateStartDateTimeWithZone;
    }

    public ZonedDateTime getUpdateEndDateTimeWithZone() {
        return updateEndDateTimeWithZone;
    }

    public void setUpdateEndDateTimeWithZone(ZonedDateTime updateEndDateTimeWithZone) {
        this.updateEndDateTimeWithZone = updateEndDateTimeWithZone;
    }
}
