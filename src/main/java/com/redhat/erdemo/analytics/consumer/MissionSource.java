package com.redhat.erdemo.analytics.consumer;

import java.math.BigDecimal;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.redhat.erdemo.analytics.model.Mission;
import com.redhat.erdemo.analytics.rest.client.IncidentService;
import com.redhat.erdemo.analytics.rest.client.ResponderService;
import io.smallrye.mutiny.Uni;
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
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    public Uni<Message<String>> process(Message<String> payload) {

        log.debug("Processing payload " + payload.getPayload());

        JsonObject json = new JsonObject(payload.getPayload());

        if (!"MissionCompletedEvent".equals(json.getString("messageType"))) {
            log.debug("Ignoring message with MessageType " + json.getString("messageType"));
            return Uni.createFrom().nullItem();
        }

        JsonObject body = json.getJsonObject("body");

        JsonArray locationHistory = body.getJsonArray("responderLocationHistory");

        JsonObject dropoff = locationHistory.getJsonObject(locationHistory.size() - 1);

        String missionId = body.getString("id");

        String incidentId = body.getString("incidentId");
        String responderId = body.getString("responderId");

        return Uni.createFrom().item(() -> Mission.builder(missionId).destinationLatitude(BigDecimal.valueOf(body.getDouble("destinationLat")))
                .destinationLongitude(BigDecimal.valueOf(body.getDouble("destinationLong")))
                .missionCompletedTimeStamp(dropoff.getLong("timestamp")))
                .onItem().transformToUni(builder -> incidentService.incidentById(incidentId).map(incident -> builder.incidentId(incident.getId()).incidentLatitude(new BigDecimal(incident.getLat()))
                        .incidentLongitude(new BigDecimal(incident.getLon())).incidentName(incident.getVictimName())
                        .incidentNumberOfPeople(incident.getNumberOfPeople()).incidentMedicalNeeded(incident.isMedicalNeeded())
                        .incidentTimeStamp(incident.getTimestamp())))
                .onItem().transformToUni(builder -> responderService.responder(responderId).map(responder -> builder.responderId(responder.getId()).responderName(responder.getName())))
                .map(Mission.Builder::build).map(mission -> (Message<String>) KafkaRecord.of(missionId, Json.encode(mission)))
                .onFailure().recoverWithUni(t -> {
                    log.error("Exception processing message with mission id " + missionId, t);
                    return Uni.createFrom().nullItem();
                });
    }
}
