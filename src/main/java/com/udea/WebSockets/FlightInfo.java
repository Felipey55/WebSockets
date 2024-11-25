package com.udea.WebSockets;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightInfo {
    private String codigoDeVuelo;
    private String latitud;
    private String longitud;
    private String altitud;
    private String velocidad;
    private String curso;
}
