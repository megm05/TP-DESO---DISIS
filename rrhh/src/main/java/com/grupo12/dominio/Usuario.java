package com.grupo12.dominio;

import com.grupo12.util.Seguridad;

/**
 * Usuario del sistema. Clase base de {@link Consultor} y {@link Candidato}.
 */
public abstract class Usuario {

    private String nombre_usuario;
    /** Hash SHA-256 de la contraseña. */
    private String contrasenia;

    /**
     * Verifica que la contraseña ingresada corresponda a la del usuario.
     *
     * @param contrasenia_ingresada contraseña en texto plano tal como la tipeó el actor
     * @return true si la autenticación es exitosa
     */
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
