package com.grupo12.gestores;

import com.grupo12.dominio.Candidato;
import com.grupo12.dominio.Consultor;
import com.grupo12.dominio.Cuestionario;
import com.grupo12.dominio.Tipo_doc;
import com.grupo12.persistencia.CandidatoDAO;
import com.grupo12.persistencia.ConsultorDAO;
import com.grupo12.persistencia.CuestionarioDAO;
import com.grupo12.persistencia.FabricaDAO;

/** Controlador de la autenticación (CU001). */
public class Gestor_de_autenticacion {

    private final ConsultorDAO consultorDAO;
    private final CandidatoDAO candidatoDAO;
    private final CuestionarioDAO cuestionarioDAO;

    public Gestor_de_autenticacion() {
        this(FabricaDAO.getConsultorDAO(), FabricaDAO.getCandidatoDAO(), FabricaDAO.getCuestionarioDAO());
    }

    public Gestor_de_autenticacion(ConsultorDAO consultorDAO, CandidatoDAO candidatoDAO,
            CuestionarioDAO cuestionarioDAO) {
        this.consultorDAO = consultorDAO;
        this.candidatoDAO = candidatoDAO;
        this.cuestionarioDAO = cuestionarioDAO;
    }

    /** CU001 flujo principal: valida usuario y contraseña del consultor. */
    public Consultor autenticar_consultor(String nombre_usuario, String contrasenia) {
        Consultor consultor = consultorDAO.obtener_por_nombre_usuario(nombre_usuario);
        if (consultor == null || !consultor.autenticar(contrasenia)) {
            return null;
        }
        return consultor;
    }

    /**
     * CU001 flujo 3.A: comprueba que exista un candidato para ese tipo y número de documento, que tenga un
     * cuestionario activo o en proceso y que la clave ingresada corresponda a ese cuestionario.
     */
    public Candidato autenticar_candidato(Tipo_doc tipo_doc, String nro_doc, String clave) {
        if (tipo_doc == null || nro_doc == null || clave == null) {
            return null;
        }
        Candidato candidato = candidatoDAO.obtener_por_documento(tipo_doc, nro_doc);
        if (candidato == null) {
            return null;
        }
        Cuestionario cuestionario = cuestionarioDAO.obtener_activo_por_candidato(candidato);
        if (cuestionario == null || !cuestionario.validar_clave(clave)) {
            return null;
        }
        return candidato;
    }
}
