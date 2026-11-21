package com.proyecto.modelo;

import com.proyecto.servicio.Util;

public class Insumo {

    private final String idMaterial;
    private final double cantidadRequerida;

    public Insumo(String idMaterial, double cantidadRequerida) {
        this.idMaterial = idMaterial;
        this.cantidadRequerida = cantidadRequerida;
    }

    public String getIdMaterial() {
        return idMaterial;
    }

    public double getCantidadRequerida() {
        return cantidadRequerida;
    }

    public String getTexto() {
        return idMaterial + ":" + Util.numero(cantidadRequerida);
    }
}
