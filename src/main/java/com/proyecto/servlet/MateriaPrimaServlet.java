package com.proyecto.servlet;

import com.proyecto.modelo.MateriaPrima;
import com.proyecto.servicio.Almacen;
import com.proyecto.servicio.NegocioException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/materia-prima")
public class MateriaPrimaServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("materiales", almacen().filtrarMateriales(
                param(req, "q"), param(req, "categoria"), param(req, "unidad")));
        req.setAttribute("categorias", Almacen.CATEGORIAS);
        req.setAttribute("unidades", Almacen.UNIDADES);
        mostrar(req, resp, "materia-prima", "materia");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = param(req, "accion");
        try {
            switch (accion) {
                case "crear" -> {
                    MateriaPrima m = leerMaterial(req);
                    almacen().crearMaterial(m, usuario(req));
                    exito(req, "Material " + m.getId() + " registrado correctamente");
                }
                case "editar" -> {
                    MateriaPrima m = leerMaterial(req);
                    almacen().actualizarMaterial(m, usuario(req));
                    exito(req, "Material " + m.getId() + " actualizado correctamente");
                }
                case "reabastecer" -> {
                    double cantidad = Double.parseDouble(param(req, "cantidad"));
                    almacen().reabastecer(param(req, "id"), cantidad, usuario(req));
                    exito(req, "Stock reabastecido correctamente");
                }
                case "eliminar" -> {
                    almacen().eliminarMaterial(param(req, "id"), usuario(req));
                    exito(req, "Material eliminado del inventario");
                }
                default -> falla(req, "Acción no válida");
            }
        } catch (NumberFormatException e) {
            falla(req, "Verifica que los campos numéricos contengan números válidos");
        } catch (NegocioException e) {
            falla(req, e.getMessage());
        }
        redirigir(req, resp, "/materia-prima");
    }

    private MateriaPrima leerMaterial(HttpServletRequest req) throws NegocioException {
        MateriaPrima m = new MateriaPrima();
        m.setId(param(req, "id"));
        m.setNombre(param(req, "nombre"));
        m.setCategoria(param(req, "categoria"));
        m.setUnidadMedida(param(req, "unidadMedida"));
        try {
            m.setStockMinimo(Double.parseDouble(param(req, "stockMinimo")));
            String cantidad = param(req, "cantidadDisponible");
            m.setCantidadDisponible(cantidad.isEmpty() ? 0 : Double.parseDouble(cantidad));
        } catch (NumberFormatException e) {
            throw new NegocioException("Verifica que las cantidades sean números válidos");
        }
        return m;
    }
}
