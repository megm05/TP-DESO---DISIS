package com.grupo12.gestores;

import com.grupo12.dominio.BloquePregunta;
import com.grupo12.dominio.Candidato;
import com.grupo12.dominio.Competencia;
import com.grupo12.dominio.Cuestionario;
import com.grupo12.dominio.Detalle_opcion;
import com.grupo12.dominio.Estado;
import com.grupo12.dominio.Factor;
import com.grupo12.dominio.Parametro;
import com.grupo12.dominio.Pregunta;
import com.grupo12.dominio.Respuesta;
import com.grupo12.dto.OpcionBloqueDTO;
import com.grupo12.dto.PreguntaBloqueDTO;
import com.grupo12.persistencia.CuestionarioDAO;
import com.grupo12.persistencia.FabricaDAO;
import com.grupo12.persistencia.ParametroDAO;
import com.grupo12.persistencia.PreguntaDAO;
import com.grupo12.persistencia.RespuestaDAO;
import com.grupo12.persistencia.Transaccion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Controlador del CU026 - Completar Cuestionario. */
public class Gestor_de_cuestionario {

    private static final int PREGUNTAS_POR_BLOQUE_POR_DEFECTO = 4;
    private static final int MAX_ACCESOS_POR_DEFECTO = 5;
    private static final double TIEMPO_MAX_ACTIVO_POR_DEFECTO = 360;

    private final CuestionarioDAO cuestionarioDAO;
    private final RespuestaDAO respuestaDAO;
    private final PreguntaDAO preguntaDAO;
    private final ParametroDAO parametroDAO;

    public Gestor_de_cuestionario() {
        this(FabricaDAO.getCuestionarioDAO(), FabricaDAO.getRespuestaDAO(), FabricaDAO.getPreguntaDAO(),
                FabricaDAO.getParametroDAO());
    }

    public Gestor_de_cuestionario(CuestionarioDAO cuestionarioDAO, RespuestaDAO respuestaDAO,
            PreguntaDAO preguntaDAO, ParametroDAO parametroDAO) {
        this.cuestionarioDAO = cuestionarioDAO;
        this.respuestaDAO = respuestaDAO;
        this.preguntaDAO = preguntaDAO;
        this.parametroDAO = parametroDAO;
    }

    // ------------------------------------------------------------------
    //  Ingreso al cuestionario (precondiciones y flujos 1.A, 1.B y 1.C)
    // ------------------------------------------------------------------

    /** Cuestionario en estado activo o en proceso del candidato; null si no tiene ninguno. */
    public Cuestionario obtener_cuestionario_activo(Candidato candidato) {
        return cuestionarioDAO.obtener_activo_por_candidato(candidato);
    }

    /**
     * Controla los vencimientos y la cantidad de accesos cada vez que el candidato
     * ingresa a completar el cuestionario, y cuenta el nuevo acceso.
     *
     * @return el aviso a mostrar si el cuestionario fue cerrado (INCOMPLETO o SIN_CONTESTAR);
     *         null si el candidato puede continuar
     */
    public String registrar_ingreso(Cuestionario cuestionario) {
        return Transaccion.ejecutar(() -> {
            String aviso = null;
            if (cuestionario.vencio_plazo_completado()) {
                // flujo 1.A
                cuestionario.marcar_incompleto();
                aviso = "Se superó el tiempo máximo para completar el cuestionario.";
            } else if (cuestionario.vencio_plazo_activacion(
                    decimal(Parametro.TIEMPO_MAX_ACTIVO, TIEMPO_MAX_ACTIVO_POR_DEFECTO))) {
                // flujo 1.B
                cuestionario.marcar_sin_contestar();
                aviso = "Se superó el tiempo máximo permitido para iniciar el cuestionario.";
            } else {
                int max_accesos = (int) decimal(Parametro.MAX_ACCESOS, MAX_ACCESOS_POR_DEFECTO);
                if (cuestionario.supero_max_accesos(max_accesos)) {
                    // precondición: cantidad máxima de accesos permitidos
                    cuestionario.marcar_incompleto();
                    aviso = "Se superó la cantidad máxima de accesos permitidos (" + max_accesos + ").";
                } else {
                    cuestionario.registrar_ingreso();
                }
            }
            cuestionarioDAO.actualizar(cuestionario);
            return aviso;
        });
    }

    /** Instrucciones del cuestionario, tomadas del repositorio de configuraciones del sistema. */
    public String obtener_instrucciones() {
        Parametro parametro = parametroDAO.obtener_por_clave(Parametro.INSTRUCCIONES);
        return parametro == null ? "" : parametro.getValor();
    }

    // ------------------------------------------------------------------
    //  Inicio (paso 3)
    // ------------------------------------------------------------------

    /**
     * Paso 3: recupera las competencias a evaluar y sus factores, asocia al
     * cuestionario las preguntas elegidas al azar, lo guarda para que pueda
     * presentarse de forma idéntica si el candidato vuelve a ingresar, registra el
     * día y hora de comienzo y lo pasa a "En Proceso".
     */
    public void iniciar_cuestionario(Cuestionario cuestionario) {
        if (cuestionario.getEstado_cuestionario() != Estado.ACTIVO) {
            return;
        }
        Transaccion.realizar(() -> {
            List<Factor> modelo = new ArrayList<>();
            for (Competencia competencia : cuestionario.getEvaluacion().getPuesto().obtener_competencias()) {
                for (Factor factor : competencia.obtener_factores()) {
                    if (factor.preguntas_suficientes()) {
                        modelo.add(factor);
                    }
                }
            }
            List<Pregunta> preguntas = seleccionar_preguntas(modelo);
            if (preguntas.isEmpty()) {
                throw new ValidacionException("El cuestionario no puede iniciarse porque no hay preguntas"
                        + " disponibles para las competencias a evaluar. Comuníquese con la consultora.");
            }
            int cant_por_bloque = (int) decimal(Parametro.PREGUNTAS_POR_BLOQUE, PREGUNTAS_POR_BLOQUE_POR_DEFECTO);
            cuestionario.armar_cuestionario(preguntas, Math.max(1, cant_por_bloque));
            cuestionario.marcar_en_proceso();
            cuestionarioDAO.actualizar(cuestionario);
        });
    }

    /**
     * Toma aleatoriamente 2 preguntas por cada factor a evaluar (CU026 observación 1).
     * Los factores que no disponen de al menos 2 preguntas vigentes no se evalúan.
     */
    public List<Pregunta> seleccionar_preguntas(List<Factor> modelo) {
        List<Pregunta> seleccionadas = new ArrayList<>();
        for (Factor factor : modelo) {
            List<Pregunta> vigentes = preguntaDAO.obtener_activas_por_factor(factor);
            if (vigentes.size() >= Factor.PREGUNTAS_POR_FACTOR) {
                Collections.shuffle(vigentes);
                seleccionadas.addAll(vigentes.subList(0, Factor.PREGUNTAS_POR_FACTOR));
            }
        }
        return seleccionadas;
    }

    // ------------------------------------------------------------------
    //  Wizard de bloques (pasos 4 a 8)
    // ------------------------------------------------------------------

    /**
     * Preguntas del bloque que aún no ha sido respondido, tal como se presentan al
     * candidato (sin las ponderaciones). Lista vacía si no quedan bloques.
     */
    public List<PreguntaBloqueDTO> obtener_bloque_actual(Cuestionario cuestionario) {
        List<PreguntaBloqueDTO> preguntas = new ArrayList<>();
        BloquePregunta bloque = cuestionario.obtener_bloque_no_respondido();
        if (bloque == null) {
            return preguntas;
        }
        for (Respuesta respuesta : bloque.obtener_respuestas()) {
            List<OpcionBloqueDTO> opciones = new ArrayList<>();
            for (Detalle_opcion detalle : respuesta.getPregunta().getOpcion_respuesta().obtener_detalles()) {
                opciones.add(new OpcionBloqueDTO(detalle.getId_detalle(), detalle.getDescripcion()));
            }
            Integer elegido = respuesta.getDetalle_elegido() == null
                    ? null : respuesta.getDetalle_elegido().getId_detalle();
            preguntas.add(new PreguntaBloqueDTO(respuesta.getId_respuesta(), respuesta.getOrden(),
                    respuesta.getPregunta().getPregunta(), opciones, elegido));
        }
        return preguntas;
    }

    /**
     * Pasos 5 a 7: guarda las respuestas del bloque actual. Si el candidato
     * respondió todas las preguntas, el bloque queda cerrado.
     *
     * @param respuestas opción elegida (id_detalle) por cada respuesta (id_respuesta)
     * @return flujo 6.A: las preguntas del bloque que no han sido respondidas (lista vacía
     *         si el bloque quedó completo)
     */
    public List<Pregunta> registrar_respuestas_bloque(Cuestionario cuestionario, Map<Integer, Integer> respuestas) {
        return Transaccion.ejecutar(() -> {
            BloquePregunta bloque = cuestionario.obtener_bloque_no_respondido();
            if (bloque == null || cuestionario.getEstado_cuestionario() != Estado.EN_PROCESO) {
                throw new ValidacionException("El cuestionario no tiene bloques pendientes de respuesta.");
            }
            List<Pregunta> sinResponder = new ArrayList<>();
            for (Respuesta respuesta : bloque.obtener_respuestas()) {
                Integer id_detalle = respuestas.get(respuesta.getId_respuesta());
                if (id_detalle != null) {
                    Detalle_opcion elegido = new Detalle_opcion();
                    elegido.setId_detalle(id_detalle);
                    try {
                        respuesta.registrar(elegido);
                    } catch (IllegalArgumentException e) {
                        throw new ValidacionException("La opción elegida no corresponde a la pregunta "
                                + respuesta.getOrden() + ".");
                    }
                    respuestaDAO.actualizar(respuesta);
                }
                if (respuesta.getDetalle_elegido() == null) {
                    sinResponder.add(respuesta.getPregunta());
                }
            }
            if (bloque.esta_completo()) {
                bloque.completar();
                cuestionarioDAO.actualizar(cuestionario);
            }
            return sinResponder;
        });
    }

    /**
     * Paso 8: calcula el puntaje de los factores y competencias, guarda los
     * resultados y pasa el cuestionario a "Completo".
     */
    public void finalizar(Cuestionario cuestionario) {
        if (cuestionario.getEstado_cuestionario() != Estado.EN_PROCESO
                || cuestionario.obtener_bloque_no_respondido() != null) {
            throw new ValidacionException("El cuestionario todavía tiene bloques sin responder.");
        }
        Transaccion.realizar(() -> {
            cuestionario.calcular_puntaje();
            cuestionario.marcar_completo();
            cuestionarioDAO.actualizar(cuestionario);
        });
    }

    private double decimal(String clave, double porDefecto) {
        Parametro parametro = parametroDAO.obtener_por_clave(clave);
        if (parametro == null) {
            return porDefecto;
        }
        try {
            return Double.parseDouble(parametro.getValor().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }
}
