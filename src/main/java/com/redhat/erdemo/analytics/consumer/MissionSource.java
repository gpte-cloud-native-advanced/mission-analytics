package com.redhat.erdemo.analytics.consumer;

import java.math.BigDecimal;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import com.redhat.erdemo.analytics.model.Incident;
import com.redhat.erdemo.analytics.model.Mission;
import com.redhat.erdemo.analytics.model.Responder;
import com.redhat.erdemo.analytics.rest.client.IncidentService;
import com.redhat.erdemo.analytics.rest.client.ResponderService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.UnicastProcessor;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
@ApplicationScoped
public class MissionSource {

    private final static Logger log = LoggerFactory.getLogger(MissionSource.class);

    @Inject
    @RestClient
    IncidentService incidentService;

    @Inject
    @RestClient
    ResponderService responderService;

    private final UnicastProcessor<Mission> missionProcessor = UnicastProcessor.create();

    @POST
    @Path("/")
    public Response process(String payload, @Context HttpHeaders httpHeaders) {

        log.debug("ce-id: " + httpHeaders.getHeaderString("ce-id"));
        log.debug("ce-source: " + httpHeaders.getHeaderString("ce-source"));
        log.debug("ce-specversion: " + httpHeaders.getHeaderString("ce-specversion"));
        log.debug("ce-time: " + httpHeaders.getHeaderString("ce-time"));
        log.debug("ce-type: " + httpHeaders.getHeaderString("ce-type"));
        log.debug("ce-key: " + httpHeaders.getHeaderString("ce-key"));

        log.debug("Processing payload " + payload);

        JsonObject json = new JsonObject(payload);

        if (!"MissionCompletedEvent".equals(json.getString("messageType"))) {
            log.debug("Ignoring message with MessageType " + json.getString("messageType"));
            return Response.ok().build();
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

        missionProcessor.onNext(mission);

        return Response.ok().build();
    }

    @Outgoing("mission-data")
    Multi<Message<String>> produceMission() {
        return missionProcessor.onItem().apply(mission -> KafkaRecord.of(mission.getMissionId(), Json.encode(mission)));
    }
}
