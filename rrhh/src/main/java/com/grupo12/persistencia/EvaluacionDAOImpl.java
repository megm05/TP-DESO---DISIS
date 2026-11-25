package com.grupo12.persistencia;

import com.grupo12.dominio.Consultor;
import com.grupo12.dominio.Cuestionario;
import com.grupo12.dominio.Evaluacion;
import com.grupo12.dominio.Puesto;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Implementación JDBC (PostgreSQL) de {@link EvaluacionDAO}. */
public class EvaluacionDAOImpl extends DAOBase implements EvaluacionDAO {

    private static final String SELECT = "SELECT e.id_evaluacion, e.id_puesto, e.fecha_generacion, e.estado,"
            + " co.id_consultor, co.nombre_usuario, co.contrasenia, co.nombre, co.apellido"
            + " FROM Evaluacion e JOIN Consultor co ON co.id_consultor = e.id_consultor";

    @Override
    public Evaluacion obtener_por_id(int id) {
        Evaluacion evaluacion = obtenerCabecera(id);
        if (evaluacion != null) {
            cargarCuestionarios(Collections.singletonList(evaluacion));
        }
        return evaluacion;
    }

    @Override
    public void guardar(Evaluacion evaluacion) {
        int id = insertar("INSERT INTO Evaluacion (id_puesto, id_consultor, fecha_generacion, estado)"
                + " VALUES (?, ?, ?, ?)", "id_evaluacion",
                evaluacion.getPuesto().getId_puesto(), evaluacion.getConsultor().getId_consultor(),
                evaluacion.getFecha_generacion(), evaluacion.getEstado());
        evaluacion.setId_evaluacion(id);
        // los cuestionarios se persisten en cascada (composición con la evaluación)
        CuestionarioDAOImpl cuestionarioDAO = new CuestionarioDAOImpl();
        for (Cuestionario cuestionario : evaluacion.getCuestionarios()) {
            cuestionarioDAO.guardar(cuestionario);
        }
    }

    @Override
    public List<Evaluacion> obtener_por_puesto(Puesto puesto) {
        List<Evaluacion> evaluaciones = consultar(SELECT + " WHERE e.id_puesto = ?"
                + " ORDER BY e.fecha_generacion, e.id_evaluacion", EvaluacionDAOImpl::mapear,
                puesto.getId_puesto());
        for (Evaluacion evaluacion : evaluaciones) {
            evaluacion.seleccionar_puesto(puesto);
        }
        cargarCuestionarios(evaluaciones);
        return evaluaciones;
    }

    /** Evaluación con su consultor y su puesto, sin cargar los cuestionarios. */
    Evaluacion obtenerCabecera(int id) {
        Map<Integer, Integer> puestoPorEvaluacion = new HashMap<>();
        Evaluacion evaluacion = consultarUno(SELECT + " WHERE e.id_evaluacion = ?", rs -> {
            puestoPorEvaluacion.put(rs.getInt("id_evaluacion"), rs.getInt("id_puesto"));
            return mapear(rs);
        }, id);
        if (evaluacion != null) {
            evaluacion.seleccionar_puesto(new PuestoDAOImpl().obtener_por_id(puestoPorEvaluacion.get(id)));
        }
        return evaluacion;
    }

    private void cargarCuestionarios(List<Evaluacion> evaluaciones) {
        for (Cuestionario cuestionario : new CuestionarioDAOImpl().obtener_por_evaluaciones(evaluaciones)) {
            Evaluacion evaluacion = cuestionario.getEvaluacion();
            evaluacion.getCuestionarios().add(cuestionario);
            evaluacion.agregar_candidato(cuestionario.getCandidato());
        }
    }

    private static Evaluacion mapear(ResultSet rs) throws SQLException {
        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setId_evaluacion(rs.getInt("id_evaluacion"));
        evaluacion.setFecha_generacion(fecha(rs, "fecha_generacion"));
        evaluacion.setEstado(rs.getString("estado"));
        Consultor consultor = ConsultorDAOImpl.mapear(rs);
        evaluacion.setConsultor(consultor);
        return evaluacion;
    }
}
