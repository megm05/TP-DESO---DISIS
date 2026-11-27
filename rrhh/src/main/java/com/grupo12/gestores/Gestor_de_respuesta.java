package com.grupo12.gestores;

import com.grupo12.dominio.Detalle_opcion;
import com.grupo12.dominio.Opcion_respuesta;
import com.grupo12.persistencia.FabricaDAO;
import com.grupo12.persistencia.OpcionRespuestaDAO;
import com.grupo12.util.Texto;
import java.util.ArrayList;
import java.util.List;

/** Gestor de opciones de respuesta. */
public class Gestor_de_respuesta {

    private final OpcionRespuestaDAO opcionRespuestaDAO;

    public Gestor_de_respuesta() {
        this(FabricaDAO.getOpcionRespuestaDAO());
    }

    public Gestor_de_respuesta(OpcionRespuestaDAO opcionRespuestaDAO) {
        this.opcionRespuestaDAO = opcionRespuestaDAO;
    }

    public List<Opcion_respuesta> buscar(String nombre) {
        List<Opcion_respuesta> todas = opcionRespuestaDAO.obtener_todos();
        if (Texto.estaVacio(nombre)) {
            return todas;
        }
        String buscado = Texto.mayusculas(nombre);
        List<Opcion_respuesta> coincidentes = new ArrayList<>();
        for (Opcion_respuesta opcion : todas) {
            if (Texto.mayusculas(opcion.getNombre()).contains(buscado)) {
                coincidentes.add(opcion);
            }
        }
        return coincidentes;
    }

    /**
     * Lista de opciones (orden y descripción) de una opción de respuesta, ordenada por orden, para la grilla
     * donde se ingresan las ponderaciones (CU021).
     */
    public List<Detalle_opcion> obtener_detalles(int id_opcion) {
        Opcion_respuesta opcion = opcionRespuestaDAO.obtener_por_id(id_opcion);
        return opcion == null ? new ArrayList<>() : opcion.obtener_detalles();
    }
}
