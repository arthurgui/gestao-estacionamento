package com.mazza.tech.gestao.estacionamento.router;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class GarageRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:parkVehicle")
            .routeId("park-vehicle-route")
            .log("Vehicle parked: ${body.licensePlate}");

        from("direct:processVehicleEvent")
            .routeId("process-vehicle-event")
            .log("Processing ${body.eventType} for ${body.licensePlate}");
    }
}
