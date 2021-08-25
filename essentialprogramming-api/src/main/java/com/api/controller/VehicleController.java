package com.api.controller;

import com.api.config.Anonymous;
import com.api.service.VehicleService;
import com.config.ExecutorsProvider;
import com.exception.ExceptionHandler;
import com.util.async.Computation;
import com.util.exceptions.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

@Path("/vehicle")
public class VehicleController {
    private static final Logger LOG = LoggerFactory.getLogger(VehicleController.class);

    private VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GET
    @Path("/{plate}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Returns the vehicle with requested plate if found")
    @Anonymous
    public void getVehicle(@PathParam("plate") String plate, @Suspended AsyncResponse asyncResponse) {
        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> getVehicle(plate), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));

    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Returns a list with all the persisted vehicles")
    @Anonymous
    public void getVehicles(@Suspended AsyncResponse asyncResponse) {
        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> getAllVehicles(), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private Serializable getAllVehicles() {
        return (Serializable) vehicleService.getAllVehicles();
    }

    private Serializable getVehicle(String plate) throws ApiException {
        return vehicleService.getVehicle(plate);
    }
}