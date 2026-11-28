package com.grupo24.gestores;

import com.grupo24.dominio.Candidato;
import com.grupo24.dto.CandidatoBusquedaDTO;
import com.grupo24.persistencia.CandidatoDAO;
import com.grupo24.persistencia.FabricaDAO;
import java.util.ArrayList;
import java.util.List;

/** Gestor de candidatos. */
public class Gestor_de_candidato {

    private final CandidatoDAO candidatoDAO;

    public Gestor_de_candidato() {
        this(FabricaDAO.getCandidatoDAO());
    }

    public Gestor_de_candidato(CandidatoDAO candidatoDAO) {
        this.candidatoDAO = candidatoDAO;
    }

    public List<CandidatoBusquedaDTO> buscar(String apellido, String nombre, Integer num_candidato) {
        List<CandidatoBusquedaDTO> resultado = new ArrayList<>();
        for (Candidato candidato : candidatoDAO.buscar_por_filtros(apellido, nombre, num_candidato)) {
            resultado.add(new CandidatoBusquedaDTO(candidato.getNum_candidato(), candidato.getApellido(),
                    candidato.getNombre(), candidato.getTipo_doc().name(), candidato.getNro_doc()));
        }
        return resultado;
    }
}
