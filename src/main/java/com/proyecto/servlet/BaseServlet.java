package com.proyecto.servlet;

import com.proyecto.modelo.Usuario;
import com.proyecto.servicio.Almacen;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {

    protected Almacen almacen() {
        return Almacen.obtener();
    }

    protected Usuario usuario(HttpServletRequest req) {
        HttpSession sesion = req.getSession(false);
        return sesion == null ? null : (Usuario) sesion.getAttribute("usuario");
    }

    protected boolean esAdmin(HttpServletRequest req) {
        Usuario u = usuario(req);
        return u != null && u.isAdmin();
    }

    protected String param(HttpServletRequest req, String nombre) {
        String valor = req.getParameter(nombre);
        return valor == null ? "" : valor.trim();
    }

    protected void exito(HttpServletRequest req, String mensaje) {
        req.getSession().setAttribute("flashOk", mensaje);
    }

    protected void falla(HttpServletRequest req, String mensaje) {
        req.getSession().setAttribute("flashError", mensaje);
    }

    protected void redirigir(HttpServletRequest req, HttpServletResponse resp, String ruta) throws IOException {
        resp.sendRedirect(req.getContextPath() + ruta);
    }

    protected void mostrar(HttpServletRequest req, HttpServletResponse resp, String vista, String activo)
            throws ServletException, IOException {
        HttpSession sesion = req.getSession();
        req.setAttribute("mensajeOk", sesion.getAttribute("flashOk"));
        req.setAttribute("mensajeError", sesion.getAttribute("flashError"));
        sesion.removeAttribute("flashOk");
        sesion.removeAttribute("flashError");
        req.setAttribute("ctx", req.getContextPath());
        req.setAttribute("activo", activo);
        req.setAttribute("totalAlertas", almacen().cantidadAlertas());
        req.getRequestDispatcher("/WEB-INF/vistas/" + vista + ".jsp").forward(req, resp);
    }
}
