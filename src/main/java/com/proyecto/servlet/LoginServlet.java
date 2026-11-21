package com.proyecto.servlet;

import com.proyecto.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (usuario(req) != null) {
            redirigir(req, resp, "/inicio");
            return;
        }
        if (req.getParameter("recuperar") != null) {
            req.setAttribute("info", "Para recuperar tu contraseña solicita un restablecimiento al administrador del sistema.");
        }
        req.getRequestDispatcher("/WEB-INF/vistas/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String correo = param(req, "correo");
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        Usuario usuario = almacen().autenticar(correo, password);
        if (usuario == null) {
            req.setAttribute("error", "Correo o contraseña incorrectos");
            req.setAttribute("correo", correo);
            req.getRequestDispatcher("/WEB-INF/vistas/login.jsp").forward(req, resp);
            return;
        }
        req.changeSessionId();
        HttpSession sesion = req.getSession();
        sesion.setAttribute("usuario", usuario);
        if (req.getParameter("recordar") != null) {
            sesion.setMaxInactiveInterval(7 * 24 * 60 * 60);
        }
        redirigir(req, resp, "/inicio");
    }
}
