package com.grupo24.dominio;

import java.time.LocalDate;

/** Candidato a un puesto. */
public class Candidato extends Usuario {

    private int id_candidato;
    private int num_candidato;
    private String nombre;
    private String apellido;
    private Tipo_doc tipo_doc;
    private String nro_doc;
    private String email;
    private char genero;
    private LocalDate fecha_nac;
    private String escolaridad;

    public int getId_candidato() {
        return id_candidato;
    }

    public void setId_candidato(int id_candidato) {
        this.id_candidato = id_candidato;
    }

    public int getNum_candidato() {
        return num_candidato;
    }

    public void setNum_candidato(int num_candidato) {
        this.num_candidato = num_candidato;
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

    public Tipo_doc getTipo_doc() {
        return tipo_doc;
    }

    public void setTipo_doc(Tipo_doc tipo_doc) {
        this.tipo_doc = tipo_doc;
    }

    public String getNro_doc() {
        return nro_doc;
    }

    public void setNro_doc(String nro_doc) {
        this.nro_doc = nro_doc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public char getGenero() {
        return genero;
    }

    public void setGenero(char genero) {
        this.genero = genero;
    }

    public LocalDate getFecha_nac() {
        return fecha_nac;
    }

    public void setFecha_nac(LocalDate fecha_nac) {
        this.fecha_nac = fecha_nac;
    }

    public String getEscolaridad() {
        return escolaridad;
    }

    public void setEscolaridad(String escolaridad) {
        this.escolaridad = escolaridad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Candidato)) {
            return false;
        }
        return num_candidato == ((Candidato) o).num_candidato;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(num_candidato);
    }
}
