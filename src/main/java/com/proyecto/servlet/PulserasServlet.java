package com.proyecto.servlet;

import com.proyecto.modelo.Pulsera;
import com.proyecto.servicio.Almacen;
import com.proyecto.servicio.NegocioException;
import com.proyecto.servicio.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/pulseras")
public class PulserasServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("pulseras", almacen().filtrarPulseras(
                param(req, "q"), param(req, "piedra"), param(req, "talla"), param(req, "estado")));
        req.setAttribute("piedras", almacen().piedras());
        req.setAttribute("tallas", Almacen.TALLAS);
        req.setAttribute("disenos", Almacen.DISENOS);
        mostrar(req, resp, "pulseras", "pulseras");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!esAdmin(req)) {
            falla(req, "No tienes permiso para realizar esta acción");
            redirigir(req, resp, "/pulseras");
            return;
        }
        String accion = param(req, "accion");
        try {
            switch (accion) {
                case "crear" -> {
                    Pulsera p = leerPulsera(req);
                    almacen().crearPulsera(p, usuario(req));
                    exito(req, "Pulsera " + p.getCodigo() + " registrada correctamente");
                }
                case "editar" -> {
                    Pulsera p = leerPulsera(req);
                    almacen().actualizarPulsera(p, usuario(req));
                    exito(req, "Pulsera " + p.getCodigo() + " actualizada correctamente");
                }
                case "producir" -> {
                    int cantidad = Integer.parseInt(param(req, "cantidad"));
                    almacen().producir(param(req, "codigo"), cantidad, usuario(req));
                    exito(req, "Se agregaron " + cantidad + " pieza(s) al stock y se descontó la materia prima");
                }
                case "eliminar" -> {
                    almacen().eliminarPulsera(param(req, "codigo"), usuario(req));
                    exito(req, "Pulsera eliminada del inventario");
                }
                default -> falla(req, "Acción no válida");
            }
        } catch (NumberFormatException e) {
            falla(req, "Verifica que los campos numéricos contengan números válidos");
        } catch (NegocioException e) {
            falla(req, e.getMessage());
        }
        redirigir(req, resp, "/pulseras");
    }

    private Pulsera leerPulsera(HttpServletRequest req) throws NegocioException {
        Pulsera p = new Pulsera();
        p.setCodigo(param(req, "codigo"));
        p.setNombre(param(req, "nombre"));
        p.setTipoPiedra(param(req, "tipoPiedra"));
        p.setDiseno(param(req, "diseno"));
        p.setTalla(param(req, "talla"));
        p.setDescripcion(param(req, "descripcion"));
        try {
            p.setPrecioVenta(Double.parseDouble(param(req, "precioVenta")));
            p.setStockMinimo(Integer.parseInt(param(req, "stockMinimo")));
            String stock = param(req, "stockActual");
            p.setStockActual(stock.isEmpty() ? 0 : Integer.parseInt(stock));
        } catch (NumberFormatException e) {
            throw new NegocioException("Verifica que el precio y las existencias sean números válidos");
        }
        p.setReceta(Util.leerReceta(param(req, "receta")));
        return p;
    }
}
