package com.grupo24.presentacion;

import com.grupo24.dto.DatosPreguntaDTO;
import com.grupo24.gestores.Gestor_de_pregunta;
import com.grupo24.gestores.ValidacionException;
import java.util.Collections;

/** Pantalla de modificación de preguntas (CU022). */
public class PantallaModificarPregunta extends PantallaAltaPregunta {

    @Override
    public RespuestaHttp atender(Peticion peticion) {
        if (!esConsultor(peticion)) {
            return RespuestaHttp.redirigir("/login");
        }
        Integer id = peticion.paramEntero("id");
        if (id == null) {
            return RespuestaHttp.redirigir("/preguntas?refrescar=1&error=seleccion");
        }
        Gestor_de_pregunta gestor = new Gestor_de_pregunta();
        DatosPreguntaDTO actuales;
        try {
            actuales = gestor.obtener_datos(id);
        } catch (ValidacionException e) {
            peticion.getSesion().agregarMensaje(e.getMessage());
            return RespuestaHttp.redirigir("/preguntas?refrescar=1");
        }
        if (!peticion.esPost()) {
            // paso 1: datos de la pregunta a modificar
            return formulario(peticion, actuales, true, Collections.emptyList(), null);
        }
        DatosPreguntaDTO datos = leerDatos(peticion);
        datos.setNombre_competencia(actuales.getNombre_competencia());
        datos.setNombre_factor(actuales.getNombre_factor());
        datos.setId_competencia(actuales.getId_competencia());
        datos.setId_factor(actuales.getId_factor());
        if ("opcion".equals(peticion.param("accion"))) {
            datos.getPonderaciones().clear();
            return formulario(peticion, datos, true, Collections.emptyList(), null);
        }
        try {
            gestor.modificar(datos);
        } catch (ValidacionException e) {
            // flujos 2.A, 2.B y 2.C (en 2.C con foco en el campo Nombre)
            return formulario(peticion, datos, true, e.getErrores(), e.getCampo());
        }
        // paso 3
        peticion.getSesion().agregarMensaje("La operación ha culminado con éxito.");
        return RespuestaHttp.redirigir("/preguntas?refrescar=1");
    }
}
