package com.api.controller;

import com.api.config.Anonymous;
import com.config.ExecutorsProvider;
import com.exception.ExceptionHandler;
import com.util.async.Computation;
import com.util.enums.Language;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

@Path("/")
public class WelcomeController {

    private static final Logger LOG = LoggerFactory.getLogger(WelcomeController.class);

    @Context
    private Language language;

    @Context
    ServletContext servletContext;

    public WelcomeController() {
        LOG.info("Starting..");
    }


    //........................................................................................................................
    //Test purpose only
    @GET
    @Path("test/{name}")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Test purpose only", tags = {"Test purpose only",})
    @Anonymous
    public void test(@HeaderParam("Accept-Language") String lang, @PathParam("name") String name, @Suspended AsyncResponse asyncResponse) {
        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(this::test, executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));

    }

    private Language test() throws IOException {
        return language;
    }
}
