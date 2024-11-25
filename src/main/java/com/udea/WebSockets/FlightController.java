package com.udea.WebSockets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "http://localhost:3000")
public class FlightController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public FlightController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Recibir información desde el cliente (React) y enviar a todos los suscriptores
    @MessageMapping("/updateFlight")
    @SendTo("/topic/flights")
    public FlightInfo updateFlightInfo(FlightInfo flightInfo) throws Exception {
        // Opcionalmente, podrías realizar aquí validaciones o procesos adicionales antes de enviar los datos
        System.out.println("Actualizando información del vuelo: " + flightInfo.getCodigoDeVuelo());
        messagingTemplate.convertAndSend("/topic/flights", flightInfo);

        // Devuelve la información del vuelo a todos los suscriptores
        return new FlightInfo(
                HtmlUtils.htmlEscape(flightInfo.getCodigoDeVuelo()),
                flightInfo.getLatitud(),
                flightInfo.getLongitud(),
                flightInfo.getAltitud(),
                flightInfo.getVelocidad(),
                HtmlUtils.htmlEscape(flightInfo.getCurso())
        );
    }

    // Método para enviar actualizaciones programáticas (o desde otro servicio)
    public void sendUpdate(FlightInfo flightInfo) {
        // Envía los datos actualizados del vuelo a todos los suscriptores en /topic/flights
        messagingTemplate.convertAndSend("/topic/flights", flightInfo);
    }

    private Map<String, FlightInfo> flightData = new ConcurrentHashMap<>();

    // Método para actualizar la información de un vuelo
    @PostMapping("/update")
    public ResponseEntity<String> updateFlight(@RequestBody FlightInfo flight) {
        // Actualiza los datos del vuelo
        flightData.put(flight.getCodigoDeVuelo(), flight);

        // Enviar la actualización a todos los suscriptores WebSocket
        messagingTemplate.convertAndSend("/topic/flights", flight);

        return ResponseEntity.ok("Información del vuelo actualizada correctamente");
    }

    @GetMapping("/{codigoDeVuelo}")
    public ResponseEntity<FlightInfo> getFlightInfo(@PathVariable String codigoDeVuelo) {
        FlightInfo flight = flightData.get(codigoDeVuelo);
        return ResponseEntity.ok(flight);
    }
}
