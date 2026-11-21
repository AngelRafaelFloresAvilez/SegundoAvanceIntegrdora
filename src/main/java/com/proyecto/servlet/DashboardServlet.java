package com.proyecto.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/inicio")
public class DashboardServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("totalPiezas", almacen().totalPiezas());
        req.setAttribute("ventasHoy", almacen().ventasDelDia());
        req.setAttribute("valorInventario", almacen().valorInventario());
        req.setAttribute("recientes", almacen().recientes(6));
        mostrar(req, resp, "inicio", "inicio");
    }
}
