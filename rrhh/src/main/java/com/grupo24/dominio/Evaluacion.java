package com.grupo24.dominio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Evaluación: convocatoria de un grupo de candidatos para un puesto (CU025). */
public class Evaluacion {

    public static final String ESTADO_GENERADA = "GENERADA";

    private int id_evaluacion;
    private LocalDate fecha_generacion;
    private String estado;
    private Puesto puesto;
    private Consultor consultor;
    /** Grilla "Candidatos a evaluar" del CU025. */
    private List<Candidato> candidatos = new ArrayList<>();
    private List<Cuestionario> cuestionarios = new ArrayList<>();

    /** Agrega un candidato a la grilla "Candidatos a evaluar" (CU025 paso 5). */
    public void agregar_candidato(Candidato candidato) {
        if (!candidatos.contains(candidato)) {
            candidatos.add(candidato);
        }
    }

    /** Quita un candidato de la grilla "Candidatos a evaluar" (CU025 flujo 5.A). */
    public void quitar_candidato(Candidato candidato) {
        candidatos.remove(candidato);
    }

    /** Función o puesto en el cual se evalúa a los candidatos (CU025 paso 8). */
    public void seleccionar_puesto(Puesto puesto) {
        this.puesto = puesto;
    }

    /**
     * Crea un cuestionario en estado ACTIVO por cada candidato a evaluar, cada uno con su clave de ingreso
     * aleatoria (CU025, requerimientos especiales).
     */
    public List<Cuestionario> generar_cuestionarios(float tiempo_max) {
        cuestionarios = new ArrayList<>();
        fecha_generacion = LocalDate.now();
        estado = ESTADO_GENERADA;
        for (Candidato candidato : candidatos) {
            Cuestionario cuestionario = new Cuestionario();
            cuestionario.setCandidato(candidato);
            cuestionario.setEvaluacion(this);
            cuestionario.setTiempoMax(tiempo_max);
            cuestionario.generar_clave();
            cuestionario.activar();
            cuestionarios.add(cuestionario);
        }
        return cuestionarios;
    }

    public int getId_evaluacion() {
        return id_evaluacion;
    }

    public void setId_evaluacion(int id_evaluacion) {
        this.id_evaluacion = id_evaluacion;
    }

    public LocalDate getFecha_generacion() {
        return fecha_generacion;
    }

    public void setFecha_generacion(LocalDate fecha_generacion) {
        this.fecha_generacion = fecha_generacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Puesto getPuesto() {
        return puesto;
    }

    public Consultor getConsultor() {
        return consultor;
    }

    public void setConsultor(Consultor consultor) {
        this.consultor = consultor;
    }

    public List<Candidato> getCandidatos() {
        return candidatos;
    }

    public List<Cuestionario> getCuestionarios() {
        return cuestionarios;
    }

    public void setCuestionarios(List<Cuestionario> cuestionarios) {
        this.cuestionarios = cuestionarios;
    }
}
