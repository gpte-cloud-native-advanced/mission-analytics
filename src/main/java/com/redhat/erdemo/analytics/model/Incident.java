package com.redhat.erdemo.analytics.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Incident {

    private String id;

    private String lat;

    private String lon;

    private int numberOfPeople;

    private boolean medicalNeeded;

    private String victimName;

    private long timestamp;

    public String getId() {
        return id;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

     public Integer getNumberOfPeople() {
        return numberOfPeople;
    }

    public Boolean isMedicalNeeded() {
        return medicalNeeded;
    }

    public String getVictimName() {
        return victimName;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
