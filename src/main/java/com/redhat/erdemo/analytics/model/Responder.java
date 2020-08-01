package com.redhat.erdemo.analytics.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Responder {

    private String id;

    private String name;

    private String phoneNumber;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Integer boatCapacity;

    private Boolean medicalKit;

    private Boolean available;

    private Boolean person;

    private Boolean enrolled;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public Integer getBoatCapacity() {
        return boatCapacity;
    }

    public Boolean isMedicalKit() {
        return medicalKit;
    }

    public Boolean isAvailable() {
        return available;
    }

    public Boolean isPerson() {
        return person;
    }

    public Boolean isEnrolled() {
        return enrolled;
    }

}
