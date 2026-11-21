package com.proyecto.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Venta {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final String id;
    private final LocalDateTime fecha;
    private final List<DetalleVenta> detalles;
    private final String metodoPago;
    private final double total;
    private final String vendedorId;

    public Venta(String id, LocalDateTime fecha, List<DetalleVenta> detalles, String metodoPago, String vendedorId) {
        this.id = id;
        this.fecha = fecha;
        this.detalles = detalles;
        this.metodoPago = metodoPago;
        this.vendedorId = vendedorId;
        double suma = 0;
        for (DetalleVenta d : detalles) {
            suma += d.getSubtotal();
        }
        this.total = suma;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getFechaTexto() {
        return fecha.format(FORMATO);
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public double getTotal() {
        return total;
    }

    public String getVendedorId() {
        return vendedorId;
    }

    public int getCantidadTotal() {
        int suma = 0;
        for (DetalleVenta d : detalles) {
            suma += d.getCantidad();
        }
        return suma;
    }

    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        for (DetalleVenta d : detalles) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(d.getCantidad()).append(" × ").append(d.getNombrePulsera());
        }
        return sb.toString();
    }
}
