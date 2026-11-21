package com.proyecto.servlet;

import com.proyecto.modelo.Venta;
import com.proyecto.servicio.Almacen;
import com.proyecto.servicio.NegocioException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/ventas")
public class VentasServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("ventas", almacen().listarVentas());
        req.setAttribute("ingresos", almacen().ingresosTotales());
        req.setAttribute("numeroVentas", almacen().numeroVentas());
        req.setAttribute("ticketPromedio", almacen().ticketPromedio());
        req.setAttribute("disponibles", almacen().pulserasDisponibles());
        req.setAttribute("metodosPago", Almacen.METODOS_PAGO);
        mostrar(req, resp, "ventas", "ventas");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] codigos = req.getParameterValues("codigo");
        String[] cantidades = req.getParameterValues("cantidad");
        try {
            Map<String, Integer> items = new LinkedHashMap<>();
            if (codigos != null && cantidades != null) {
                for (int i = 0; i < codigos.length && i < cantidades.length; i++) {
                    if (codigos[i].isBlank()) {
                        continue;
                    }
                    items.merge(codigos[i].trim(), Integer.parseInt(cantidades[i].trim()), Integer::sum);
                }
            }
            Venta venta = almacen().registrarVenta(items, param(req, "metodoPago"), usuario(req));
            exito(req, "Venta " + venta.getId() + " registrada por $" + String.format("%,.2f", venta.getTotal()));
        } catch (NumberFormatException e) {
            falla(req, "Las cantidades deben ser números enteros válidos");
        } catch (NegocioException e) {
            falla(req, e.getMessage());
        }
        redirigir(req, resp, "/ventas");
    }
}
