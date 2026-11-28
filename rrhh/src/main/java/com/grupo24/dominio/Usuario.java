package com.grupo24.dominio;

import com.grupo24.util.Seguridad;

/** Usuario del sistema. */
public abstract class Usuario {

    private String nombre_usuario;
    private String contrasenia;

    public boolean autenticar(String contrasenia_ingresada) {
        if (contrasenia == null || contrasenia_ingresada == null) {
            return false;
        }
        return contrasenia.equals(Seguridad.hash(contrasenia_ingresada));
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }
}
