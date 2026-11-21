package com.proyecto.modelo;

public class Usuario {

    private final String id;
    private final String nombre;
    private final String correo;
    private final String password;
    private final String rol;

    public Usuario(String id, String nombre, String correo, String password, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
        this.rol = rol;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getPassword() {
        return password;
    }

    public String getRol() {
        return rol;
    }

    public boolean isAdmin() {
        return "Administrador".equals(rol);
    }
}
