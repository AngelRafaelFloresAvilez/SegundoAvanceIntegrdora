package com.proyecto.modelo;

import com.proyecto.servicio.Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Movimiento {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final LocalDateTime fecha;
    private final String tipo;
    private final String categoria;
    private final String referencia;
    private final String detalle;
    private final double cantidad;
    private final String usuario;

    public Movimiento(LocalDateTime fecha, String tipo, String categoria, String referencia,
                      String detalle, double cantidad, String usuario) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.categoria = categoria;
        this.referencia = referencia;
        this.detalle = detalle;
        this.cantidad = cantidad;
        this.usuario = usuario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getFechaTexto() {
        return fecha.format(FORMATO);
    }

    public String getTipo() {
        return tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getReferencia() {
        return referencia;
    }

    public String getDetalle() {
        return detalle;
    }

    public String getCantidadTexto() {
        return Util.numero(cantidad);
    }

    public String getUsuario() {
        return usuario;
    }
}
