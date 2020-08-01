package com.redhat.erdemo.analytics.model;

import java.math.BigDecimal;

public class Mission {

    private String missionId;

    private String incidentId;

    private String incidentName;

    private BigDecimal incidentLatitude;

    private BigDecimal incidentLongitude;

    private Integer incidentNumberOfPeople;

    private Boolean incidentMedicalNeeded;

    private Long incidentTimestamp;

    private String responderId;

    private String responderName;

    private BigDecimal destinationLatitude;

    private BigDecimal destinationLongitude;

    private Long missionCompetedTimestamp;

    public String getMissionId() {
        return missionId;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public String getIncidentName() {
        return incidentName;
    }

    public BigDecimal getIncidentLatitude() {
        return incidentLatitude;
    }

    public BigDecimal getIncidentLongitude() {
        return incidentLongitude;
    }

    public Integer getIncidentNumberOfPeople() {
        return incidentNumberOfPeople;
    }

    public Boolean getIncidentMedicalNeeded() {
        return incidentMedicalNeeded;
    }

    public Long getIncidentTimestamp() {
        return incidentTimestamp;
    }

    public String getResponderId() {
        return responderId;
    }

    public String getResponderName() {
        return responderName;
    }

    public BigDecimal getDestinationLatitude() {
        return destinationLatitude;
    }

    public BigDecimal getDestinationLongitude() {
        return destinationLongitude;
    }

    public Long getMissionCompetedTimestamp() {
        return missionCompetedTimestamp;
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static class Builder {

        private final Mission mission;

        public Builder(String id) {
            this.mission = new Mission();
            mission.missionId = id;
        }

        public Builder incidentId(String responderId) {
            mission.incidentId = responderId;
            return this;
        }

        public Builder incidentName(String name) {
            mission.incidentName = name;
            return this;
        }

        public Builder incidentLatitude(BigDecimal latitude) {
            mission.incidentLatitude = latitude;
            return this;
        }

        public Builder incidentLongitude(BigDecimal longitude) {
            mission.incidentLongitude = longitude;
            return this;
        }

        public Builder incidentNumberOfPeople(Integer numberOfPeople) {
            mission.incidentNumberOfPeople = numberOfPeople;
            return this;
        }

        public Builder incidentMedicalNeeded(Boolean medicalNeeded) {
            mission.incidentMedicalNeeded = medicalNeeded;
            return this;
        }

        public Builder incidentTimeStamp(Long timestamp) {
            mission.incidentTimestamp = timestamp;
            return this;
        }

        public Builder responderId(String responderId) {
            mission.responderId = responderId;
            return this;
        }

        public Builder responderName(String responderName) {
            mission.responderName = responderName;
            return this;
        }

        public Builder destinationLatitude(BigDecimal latitude) {
            mission.destinationLatitude = latitude;
            return this;
        }

        public Builder destinationLongitude(BigDecimal longitude) {
            mission.destinationLongitude = longitude;
            return this;
        }

        public Builder missionCompletedTimeStamp(Long timestamp) {
            mission.missionCompetedTimestamp = timestamp;
            return this;
        }

        public Mission build() {
            return mission;
        }

    }
}
