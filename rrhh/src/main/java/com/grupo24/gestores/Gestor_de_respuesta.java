package com.grupo24.gestores;

import com.grupo24.dominio.Detalle_opcion;
import com.grupo24.dominio.Opcion_respuesta;
import com.grupo24.dto.DetalleOpcionDTO;
import com.grupo24.dto.OpcionRespuestaDTO;
import com.grupo24.persistencia.FabricaDAO;
import com.grupo24.persistencia.OpcionRespuestaDAO;
import com.grupo24.util.Texto;
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

    /** Opciones de respuesta para la lista de selección del alta de preguntas (CU021 paso 1). */
    public List<OpcionRespuestaDTO> buscar(String nombre) {
        String buscado = Texto.estaVacio(nombre) ? null : Texto.mayusculas(nombre);
        List<OpcionRespuestaDTO> resultado = new ArrayList<>();
        for (Opcion_respuesta opcion : opcionRespuestaDAO.obtener_todos()) {
            if (buscado == null || Texto.mayusculas(opcion.getNombre()).contains(buscado)) {
                resultado.add(new OpcionRespuestaDTO(opcion.getId_opcion(), opcion.getNombre()));
            }
        }
        return resultado;
    }

    /** Filas de la grilla Orden / Descripción / Ponderación, ordenadas por orden (CU021). */
    public List<DetalleOpcionDTO> obtener_detalles(int id_opcion) {
        List<DetalleOpcionDTO> resultado = new ArrayList<>();
        Opcion_respuesta opcion = opcionRespuestaDAO.obtener_por_id(id_opcion);
        if (opcion != null) {
            for (Detalle_opcion detalle : opcion.obtener_detalles()) {
                resultado.add(new DetalleOpcionDTO(detalle.getId_detalle(), detalle.getOrden(),
                        detalle.getDescripcion()));
            }
        }
        return resultado;
    }
}
