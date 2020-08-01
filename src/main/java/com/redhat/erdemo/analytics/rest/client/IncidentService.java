package com.redhat.erdemo.analytics.rest.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.redhat.erdemo.analytics.model.Incident;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/incidents")
@RegisterRestClient(configKey="incident-service")
public interface IncidentService {

    @GET
    @Path("/incident/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<Incident> incidentById(@PathParam("id") String id);
}
