package com.grupo24.dominio;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Cuestionario generado para un candidato dentro de una evaluación (CU025) y que el candidato completa por
 * bloques (CU026).
 */
public class Cuestionario {

    /** Longitud de la clave de ingreso (CU025, requerimientos especiales). */
    public static final int LONGITUD_CLAVE = 8;

    // Se excluyen caracteres que se confunden al leerlos (O/0, I/1).
    private static final String LETRAS = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String DIGITOS = "23456789";
    private static final SecureRandom AZAR = new SecureRandom();

    private int id_cuestionario;
    private Estado estado_cuestionario;
    private String clave_cuestionario;
    private LocalDateTime fecha_ini;
    private LocalDateTime fecha_fin;
    private LocalDateTime ultimo_ingreso;
    private LocalDateTime fecha_cambio_estado;
    private int cant_accesos;
    private float tiempoMax;
    private Candidato candidato;
    private Evaluacion evaluacion;
    private List<BloquePregunta> bloques = new ArrayList<>();
    private List<Resultado_competencia> resultados = new ArrayList<>();

    /** Genera una clave aleatoria de 8 caracteres que contiene letras y dígitos. */
    public void generar_clave() {
        String alfabeto = LETRAS + DIGITOS;
        String clave;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < LONGITUD_CLAVE; i++) {
                sb.append(alfabeto.charAt(AZAR.nextInt(alfabeto.length())));
            }
            clave = sb.toString();
        } while (!contieneAlguno(clave, LETRAS) || !contieneAlguno(clave, DIGITOS));
        this.clave_cuestionario = clave;
    }

    private static boolean contieneAlguno(String texto, String caracteres) {
        for (char c : texto.toCharArray()) {
            if (caracteres.indexOf(c) >= 0) {
                return true;
            }
        }
        return false;
    }

    /** Verifica que la clave ingresada corresponda a este cuestionario (CU001 flujo 3.A.2). */
    public boolean validar_clave(String clave) {
        return clave != null && clave_cuestionario != null
                && clave_cuestionario.equalsIgnoreCase(clave.trim());
    }

    /** Deja el cuestionario en estado ACTIVO (CU025: se crean en estado "activo"). */
    public void activar() {
        cambiarEstado(Estado.ACTIVO);
    }

    /** Registra el día y hora de comienzo y pasa a EN_PROCESO (CU026 paso 3). */
    public void marcar_en_proceso() {
        fecha_ini = LocalDateTime.now();
        cambiarEstado(Estado.EN_PROCESO);
    }

    /** El candidato respondió todos los bloques (CU026 paso 8). */
    public void marcar_completo() {
        fecha_fin = LocalDateTime.now();
        cambiarEstado(Estado.COMPLETO);
    }

    /** Se superó el tiempo para completarlo o la cantidad de accesos (CU026 flujo 1.A). */
    public void marcar_incompleto() {
        cambiarEstado(Estado.INCOMPLETO);
    }

    /** Se superó el tiempo máximo en estado activo sin ser iniciado (CU026 flujo 1.B). */
    public void marcar_sin_contestar() {
        cambiarEstado(Estado.SIN_CONTESTAR);
    }

    private void cambiarEstado(Estado nuevo) {
        this.estado_cuestionario = nuevo;
        this.fecha_cambio_estado = LocalDateTime.now();
    }

    public String disponibilidad() {
        if (estado_cuestionario == null) {
            return "El cuestionario todavía no fue generado.";
        }
        switch (estado_cuestionario) {
            case ACTIVO:
                return "El cuestionario está disponible para ser iniciado.";
            case EN_PROCESO:
                return "El cuestionario está en proceso y puede continuar respondiéndolo.";
            case COMPLETO:
                return "El cuestionario ya fue completado.";
            case INCOMPLETO:
                return "El cuestionario fue cerrado en estado INCOMPLETO y ya no puede ser respondido.";
            case SIN_CONTESTAR:
                return "El cuestionario fue cerrado en estado SIN CONTESTAR y ya no puede ser respondido.";
            default:
                return "";
        }
    }

    /**
     * Tiempo máximo para completar (CU026 observación 2): fecha y hora actual menos la del primer ingreso; si
     * supera el tiempo máximo del cuestionario, venció.
     */
    public boolean vencio_plazo_completado() {
        if (estado_cuestionario != Estado.EN_PROCESO || fecha_ini == null) {
            return false;
        }
        return horasDesde(fecha_ini) > tiempoMax;
    }

    public boolean vencio_plazo_activacion(double horas_max_activo) {
        if (estado_cuestionario != Estado.ACTIVO || fecha_cambio_estado == null) {
            return false;
        }
        return horasDesde(fecha_cambio_estado) > horas_max_activo;
    }

    public boolean supero_max_accesos(int max) {
        return cant_accesos >= max;
    }

    public void registrar_ingreso() {
        cant_accesos++;
        ultimo_ingreso = LocalDateTime.now();
    }

    private static double horasDesde(LocalDateTime desde) {
        return Duration.between(desde, LocalDateTime.now()).toMillis() / 3_600_000.0;
    }

    /**
     * Asocia al cuestionario las preguntas sorteadas: las mezcla (preguntas de los distintos factores y
     * competencias) y las agrupa en bloques, creando una respuesta vacía por cada pregunta (CU026 observación
     * 1).
     */
    public void armar_cuestionario(List<Pregunta> preguntas, int cant_por_bloque) {
        List<Pregunta> mezcladas = new ArrayList<>(preguntas);
        Collections.shuffle(mezcladas, AZAR);
        bloques = new ArrayList<>();
        BloquePregunta bloque = null;
        int orden = 0;
        for (Pregunta pregunta : mezcladas) {
            if (bloque == null || bloque.obtener_respuestas().size() >= cant_por_bloque) {
                bloque = new BloquePregunta();
                bloque.setNumero(bloques.size() + 1);
                bloques.add(bloque);
            }
            orden++;
            Respuesta respuesta = new Respuesta();
            respuesta.setPregunta(pregunta);
            respuesta.setCandidato(candidato);
            respuesta.setOrden(orden);
            bloque.obtener_respuestas().add(respuesta);
        }
    }

    /** Primer bloque (paso del wizard) que aún no ha sido respondido; null si no quedan. */
    public BloquePregunta obtener_bloque_no_respondido() {
        for (BloquePregunta bloque : bloques) {
            if (!bloque.isCompletado()) {
                return bloque;
            }
        }
        return null;
    }

    /**
     * Algoritmo para el cálculo del puntaje de los factores y competencias:
     * por cada factor suma las ponderaciones de las opciones elegidas en sus
     * preguntas y guarda esa suma dividido 2 como total del factor; por cada
     * competencia suma los totales de sus factores y lo divide por la cantidad
     * de factores evaluados.
     */
    public void calcular_puntaje() {
        Map<Integer, Competencia> competencias = new LinkedHashMap<>();
        Map<Integer, Map<Integer, Factor>> factoresPorCompetencia = new LinkedHashMap<>();
        Map<Integer, Double> sumaPorFactor = new LinkedHashMap<>();

        for (BloquePregunta bloque : bloques) {
            for (Respuesta respuesta : bloque.obtener_respuestas()) {
                Factor factor = respuesta.getPregunta().getFactor();
                Competencia competencia = factor.getCompetencia();
                competencias.putIfAbsent(competencia.getId_competencia(), competencia);
                factoresPorCompetencia
                        .computeIfAbsent(competencia.getId_competencia(), k -> new LinkedHashMap<>())
                        .putIfAbsent(factor.getId_factor(), factor);
                sumaPorFactor.merge(factor.getId_factor(), respuesta.obtener_ponderacion(), Double::sum);
            }
        }

        resultados = new ArrayList<>();
        for (Competencia competencia : competencias.values()) {
            Resultado_competencia resultado = new Resultado_competencia();
            resultado.setCompetencia(competencia);
            double sumaFactores = 0;
            Map<Integer, Factor> factores = factoresPorCompetencia.get(competencia.getId_competencia());
            for (Factor factor : factores.values()) {
                double totalFactor = redondear(sumaPorFactor.get(factor.getId_factor()) / Factor.PREGUNTAS_POR_FACTOR);
                Resultado_factor resultadoFactor = new Resultado_factor();
                resultadoFactor.setFactor(factor);
                resultadoFactor.setTotal(totalFactor);
                resultado.getResultados_factor().add(resultadoFactor);
                sumaFactores += totalFactor;
            }
            resultado.setTotal(redondear(sumaFactores / factores.size()));
            resultados.add(resultado);
        }
    }

    public List<Resultado_competencia> obtener_resultados_competencia() {
        return resultados;
    }

    /** Puntaje obtenido por el candidato, usado para ordenar el orden de mérito (CU028). */
    public double obtener_puntaje_obtenido() {
        if (resultados.isEmpty()) {
            return 0;
        }
        double total = 0;
        for (Resultado_competencia resultado : resultados) {
            total += resultado.getTotal();
        }
        return redondear(total / resultados.size());
    }

    /**
     * Indica si el candidato alcanzó las ponderaciones mínimas definidas para cada competencia en el puesto
     * (CU028 paso 7).
     */
    public boolean alcanza_ponderaciones_minimas(Puesto puesto) {
        for (Caracteristicas_del_puesto caracteristica : puesto.getCaracteristicas()) {
            Resultado_competencia resultado = null;
            for (Resultado_competencia r : resultados) {
                if (r.getCompetencia().getId_competencia() == caracteristica.getCompetencia().getId_competencia()) {
                    resultado = r;
                    break;
                }
            }
            if (resultado == null || !caracteristica.cumple_ponderacion(resultado.getTotal())) {
                return false;
            }
        }
        return true;
    }

    private static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    public int getId_cuestionario() {
        return id_cuestionario;
    }

    public void setId_cuestionario(int id_cuestionario) {
        this.id_cuestionario = id_cuestionario;
    }

    public Estado getEstado_cuestionario() {
        return estado_cuestionario;
    }

    public void setEstado_cuestionario(Estado estado_cuestionario) {
        this.estado_cuestionario = estado_cuestionario;
    }

    public String getClave_cuestionario() {
        return clave_cuestionario;
    }

    public void setClave_cuestionario(String clave_cuestionario) {
        this.clave_cuestionario = clave_cuestionario;
    }

    public LocalDateTime getFecha_ini() {
        return fecha_ini;
    }

    public void setFecha_ini(LocalDateTime fecha_ini) {
        this.fecha_ini = fecha_ini;
    }

    public LocalDateTime getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(LocalDateTime fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public LocalDateTime getUltimo_ingreso() {
        return ultimo_ingreso;
    }

    public void setUltimo_ingreso(LocalDateTime ultimo_ingreso) {
        this.ultimo_ingreso = ultimo_ingreso;
    }

    public LocalDateTime getFecha_cambio_estado() {
        return fecha_cambio_estado;
    }

    public void setFecha_cambio_estado(LocalDateTime fecha_cambio_estado) {
        this.fecha_cambio_estado = fecha_cambio_estado;
    }

    public int getCant_accesos() {
        return cant_accesos;
    }

    public void setCant_accesos(int cant_accesos) {
        this.cant_accesos = cant_accesos;
    }

    public float getTiempoMax() {
        return tiempoMax;
    }

    public void setTiempoMax(float tiempoMax) {
        this.tiempoMax = tiempoMax;
    }

    public Candidato getCandidato() {
        return candidato;
    }

    public void setCandidato(Candidato candidato) {
        this.candidato = candidato;
    }

    public Evaluacion getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(Evaluacion evaluacion) {
        this.evaluacion = evaluacion;
    }

    public List<BloquePregunta> getBloques() {
        return bloques;
    }

    public void setBloques(List<BloquePregunta> bloques) {
        this.bloques = bloques;
    }

    public void setResultados(List<Resultado_competencia> resultados) {
        this.resultados = resultados;
    }
}
