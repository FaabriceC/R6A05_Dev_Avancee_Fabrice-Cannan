package com.master.air.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;


@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {

    @GET
    @Path("helloWorld")
    public Response helloWorld() {
        return Response.ok(Map.of("message", "Hello World from MasterAnnonce API!")).build();
    }

    @GET
    @Path("params/{name}")
    public Response pathParam(@PathParam("name") String name) {
        return Response.ok(Map.of("message", "Hello " + name + "!", "type", "PathParam")).build();
    }

    @GET
    @Path("params")
    public Response queryParam(@QueryParam("name") @DefaultValue("World") String name,
                               @QueryParam("age") Integer age) {
        if (age != null) {
            return Response.ok(Map.of("message", "Hello " + name + "!", "age", age, "type", "QueryParam")).build();
        }
        return Response.ok(Map.of("message", "Hello " + name + "!", "type", "QueryParam")).build();
    }
}
