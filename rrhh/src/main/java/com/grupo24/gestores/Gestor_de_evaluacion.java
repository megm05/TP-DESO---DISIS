package com.grupo24.gestores;

import com.grupo24.dominio.Candidato;
import com.grupo24.dominio.Caracteristicas_del_puesto;
import com.grupo24.dominio.Competencia;
import com.grupo24.dominio.Consultor;
import com.grupo24.dominio.Cuestionario;
import com.grupo24.dominio.Evaluacion;
import com.grupo24.dominio.Parametro;
import com.grupo24.dominio.Puesto;
import com.grupo24.dto.ArchivoDTO;
import com.grupo24.dto.CandidatoConClaveDTO;
import com.grupo24.dto.CompetenciaRequeridaDTO;
import com.grupo24.persistencia.CandidatoDAO;
import com.grupo24.persistencia.CuestionarioDAO;
import com.grupo24.persistencia.EvaluacionDAO;
import com.grupo24.persistencia.FabricaDAO;
import com.grupo24.persistencia.ParametroDAO;
import com.grupo24.persistencia.PuestoDAO;
import com.grupo24.persistencia.Transaccion;
import com.grupo24.util.ExportadorExcel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Controlador del CU025 - Evaluar a Candidatos. */
public class Gestor_de_evaluacion {

    private static final float TIEMPO_MAX_POR_DEFECTO = 72f;

    private final EvaluacionDAO evaluacionDAO;
    private final CandidatoDAO candidatoDAO;
    private final CuestionarioDAO cuestionarioDAO;
    private final PuestoDAO puestoDAO;
    private final ParametroDAO parametroDAO;

    public Gestor_de_evaluacion() {
        this(FabricaDAO.getEvaluacionDAO(), FabricaDAO.getCandidatoDAO(), FabricaDAO.getCuestionarioDAO(),
                FabricaDAO.getPuestoDAO(), FabricaDAO.getParametroDAO());
    }

    public Gestor_de_evaluacion(EvaluacionDAO evaluacionDAO, CandidatoDAO candidatoDAO,
            CuestionarioDAO cuestionarioDAO, PuestoDAO puestoDAO, ParametroDAO parametroDAO) {
        this.evaluacionDAO = evaluacionDAO;
        this.candidatoDAO = candidatoDAO;
        this.cuestionarioDAO = cuestionarioDAO;
        this.puestoDAO = puestoDAO;
        this.parametroDAO = parametroDAO;
    }

    /** Comienza una evaluación nueva, todavía sin candidatos ni puesto (paso 1). */
    public Evaluacion iniciar_evaluacion(Consultor consultor) {
        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setConsultor(consultor);
        return evaluacion;
    }

    /** Agrega los candidatos seleccionados a la grilla "Candidatos a evaluar". */
    public void agregar_candidatos(Evaluacion evaluacion, List<Integer> nros_candidato) {
        if (nros_candidato == null || nros_candidato.isEmpty()) {
            throw new ValidacionException("Debe seleccionar uno o más candidatos.");
        }
        for (Integer numero : nros_candidato) {
            Candidato candidato = candidatoDAO.obtener_por_numero(numero);
            if (candidato != null) {
                evaluacion.agregar_candidato(candidato);
            }
        }
        // si cambia la grilla, las claves generadas antes dejan de ser válidas
        evaluacion.setCuestionarios(new ArrayList<>());
    }

    /** Flujo 5.A: quita candidatos de la grilla "Candidatos a evaluar". */
    public void quitar_candidatos(Evaluacion evaluacion, List<Integer> nros_candidato) {
        if (nros_candidato == null || nros_candidato.isEmpty()) {
            throw new ValidacionException("Debe seleccionar uno o más candidatos para quitar.");
        }
        for (Candidato candidato : new ArrayList<>(evaluacion.getCandidatos())) {
            if (nros_candidato.contains(candidato.getNum_candidato())) {
                evaluacion.quitar_candidato(candidato);
            }
        }
        evaluacion.setCuestionarios(new ArrayList<>());
    }

    /** Flujo 6.A: un candidato con un cuestionario activo o en proceso no puede ser evaluado nuevamente. */
    public List<Candidato> validar_candidatos_disponibles(List<Candidato> candidatos) {
        List<Candidato> noDisponibles = new ArrayList<>();
        for (Candidato candidato : candidatos) {
            if (cuestionarioDAO.obtener_activo_por_candidato(candidato) != null) {
                noDisponibles.add(candidato);
            }
        }
        return noDisponibles;
    }

    /** Asigna a la evaluación la función o puesto elegido por el actor (paso 8). */
    public void seleccionar_puesto(Evaluacion evaluacion, int id_puesto) {
        Puesto puesto = puestoDAO.obtener_por_id(id_puesto);
        if (puesto == null) {
            throw new ValidacionException("La función o puesto seleccionado no existe.");
        }
        evaluacion.seleccionar_puesto(puesto);
        evaluacion.setCuestionarios(new ArrayList<>());
    }

    /**
     * Flujo 9.A: competencias de la función que NO poseen al menos un factor en condiciones de ser evaluado.
     */
    public List<Competencia> validar_competencias_evaluables(Puesto puesto) {
        List<Competencia> noEvaluables = new ArrayList<>();
        for (Competencia competencia : puesto.obtener_competencias()) {
            if (!competencia.factores_evaluables()) {
                noEvaluables.add(competencia);
            }
        }
        return noEvaluables;
    }

    /** Paso 9: competencias asociadas a la función junto a la ponderación requerida en cada una. */
    public List<CompetenciaRequeridaDTO> obtener_competencias_requeridas(Puesto puesto) {
        List<CompetenciaRequeridaDTO> requeridas = new ArrayList<>();
        for (Caracteristicas_del_puesto caracteristica : puesto.getCaracteristicas()) {
            requeridas.add(new CompetenciaRequeridaDTO(caracteristica.getCompetencia().getNombre(),
                    caracteristica.getPonderacion()));
        }
        return requeridas;
    }

    /**
     * Paso 11: genera, todavía en memoria, un cuestionario en estado ACTIVO con su clave de ingreso para cada
     * candidato a evaluar.
     */
    public List<CandidatoConClaveDTO> generar_evaluacion(Evaluacion evaluacion) {
        if (evaluacion.getCandidatos().isEmpty()) {
            throw new ValidacionException("Debe agregar al menos un candidato a la grilla \"Candidatos a evaluar\".");
        }
        if (evaluacion.getPuesto() == null) {
            throw new ValidacionException("Debe seleccionar una función o puesto.");
        }
        validarQueSePuedeEvaluar(evaluacion);
        if (evaluacion.getCuestionarios().size() != evaluacion.getCandidatos().size()) {
            Parametro tiempo = parametroDAO.obtener_por_clave(Parametro.TIEMPO_MAX_COMPLETAR);
            float tiempo_max = TIEMPO_MAX_POR_DEFECTO;
            if (tiempo != null) {
                try {
                    tiempo_max = Float.parseFloat(tiempo.getValor().trim());
                } catch (NumberFormatException e) {
                    tiempo_max = TIEMPO_MAX_POR_DEFECTO;
                }
            }
            evaluacion.generar_cuestionarios(tiempo_max);
        }
        return obtener_candidatos_con_clave(evaluacion);
    }

    /** Lista de candidatos de la evaluación con su clave de ingreso (pasos 11 y 13). */
    public List<CandidatoConClaveDTO> obtener_candidatos_con_clave(Evaluacion evaluacion) {
        List<CandidatoConClaveDTO> lista = new ArrayList<>();
        for (Cuestionario cuestionario : evaluacion.getCuestionarios()) {
            Candidato candidato = cuestionario.getCandidato();
            lista.add(new CandidatoConClaveDTO(candidato.getApellido(), candidato.getNombre(),
                    candidato.getTipo_doc().name(), candidato.getNro_doc(), cuestionario.getClave_cuestionario()));
        }
        return lista;
    }

    /** Paso 13: guarda la evaluación con los cuestionarios en estado "activo" de cada candidato. */
    public void guardar_evaluacion(Evaluacion evaluacion) {
        if (evaluacion.getId_evaluacion() != 0) {
            return;
        }
        if (evaluacion.getCuestionarios().isEmpty()) {
            throw new ValidacionException("La evaluación todavía no fue generada.");
        }
        Transaccion.realizar(() -> {
            validarQueSePuedeEvaluar(evaluacion);
            evaluacionDAO.guardar(evaluacion);
        });
    }

    private void validarQueSePuedeEvaluar(Evaluacion evaluacion) {
        List<Candidato> noDisponibles = validar_candidatos_disponibles(evaluacion.getCandidatos());
        if (!noDisponibles.isEmpty()) {
            List<String> errores = new ArrayList<>();
            for (Candidato candidato : noDisponibles) {
                errores.add("El candidato " + candidato.getApellido() + ", " + candidato.getNombre()
                        + " posee un cuestionario activo o en proceso y no puede ser evaluado nuevamente.");
            }
            throw new ValidacionException(errores);
        }
        List<Competencia> noEvaluables = validar_competencias_evaluables(evaluacion.getPuesto());
        if (!noEvaluables.isEmpty()) {
            List<String> errores = new ArrayList<>();
            for (Competencia competencia : noEvaluables) {
                errores.add("La competencia " + competencia.getNombre() + " no puede ser evaluada.");
            }
            throw new ValidacionException(errores);
        }
    }

    /** Pasos 14 a 17: lista de candidatos con sus claves en un archivo Excel. */
    public ArchivoDTO exportar_claves_excel(Evaluacion evaluacion) {
        if (evaluacion.getId_evaluacion() == 0) {
            throw new ValidacionException("Debe finalizar la evaluación antes de exportar las claves.");
        }
        Puesto puesto = evaluacion.getPuesto();
        List<String[]> filas = new ArrayList<>();
        for (CandidatoConClaveDTO candidato : obtener_candidatos_con_clave(evaluacion)) {
            filas.add(new String[] {candidato.getApellido(), candidato.getNombre(), candidato.getTipo_doc(),
                candidato.getNro_doc(), candidato.getClave_ingreso()});
        }
        String titulo = "Evaluación N° " + evaluacion.getId_evaluacion() + " - " + puesto.getNombre()
                + " (" + puesto.getEmpresa().getNombre() + ")";
        byte[] contenido = ExportadorExcel.generar("Candidatos", titulo,
                new String[] {"Apellido", "Nombre", "Tipo documento", "Nro documento", "Clave de ingreso"},
                filas);
        String funcion = puesto.getNombre().replaceAll("[\\\\/:*?\"<>|]", "-").trim();
        String nombre = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"))
                + "-" + funcion + ".xlsx";
        return new ArchivoDTO(nombre, contenido);
    }
}
