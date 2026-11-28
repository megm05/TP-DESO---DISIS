package com.grupo24.dominio;

/** Consultor de Capit@l Humano: usuario interno que opera el sistema. */
public class Consultor extends Usuario {

    private int id_consultor;
    private String nombre;
    private String apellido;

    public int getId_consultor() {
        return id_consultor;
    }

    public void setId_consultor(int id_consultor) {
        this.id_consultor = id_consultor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
}
