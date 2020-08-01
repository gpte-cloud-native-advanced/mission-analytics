package com.redhat.erdemo.analytics.rest.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.redhat.erdemo.analytics.model.Responder;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey="responder-service")
public interface ResponderService {

    @GET
    @Path("/responder/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<Responder> responder(@PathParam("id") String id);

}

