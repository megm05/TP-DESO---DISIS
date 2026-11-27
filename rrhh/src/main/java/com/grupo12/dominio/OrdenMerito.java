package com.grupo12.dominio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Orden de mérito emitido para un puesto sobre una o más evaluaciones (CU028). */
public class OrdenMerito {

    private int id_orden_merito;
    private LocalDate fecha_generacion;
    private Puesto puesto;
    private Consultor consultor;
    private List<Evaluacion> evaluaciones = new ArrayList<>();
    private List<Renglon_Orden_Merito> renglones = new ArrayList<>();

    public void agregar_renglon(Candidato candidato, Cuestionario cuestionario, Double puntaje) {
        Renglon_Orden_Merito renglon = new Renglon_Orden_Merito();
        renglon.setCandidato(candidato);
        renglon.setCuestionario(cuestionario);
        renglon.setPuntaje_obtenido(puntaje);
        renglones.add(renglon);
    }

    /**
     * Conforma el orden de mérito (CU028 paso 7): lo integran los candidatos con cuestionario completo que
     * alcanzaron las ponderaciones mínimas de cada competencia del puesto, ordenados por puntaje obtenido.
     */
    public void generar() {
        List<Renglon_Orden_Merito> integran = new ArrayList<>();
        List<Renglon_Orden_Merito> resto = new ArrayList<>();
        for (Renglon_Orden_Merito renglon : renglones) {
            Cuestionario cuestionario = renglon.getCuestionario();
            if (cuestionario.getEstado_cuestionario() == Estado.COMPLETO
                    && cuestionario.alcanza_ponderaciones_minimas(puesto)) {
                integran.add(renglon);
            } else {
                resto.add(renglon);
            }
        }

        Comparator<Renglon_Orden_Merito> porNombre = Comparator
                .comparing((Renglon_Orden_Merito r) -> r.getCandidato().getApellido())
                .thenComparing(r -> r.getCandidato().getNombre());
        Comparator<Renglon_Orden_Merito> porPuntaje = Comparator.comparing(
                Renglon_Orden_Merito::getPuntaje_obtenido, Comparator.nullsLast(Comparator.reverseOrder()));
        integran.sort(porPuntaje.thenComparing(porNombre));
        // el resto se agrupa por estado: primero los completos que no alcanzaron las
        // ponderaciones mínimas, luego los que siguen en curso y por último los cerrados
        resto.sort(Comparator
                .comparing((Renglon_Orden_Merito r) -> ordenDeGrupo(r.getCuestionario().getEstado_cuestionario()))
                .thenComparing(porPuntaje)
                .thenComparing(porNombre));

        renglones = new ArrayList<>();
        int posicion = 0;
        for (Renglon_Orden_Merito renglon : integran) {
            renglon.setPosicion(++posicion);
            renglones.add(renglon);
        }
        for (Renglon_Orden_Merito renglon : resto) {
            renglon.setPosicion(null);
            renglones.add(renglon);
        }
        int numero = 0;
        for (Renglon_Orden_Merito renglon : renglones) {
            renglon.setNro_renglon(++numero);
        }
        if (fecha_generacion == null) {
            fecha_generacion = LocalDate.now();
        }
    }

    private static int ordenDeGrupo(Estado estado) {
        switch (estado) {
            case COMPLETO:
                return 0;
            case EN_PROCESO:
                return 1;
            case ACTIVO:
                return 2;
            case INCOMPLETO:
                return 3;
            default:
                return 4;
        }
    }

    public int getId_orden_merito() {
        return id_orden_merito;
    }

    public void setId_orden_merito(int id_orden_merito) {
        this.id_orden_merito = id_orden_merito;
    }

    public LocalDate getFecha_generacion() {
        return fecha_generacion;
    }

    public void setFecha_generacion(LocalDate fecha_generacion) {
        this.fecha_generacion = fecha_generacion;
    }

    public Puesto getPuesto() {
        return puesto;
    }

    public void setPuesto(Puesto puesto) {
        this.puesto = puesto;
    }

    public Consultor getConsultor() {
        return consultor;
    }

    public void setConsultor(Consultor consultor) {
        this.consultor = consultor;
    }

    public List<Evaluacion> getEvaluaciones() {
        return evaluaciones;
    }

    public void setEvaluaciones(List<Evaluacion> evaluaciones) {
        this.evaluaciones = evaluaciones;
    }

    public List<Renglon_Orden_Merito> getRenglones() {
        return renglones;
    }
}
