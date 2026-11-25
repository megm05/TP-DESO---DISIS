package com.grupo12.persistencia;

import com.grupo12.dominio.BloquePregunta;
import com.grupo12.dominio.Candidato;
import com.grupo12.dominio.Competencia;
import com.grupo12.dominio.Cuestionario;
import com.grupo12.dominio.Detalle_opcion;
import com.grupo12.dominio.Estado;
import com.grupo12.dominio.Evaluacion;
import com.grupo12.dominio.Factor;
import com.grupo12.dominio.Pregunta;
import com.grupo12.dominio.Puesto;
import com.grupo12.dominio.Respuesta;
import com.grupo12.dominio.Resultado_competencia;
import com.grupo12.dominio.Resultado_factor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Implementación JDBC (PostgreSQL) de {@link CuestionarioDAO}. */
public class CuestionarioDAOImpl extends DAOBase implements CuestionarioDAO {

    private static final String SELECT = "SELECT q.id_cuestionario, q.id_evaluacion, q.clave, q.estado,"
            + " q.fecha_ini, q.fecha_fin, q.ultimo_ingreso, q.fecha_cambio_estado, q.cant_accesos,"
            + " q.tiempo_max, " + CandidatoDAOImpl.COLUMNAS
            + " FROM Cuestionario q JOIN Candidato ca ON ca.id_candidato = q.id_candidato";

    // ------------------------------------------------------------------
    //  Consultas
    // ------------------------------------------------------------------

    @Override
    public Cuestionario obtener_por_id(int id) {
        Cuestionario cuestionario = consultarUno(SELECT + " WHERE q.id_cuestionario = ?",
                CuestionarioDAOImpl::mapear, id);
        if (cuestionario != null) {
            completar(cuestionario);
        }
        return cuestionario;
    }

    @Override
    public Cuestionario obtener_activo_por_candidato(Candidato candidato) {
        Cuestionario cuestionario = consultarUno(SELECT + " WHERE q.id_candidato = ?"
                + " AND q.estado IN ('ACTIVO', 'EN_PROCESO') ORDER BY q.id_cuestionario DESC",
                CuestionarioDAOImpl::mapear, candidato.getId_candidato());
        if (cuestionario != null) {
            cuestionario.setCandidato(candidato);
            completar(cuestionario);
        }
        return cuestionario;
    }

    @Override
    public List<Cuestionario> obtener_por_evaluaciones(List<Evaluacion> evaluaciones) {
        if (evaluaciones.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Integer, Evaluacion> porId = new HashMap<>();
        List<Object> ids = new ArrayList<>();
        for (Evaluacion evaluacion : evaluaciones) {
            porId.put(evaluacion.getId_evaluacion(), evaluacion);
            ids.add(evaluacion.getId_evaluacion());
        }
        List<Cuestionario> cuestionarios = consultar(SELECT + " WHERE q.id_evaluacion IN ("
                + CargadorCatalogo.marcadores(ids.size()) + ") ORDER BY q.id_cuestionario",
                CuestionarioDAOImpl::mapear, ids.toArray());
        for (Cuestionario cuestionario : cuestionarios) {
            cuestionario.setEvaluacion(porId.get(cuestionario.getEvaluacion().getId_evaluacion()));
        }
        cargarResultados(cuestionarios);
        return cuestionarios;
    }

    @Override
    public int contar_por_puesto(Puesto puesto) {
        return contar("SELECT COUNT(*) FROM Cuestionario q JOIN Evaluacion e"
                + " ON e.id_evaluacion = q.id_evaluacion WHERE e.id_puesto = ?", puesto.getId_puesto());
    }

    @Override
    public int contar_completos_por_puesto(Puesto puesto) {
        return contar("SELECT COUNT(*) FROM Cuestionario q JOIN Evaluacion e"
                + " ON e.id_evaluacion = q.id_evaluacion WHERE e.id_puesto = ? AND q.estado = ?",
                puesto.getId_puesto(), Estado.COMPLETO);
    }

    // ------------------------------------------------------------------
    //  Altas y modificaciones
    // ------------------------------------------------------------------

    @Override
    public void guardar(Cuestionario cuestionario) {
        int id = insertar("INSERT INTO Cuestionario (id_evaluacion, id_candidato, clave, estado, fecha_ini,"
                + " fecha_fin, ultimo_ingreso, fecha_cambio_estado, cant_accesos, tiempo_max)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", "id_cuestionario",
                cuestionario.getEvaluacion().getId_evaluacion(),
                cuestionario.getCandidato().getId_candidato(),
                cuestionario.getClave_cuestionario(), cuestionario.getEstado_cuestionario(),
                cuestionario.getFecha_ini(), cuestionario.getFecha_fin(), cuestionario.getUltimo_ingreso(),
                cuestionario.getFecha_cambio_estado(), cuestionario.getCant_accesos(),
                (double) cuestionario.getTiempoMax());
        cuestionario.setId_cuestionario(id);
        guardarBloques(cuestionario);
        guardarResultados(cuestionario);
    }

    @Override
    public void actualizar(Cuestionario cuestionario) {
        ejecutar("UPDATE Cuestionario SET clave = ?, estado = ?, fecha_ini = ?, fecha_fin = ?,"
                + " ultimo_ingreso = ?, fecha_cambio_estado = ?, cant_accesos = ?, tiempo_max = ?"
                + " WHERE id_cuestionario = ?",
                cuestionario.getClave_cuestionario(), cuestionario.getEstado_cuestionario(),
                cuestionario.getFecha_ini(), cuestionario.getFecha_fin(), cuestionario.getUltimo_ingreso(),
                cuestionario.getFecha_cambio_estado(), cuestionario.getCant_accesos(),
                (double) cuestionario.getTiempoMax(), cuestionario.getId_cuestionario());
        guardarBloques(cuestionario);
        guardarResultados(cuestionario);
    }

    /** Inserta los bloques y respuestas nuevos y actualiza los existentes. */
    private void guardarBloques(Cuestionario cuestionario) {
        for (BloquePregunta bloque : cuestionario.getBloques()) {
            if (bloque.getId_bloque() == 0) {
                bloque.setId_bloque(insertar("INSERT INTO Bloque_pregunta (id_cuestionario, numero, completado)"
                        + " VALUES (?, ?, ?)", "id_bloque",
                        cuestionario.getId_cuestionario(), bloque.getNumero(), bloque.isCompletado()));
            } else {
                ejecutar("UPDATE Bloque_pregunta SET completado = ? WHERE id_bloque = ?",
                        bloque.isCompletado(), bloque.getId_bloque());
            }
            for (Respuesta respuesta : bloque.obtener_respuestas()) {
                Integer id_detalle = respuesta.getDetalle_elegido() == null
                        ? null : respuesta.getDetalle_elegido().getId_detalle();
                if (respuesta.getId_respuesta() == 0) {
                    respuesta.setId_respuesta(insertar("INSERT INTO Respuesta (id_bloque, id_pregunta,"
                            + " id_candidato, id_detalle, orden, fecha) VALUES (?, ?, ?, ?, ?, ?)", "id_respuesta",
                            bloque.getId_bloque(), respuesta.getPregunta().getId_pregunta(),
                            respuesta.getCandidato().getId_candidato(), id_detalle, respuesta.getOrden(),
                            respuesta.getFecha()));
                } else {
                    ejecutar("UPDATE Respuesta SET id_detalle = ?, fecha = ? WHERE id_respuesta = ?",
                            id_detalle, respuesta.getFecha(), respuesta.getId_respuesta());
                }
            }
        }
    }

    /** Reemplaza los resultados guardados por los calculados en el cuestionario. */
    private void guardarResultados(Cuestionario cuestionario) {
        if (cuestionario.obtener_resultados_competencia().isEmpty()) {
            return;
        }
        int id = cuestionario.getId_cuestionario();
        ejecutar("DELETE FROM Resultado_factor WHERE id_cuestionario = ?", id);
        ejecutar("DELETE FROM Resultado_competencia WHERE id_cuestionario = ?", id);
        for (Resultado_competencia resultado : cuestionario.obtener_resultados_competencia()) {
            ejecutar("INSERT INTO Resultado_competencia (id_cuestionario, id_competencia, total)"
                    + " VALUES (?, ?, ?)", id, resultado.getCompetencia().getId_competencia(),
                    resultado.getTotal());
            for (Resultado_factor resultadoFactor : resultado.getResultados_factor()) {
                ejecutar("INSERT INTO Resultado_factor (id_cuestionario, id_factor, total) VALUES (?, ?, ?)",
                        id, resultadoFactor.getFactor().getId_factor(), resultadoFactor.getTotal());
            }
        }
    }

    // ------------------------------------------------------------------
    //  Armado de objetos
    // ------------------------------------------------------------------

    /** Cabecera del cuestionario con su candidato; la evaluación queda solo con su identificador. */
    private static Cuestionario mapear(ResultSet rs) throws SQLException {
        Cuestionario cuestionario = new Cuestionario();
        cuestionario.setId_cuestionario(rs.getInt("id_cuestionario"));
        cuestionario.setClave_cuestionario(rs.getString("clave"));
        cuestionario.setEstado_cuestionario(Estado.valueOf(rs.getString("estado").trim()));
        cuestionario.setFecha_ini(fechaHora(rs, "fecha_ini"));
        cuestionario.setFecha_fin(fechaHora(rs, "fecha_fin"));
        cuestionario.setUltimo_ingreso(fechaHora(rs, "ultimo_ingreso"));
        cuestionario.setFecha_cambio_estado(fechaHora(rs, "fecha_cambio_estado"));
        cuestionario.setCant_accesos(rs.getInt("cant_accesos"));
        cuestionario.setTiempoMax(rs.getFloat("tiempo_max"));
        cuestionario.setCandidato(CandidatoDAOImpl.mapear(rs));
        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setId_evaluacion(rs.getInt("id_evaluacion"));
        cuestionario.setEvaluacion(evaluacion);
        return cuestionario;
    }

    /** Completa el cuestionario con su evaluación (y puesto), sus bloques, respuestas y resultados. */
    private void completar(Cuestionario cuestionario) {
        cuestionario.setEvaluacion(new EvaluacionDAOImpl()
                .obtenerCabecera(cuestionario.getEvaluacion().getId_evaluacion()));

        Map<Integer, BloquePregunta> bloques = new HashMap<>();
        cuestionario.setBloques(consultar("SELECT id_bloque, numero, completado FROM Bloque_pregunta"
                + " WHERE id_cuestionario = ? ORDER BY numero",
                rs -> {
                    BloquePregunta bloque = new BloquePregunta();
                    bloque.setId_bloque(rs.getInt("id_bloque"));
                    bloque.setNumero(rs.getInt("numero"));
                    bloque.setCompletado(rs.getBoolean("completado"));
                    bloques.put(bloque.getId_bloque(), bloque);
                    return bloque;
                }, cuestionario.getId_cuestionario()));

        if (!bloques.isEmpty()) {
            // cada fila: { respuesta, id_bloque, id_pregunta, id_detalle }
            List<Object[]> filas = consultar("SELECT r.id_respuesta, r.id_bloque, r.id_pregunta, r.id_detalle,"
                    + " r.orden, r.fecha FROM Respuesta r JOIN Bloque_pregunta b ON b.id_bloque = r.id_bloque"
                    + " WHERE b.id_cuestionario = ? ORDER BY r.orden",
                    rs -> {
                        Respuesta respuesta = new Respuesta();
                        respuesta.setId_respuesta(rs.getInt("id_respuesta"));
                        respuesta.setOrden(rs.getInt("orden"));
                        respuesta.setFecha(fechaHora(rs, "fecha"));
                        return new Object[] {respuesta, rs.getInt("id_bloque"), rs.getInt("id_pregunta"),
                            enteroONulo(rs, "id_detalle")};
                    }, cuestionario.getId_cuestionario());

            Set<Object> idsPregunta = new LinkedHashSet<>();
            for (Object[] fila : filas) {
                idsPregunta.add(fila[2]);
            }
            Map<Integer, Pregunta> preguntas = new HashMap<>();
            if (!idsPregunta.isEmpty()) {
                for (Pregunta pregunta : new CargadorCatalogo().preguntas("p.id_pregunta IN ("
                        + CargadorCatalogo.marcadores(idsPregunta.size()) + ")", idsPregunta.toArray())) {
                    preguntas.put(pregunta.getId_pregunta(), pregunta);
                }
            }
            for (Object[] fila : filas) {
                Respuesta respuesta = (Respuesta) fila[0];
                Pregunta pregunta = preguntas.get((Integer) fila[2]);
                respuesta.setPregunta(pregunta);
                respuesta.setCandidato(cuestionario.getCandidato());
                Integer id_detalle = (Integer) fila[3];
                if (id_detalle != null) {
                    for (Detalle_opcion detalle : pregunta.getOpcion_respuesta().obtener_detalles()) {
                        if (detalle.getId_detalle() == id_detalle) {
                            respuesta.setDetalle_elegido(detalle);
                        }
                    }
                }
                bloques.get((Integer) fila[1]).obtener_respuestas().add(respuesta);
            }
        }
        cargarResultados(Collections.singletonList(cuestionario));
    }

    /** Carga los resultados por competencia y por factor de los cuestionarios recibidos. */
    private void cargarResultados(List<Cuestionario> cuestionarios) {
        if (cuestionarios.isEmpty()) {
            return;
        }
        Map<Integer, Cuestionario> porId = new HashMap<>();
        List<Object> ids = new ArrayList<>();
        for (Cuestionario cuestionario : cuestionarios) {
            cuestionario.setResultados(new ArrayList<>());
            porId.put(cuestionario.getId_cuestionario(), cuestionario);
            ids.add(cuestionario.getId_cuestionario());
        }
        String marcadores = CargadorCatalogo.marcadores(ids.size());
        // clave: id_cuestionario + "-" + id_competencia
        Map<String, Resultado_competencia> resultados = new HashMap<>();
        consultar("SELECT rc.id_cuestionario, rc.total, c.id_competencia, c.nombre, c.tipo"
                + " FROM Resultado_competencia rc JOIN Competencia c ON c.id_competencia = rc.id_competencia"
                + " WHERE rc.id_cuestionario IN (" + marcadores + ") ORDER BY c.nombre",
                rs -> {
                    Competencia competencia = new Competencia();
                    competencia.setId_competencia(rs.getInt("id_competencia"));
                    competencia.setNombre(rs.getString("nombre"));
                    competencia.setTipo(rs.getString("tipo"));
                    Resultado_competencia resultado = new Resultado_competencia();
                    resultado.setCompetencia(competencia);
                    resultado.setTotal(rs.getDouble("total"));
                    int id_cuestionario = rs.getInt("id_cuestionario");
                    porId.get(id_cuestionario).obtener_resultados_competencia().add(resultado);
                    resultados.put(id_cuestionario + "-" + competencia.getId_competencia(), resultado);
                    return resultado;
                }, ids.toArray());
        consultar("SELECT rf.id_cuestionario, rf.total, f.id_factor, f.nombre, f.descripcion, f.id_competencia"
                + " FROM Resultado_factor rf JOIN Factor f ON f.id_factor = rf.id_factor"
                + " WHERE rf.id_cuestionario IN (" + marcadores + ") ORDER BY f.nombre",
                rs -> {
                    Resultado_competencia resultado = resultados.get(
                            rs.getInt("id_cuestionario") + "-" + rs.getInt("id_competencia"));
                    if (resultado == null) {
                        return null;
                    }
                    Factor factor = new Factor();
                    factor.setId_factor(rs.getInt("id_factor"));
                    factor.setNombre(rs.getString("nombre"));
                    factor.setDescripcion(rs.getString("descripcion"));
                    factor.setCompetencia(resultado.getCompetencia());
                    Resultado_factor resultadoFactor = new Resultado_factor();
                    resultadoFactor.setFactor(factor);
                    resultadoFactor.setTotal(rs.getDouble("total"));
                    resultado.getResultados_factor().add(resultadoFactor);
                    return resultadoFactor;
                }, ids.toArray());
    }
}
