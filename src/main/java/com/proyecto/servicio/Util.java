package com.proyecto.servicio;

import com.proyecto.modelo.Insumo;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static String numero(double valor) {
        if (valor == Math.rint(valor)) {
            return String.valueOf((long) valor);
        }
        return String.valueOf(Math.round(valor * 100) / 100.0);
    }

    public static double redondear(double valor) {
        return Math.round(valor * 100) / 100.0;
    }

    public static List<Insumo> leerReceta(String texto) throws NegocioException {
        List<Insumo> receta = new ArrayList<>();
        if (texto == null || texto.isBlank()) {
            return receta;
        }
        for (String par : texto.split(",")) {
            String[] partes = par.trim().split(":");
            if (partes.length != 2) {
                throw new NegocioException("La receta debe tener el formato MP-01:21, MP-06:0.5");
            }
            try {
                double cantidad = Double.parseDouble(partes[1].trim());
                if (cantidad <= 0) {
                    throw new NegocioException("Las cantidades de la receta deben ser mayores a cero");
                }
                receta.add(new Insumo(partes[0].trim(), cantidad));
            } catch (NumberFormatException e) {
                throw new NegocioException("La receta debe tener el formato MP-01:21, MP-06:0.5");
            }
        }
        return receta;
    }
}
