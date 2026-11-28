package com.grupo24.gestores;

import com.grupo24.dominio.Competencia;
import com.grupo24.dominio.Consultor;
import com.grupo24.dominio.Detalle_opcion;
import com.grupo24.dominio.Factor;
import com.grupo24.dominio.Opcion_respuesta;
import com.grupo24.dominio.Ponderacion_opcion;
import com.grupo24.dominio.Pregunta;
import com.grupo24.dominio.Registro_pregunta;
import com.grupo24.dto.DatosPreguntaDTO;
import com.grupo24.dto.PreguntaBusquedaDTO;
import com.grupo24.persistencia.FabricaDAO;
import com.grupo24.persistencia.FactorDAO;
import com.grupo24.persistencia.OpcionRespuestaDAO;
import com.grupo24.persistencia.PreguntaDAO;
import com.grupo24.persistencia.RegistroAuditoriaDAO;
import com.grupo24.persistencia.Transaccion;
import com.grupo24.util.Texto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Controlador de la gestión de preguntas: CU020 (Gestionar Preguntas), CU021 (Dar de alta Pregunta), CU022
 * (Modificar Pregunta) y CU023 (Dar de baja Pregunta).
 */
public class Gestor_de_pregunta implements Gestor<DatosPreguntaDTO> {

    public static final int LONGITUD_MAX_NOMBRE = 100;
    public static final int LONGITUD_MAX_PREGUNTA = 500;
    public static final int LONGITUD_MAX_DESCRIPCION = 500;
    /** Las ponderaciones de las opciones de respuesta de una pregunta deben sumar 10 puntos. */
    public static final double SUMA_PONDERACIONES = 10.0;

    private final PreguntaDAO preguntaDAO;
    private final FactorDAO factorDAO;
    private final OpcionRespuestaDAO opcionRespuestaDAO;
    private final RegistroAuditoriaDAO registroAuditoriaDAO;

    public Gestor_de_pregunta() {
        this(FabricaDAO.getPreguntaDAO(), FabricaDAO.getFactorDAO(), FabricaDAO.getOpcionRespuestaDAO(),
                FabricaDAO.getRegistroAuditoriaDAO());
    }

    public Gestor_de_pregunta(PreguntaDAO preguntaDAO, FactorDAO factorDAO,
            OpcionRespuestaDAO opcionRespuestaDAO, RegistroAuditoriaDAO registroAuditoriaDAO) {
        this.preguntaDAO = preguntaDAO;
        this.factorDAO = factorDAO;
        this.opcionRespuestaDAO = opcionRespuestaDAO;
        this.registroAuditoriaDAO = registroAuditoriaDAO;
    }

    public List<PreguntaBusquedaDTO> buscar(String nombre_competencia, String nombre_factor,
            String nombre_pregunta) {
        if (Texto.estaVacio(nombre_competencia) && Texto.estaVacio(nombre_factor)
                && Texto.estaVacio(nombre_pregunta)) {
            throw new ValidacionException("Debe ingresar datos en al menos uno de los criterios de búsqueda.");
        }
        List<PreguntaBusquedaDTO> resultado = new ArrayList<>();
        for (Pregunta pregunta : preguntaDAO.buscar_por_filtros(nombre_competencia, nombre_factor,
                nombre_pregunta)) {
            resultado.add(new PreguntaBusquedaDTO(pregunta.getId_pregunta(),
                    pregunta.getFactor().getCompetencia().getNombre(), pregunta.getFactor().getNombre(),
                    pregunta.getNombre(), pregunta.getPregunta()));
        }
        return resultado;
    }

    @Override
    public void dar_de_alta(DatosPreguntaDTO datos) {
        normalizar(datos);
        List<String> errores = new ArrayList<>();
        errores.addAll(validar_campos_obligatorios(datos));
        errores.addAll(validar_longitudes(datos));
        if (datos.getId_opcion() > 0) {
            errores.addAll(validar_ponderaciones(datos));
        }
        if (!errores.isEmpty()) {
            throw new ValidacionException(errores);
        }
        Transaccion.realizar(() -> {
            Factor factor = factorDAO.obtener_por_id(datos.getId_factor());
            if (factor == null || factor.getCompetencia().getId_competencia() != datos.getId_competencia()) {
                throw new ValidacionException("El factor seleccionado no corresponde a la competencia.");
            }
            Opcion_respuesta opcion = obtenerOpcion(datos);
            validarExistencia(datos, factor, 0);
            Pregunta pregunta = new Pregunta();
            pregunta.setFactor(factor);
            completar(pregunta, datos, opcion);
            preguntaDAO.guardar(pregunta);
            datos.setId_pregunta(pregunta.getId_pregunta());
        });
    }

    public DatosPreguntaDTO obtener_datos(int id_pregunta) {
        Pregunta pregunta = obtenerVigente(id_pregunta);
        DatosPreguntaDTO datos = new DatosPreguntaDTO();
        datos.setId_pregunta(pregunta.getId_pregunta());
        datos.setId_competencia(pregunta.getFactor().getCompetencia().getId_competencia());
        datos.setNombre_competencia(pregunta.getFactor().getCompetencia().getNombre());
        datos.setId_factor(pregunta.getFactor().getId_factor());
        datos.setNombre_factor(pregunta.getFactor().getNombre());
        datos.setNombre(pregunta.getNombre());
        datos.setPregunta(pregunta.getPregunta());
        datos.setDescripcion(pregunta.getDescripcion());
        datos.setId_opcion(pregunta.getOpcion_respuesta().getId_opcion());
        for (Ponderacion_opcion ponderacion : pregunta.getPonderaciones()) {
            datos.getPonderaciones().put(ponderacion.getDetalle().getId_detalle(), ponderacion.getPonderacion());
        }
        return datos;
    }

    @Override
    public void modificar(DatosPreguntaDTO datos) {
        normalizar(datos);
        Transaccion.realizar(() -> {
            Pregunta pregunta = obtenerVigente(datos.getId_pregunta());
            Factor factor = pregunta.getFactor();
            datos.setId_factor(factor.getId_factor());
            datos.setId_competencia(factor.getCompetencia().getId_competencia());
            List<String> errores = new ArrayList<>();
            errores.addAll(validar_campos_obligatorios(datos));
            errores.addAll(validar_longitudes(datos));
            if (datos.getId_opcion() > 0) {
                errores.addAll(validar_ponderaciones(datos));
            }
            if (!errores.isEmpty()) {
                throw new ValidacionException(errores);
            }
            Opcion_respuesta opcion = obtenerOpcion(datos);
            validarExistencia(datos, factor, pregunta.getId_pregunta());
            completar(pregunta, datos, opcion);
            preguntaDAO.actualizar(pregunta);
        });
    }

    @Override
    public void dar_baja(int id_pregunta, Consultor consultor) {
        Transaccion.realizar(() -> {
            Pregunta pregunta = obtenerVigente(id_pregunta);
            Registro_pregunta registro = new Registro_pregunta();
            registro.Borrar_Con_Registro(pregunta, LocalDateTime.now(), consultor);
            preguntaDAO.actualizar(pregunta);
            registroAuditoriaDAO.guardar(registro);
        });
    }

    /** Flujo 2.A: detalla TODOS los datos obligatorios que no fueron ingresados. */
    public List<String> validar_campos_obligatorios(DatosPreguntaDTO datos) {
        List<String> errores = new ArrayList<>();
        if (datos.getId_competencia() <= 0) {
            errores.add("Debe seleccionar la Competencia.");
        }
        if (datos.getId_factor() <= 0) {
            errores.add("Debe seleccionar el Factor.");
        }
        if (Texto.estaVacio(datos.getNombre())) {
            errores.add("Debe ingresar el Nombre de Pregunta.");
        }
        if (Texto.estaVacio(datos.getPregunta())) {
            errores.add("Debe ingresar la Pregunta.");
        }
        if (datos.getId_opcion() <= 0) {
            errores.add("Debe seleccionar la Opción de Respuesta.");
        }
        return errores;
    }

    /** Flujo 2.B: detalla TODOS los campos que exceden su longitud máxima. */
    public List<String> validar_longitudes(DatosPreguntaDTO datos) {
        List<String> errores = new ArrayList<>();
        validarLongitud(errores, "Nombre de Pregunta", datos.getNombre(), LONGITUD_MAX_NOMBRE);
        validarLongitud(errores, "Pregunta", datos.getPregunta(), LONGITUD_MAX_PREGUNTA);
        validarLongitud(errores, "Descripción", datos.getDescripcion(), LONGITUD_MAX_DESCRIPCION);
        return errores;
    }

    private static void validarLongitud(List<String> errores, String campo, String valor, int maximo) {
        if (valor != null && valor.length() > maximo) {
            errores.add("El campo " + campo + " excede su longitud máxima de " + maximo
                    + " caracteres (tiene " + valor.length() + ").");
        }
    }

    /**
     * Las ponderaciones de las opciones de respuesta deben sumar 10 puntos y ser siempre 0 o positivas
     * (CU021, observaciones).
     */
    public List<String> validar_ponderaciones(DatosPreguntaDTO datos) {
        List<String> errores = new ArrayList<>();
        Map<Integer, Double> ponderaciones = datos.getPonderaciones();
        if (ponderaciones.isEmpty() || ponderaciones.containsValue(null)) {
            errores.add("Debe ingresar una ponderación numérica para cada una de las opciones de respuesta.");
            return errores;
        }
        double suma = 0;
        boolean negativas = false;
        for (Double ponderacion : ponderaciones.values()) {
            if (ponderacion < 0) {
                negativas = true;
            }
            suma += ponderacion;
        }
        if (negativas) {
            errores.add("Las ponderaciones de las opciones de respuesta deben ser siempre 0 o positivas.");
        }
        if (Math.abs(suma - SUMA_PONDERACIONES) > 0.001) {
            errores.add("Las ponderaciones de las opciones de respuesta deben sumar 10 puntos (suman "
                    + String.format(Locale.ROOT, "%.2f", suma).replace('.', ',') + ").");
        }
        return errores;
    }

    /**
     * Flujo 2.C: la validación de existencia se realiza tanto sobre el nombre de la pregunta como sobre la
     * pregunta en cuestión, aclarando cuál no fue superada.
     */
    private void validarExistencia(DatosPreguntaDTO datos, Factor factor, int id_excluida) {
        Competencia competencia = factor.getCompetencia();
        String ubicacion = " definida para la competencia " + competencia.getNombre() + " y el factor "
                + factor.getNombre() + ".";
        List<String> errores = new ArrayList<>();
        if (preguntaDAO.existe_pregunta(datos.getNombre(), null, competencia, factor, id_excluida)) {
            errores.add("Ya existe una pregunta con el nombre \"" + datos.getNombre() + "\"" + ubicacion);
        }
        if (preguntaDAO.existe_pregunta(null, datos.getPregunta(), competencia, factor, id_excluida)) {
            errores.add("Ya existe una pregunta con el mismo texto de pregunta" + ubicacion);
        }
        if (!errores.isEmpty()) {
            throw new ValidacionException(errores, "nombre");
        }
    }

    private Pregunta obtenerVigente(int id_pregunta) {
        Pregunta pregunta = preguntaDAO.obtener_por_id(id_pregunta);
        if (pregunta == null || !pregunta.disponibilidad()) {
            throw new ValidacionException("La pregunta seleccionada no existe o ya fue eliminada.");
        }
        return pregunta;
    }

    private Opcion_respuesta obtenerOpcion(DatosPreguntaDTO datos) {
        Opcion_respuesta opcion = opcionRespuestaDAO.obtener_por_id(datos.getId_opcion());
        if (opcion == null) {
            throw new ValidacionException("La opción de respuesta seleccionada no existe.");
        }
        List<Detalle_opcion> detalles = opcion.obtener_detalles();
        if (detalles.size() != datos.getPonderaciones().size()) {
            throw new ValidacionException(
                    "Debe ingresar una ponderación numérica para cada una de las opciones de respuesta.");
        }
        for (Detalle_opcion detalle : detalles) {
            if (!datos.getPonderaciones().containsKey(detalle.getId_detalle())) {
                throw new ValidacionException(
                        "Las ponderaciones ingresadas no corresponden a la opción de respuesta seleccionada.");
            }
        }
        return opcion;
    }

    private static void completar(Pregunta pregunta, DatosPreguntaDTO datos, Opcion_respuesta opcion) {
        pregunta.setNombre(datos.getNombre());
        pregunta.setPregunta(datos.getPregunta());
        pregunta.setDescripcion(Texto.estaVacio(datos.getDescripcion()) ? null : datos.getDescripcion());
        pregunta.setOpcion_respuesta(opcion);
        List<Ponderacion_opcion> ponderaciones = new ArrayList<>();
        for (Detalle_opcion detalle : opcion.obtener_detalles()) {
            ponderaciones.add(new Ponderacion_opcion(detalle, datos.getPonderaciones().get(detalle.getId_detalle())));
        }
        pregunta.setPonderaciones(ponderaciones);
    }

    /** El ingreso de datos literales es SIEMPRE en mayúsculas. */
    private static void normalizar(DatosPreguntaDTO datos) {
        datos.setNombre(Texto.mayusculas(datos.getNombre()));
        datos.setPregunta(Texto.mayusculas(datos.getPregunta()));
        datos.setDescripcion(Texto.mayusculas(datos.getDescripcion()));
    }
}
