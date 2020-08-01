package com.redhat.erdemo.analytics.consumer;

import java.math.BigDecimal;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.redhat.erdemo.analytics.model.Incident;
import com.redhat.erdemo.analytics.model.Mission;
import com.redhat.erdemo.analytics.model.Responder;
import com.redhat.erdemo.analytics.rest.client.IncidentService;
import com.redhat.erdemo.analytics.rest.client.ResponderService;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MissionSource {

    private final static Logger log = LoggerFactory.getLogger(MissionSource.class);

    @Inject
    @RestClient
    IncidentService incidentService;

    @Inject
    @RestClient
    ResponderService responderService;

    @Incoming("mission-event")
    @Outgoing("mission-data")
    @Blocking
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    public Message<String> process(Message<String> payload) {

        log.debug("Processing payload " + payload.getPayload());

        JsonObject json = new JsonObject(payload.getPayload());

        if (!"MissionCompletedEvent".equals(json.getString("messageType"))) {
            log.debug("Ignoring message with MessageType " + json.getString("messageType"));
            return null;
        }

        JsonObject body = json.getJsonObject("body");

        JsonArray locationHistory = body.getJsonArray("responderLocationHistory");
        JsonObject dropoff = locationHistory.getJsonObject(locationHistory.size() - 1);

        String missionId = body.getString("id");

        String incidentId = body.getString("incidentId");
        String responderId = body.getString("responderId");

        Incident incident = incidentService.incidentById(incidentId);

        Responder responder = responderService.responder(responderId);

        Mission mission = Mission.builder(missionId).incidentId(incident.getId()).incidentLatitude(new BigDecimal(incident.getLat()))
                .incidentLongitude(new BigDecimal(incident.getLon())).incidentName(incident.getVictimName())
                .incidentNumberOfPeople(incident.getNumberOfPeople()).incidentMedicalNeeded(incident.isMedicalNeeded())
                .incidentTimeStamp(incident.getTimestamp()).responderId(responder.getId()).responderName(responder.getName())
                .destinationLatitude(BigDecimal.valueOf(body.getDouble("destinationLat")))
                .destinationLongitude(BigDecimal.valueOf(body.getDouble("destinationLong")))
                .missionCompletedTimeStamp(dropoff.getLong("timestamp")).build();

        return KafkaRecord.of(missionId, Json.encode(mission));
    }

}
