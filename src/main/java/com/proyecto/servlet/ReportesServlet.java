package com.proyecto.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/reportes")
public class ReportesServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int dias = 7;
        try {
            dias = Integer.parseInt(param(req, "dias"));
        } catch (NumberFormatException e) {
            dias = 7;
        }
        if (dias != 7 && dias != 30 && dias != 0) {
            dias = 7;
        }
        req.setAttribute("dias", dias);
        req.setAttribute("top", almacen().topPulseras(dias, 5));
        req.setAttribute("porPiedra", almacen().ventasPorPiedra(dias));
        req.setAttribute("valuacion", almacen().valuacionInventario());
        req.setAttribute("valorInventario", almacen().valorInventario());
        req.setAttribute("criticos", almacen().listarAlertas());
        req.setAttribute("movimientos", almacen().ultimosMovimientos(15));
        mostrar(req, resp, "reportes", "reportes");
    }
}
