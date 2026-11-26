package com.grupo12.gestores;

import com.grupo12.dominio.Candidato;
import com.grupo12.dto.CandidatoBusquedaDTO;
import com.grupo12.persistencia.CandidatoDAO;
import com.grupo12.persistencia.FabricaDAO;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de candidatos. En esta entrega solo se implementa la búsqueda, que usa
 * la evaluación de candidatos (CU025 pasos 2 y 3); la importación y exportación
 * (CU006 y CU007) quedan fuera del alcance de la implementación.
 */
public class Gestor_de_candidato {

    private final CandidatoDAO candidatoDAO;

    public Gestor_de_candidato() {
        this(FabricaDAO.getCandidatoDAO());
    }

    public Gestor_de_candidato(CandidatoDAO candidatoDAO) {
        this.candidatoDAO = candidatoDAO;
    }

    /** Candidatos que cumplen con los filtros: Apellido, Nombre y Número de Candidato. */
    public List<CandidatoBusquedaDTO> buscar(String apellido, String nombre, Integer num_candidato) {
        List<CandidatoBusquedaDTO> resultado = new ArrayList<>();
        for (Candidato candidato : candidatoDAO.buscar_por_filtros(apellido, nombre, num_candidato)) {
            resultado.add(new CandidatoBusquedaDTO(candidato.getNum_candidato(), candidato.getApellido(),
                    candidato.getNombre(), candidato.getTipo_doc().name(), candidato.getNro_doc()));
        }
        return resultado;
    }
}
