package com.grupo12.persistencia;

/**
 * Punto único donde se elige la implementación de cada DAO. Los gestores trabajan
 * contra las interfaces: cambiar de motor de base de datos (o usar una
 * implementación en memoria para pruebas) solo requiere modificar esta clase.
 */
public final class FabricaDAO {

    private FabricaDAO() {
    }

    public static ParametroDAO getParametroDAO() {
        return new ParametroDAOImpl();
    }

    public static ConsultorDAO getConsultorDAO() {
        return new ConsultorDAOImpl();
    }

    public static CandidatoDAO getCandidatoDAO() {
        return new CandidatoDAOImpl();
    }

    public static CompetenciaDAO getCompetenciaDAO() {
        return new CompetenciaDAOImpl();
    }

    public static FactorDAO getFactorDAO() {
        return new FactorDAOImpl();
    }

    public static PreguntaDAO getPreguntaDAO() {
        return new PreguntaDAOImpl();
    }

    public static OpcionRespuestaDAO getOpcionRespuestaDAO() {
        return new OpcionRespuestaDAOImpl();
    }

    public static PuestoDAO getPuestoDAO() {
        return new PuestoDAOImpl();
    }

    public static EvaluacionDAO getEvaluacionDAO() {
        return new EvaluacionDAOImpl();
    }

    public static CuestionarioDAO getCuestionarioDAO() {
        return new CuestionarioDAOImpl();
    }

    public static RespuestaDAO getRespuestaDAO() {
        return new RespuestaDAOImpl();
    }

    public static RegistroAuditoriaDAO getRegistroAuditoriaDAO() {
        return new RegistroAuditoriaDAOImpl();
    }

    public static OrdenMeritoDAO getOrdenMeritoDAO() {
        return new OrdenMeritoDAOImpl();
    }
}
