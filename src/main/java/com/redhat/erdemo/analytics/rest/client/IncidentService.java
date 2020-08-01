package com.redhat.erdemo.analytics.rest.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.redhat.erdemo.analytics.model.Incident;

@Path("/incidents")
@RegisterRestClient(configKey="incident-service")
public interface IncidentService {

    @GET
    @Path("/incident/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    Incident incidentById(@PathParam("id") String id);
}
