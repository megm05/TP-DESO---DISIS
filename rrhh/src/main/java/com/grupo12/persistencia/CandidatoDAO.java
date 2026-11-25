package com.grupo12.persistencia;

import com.grupo12.dominio.Candidato;
import com.grupo12.dominio.Tipo_doc;
import java.util.List;

/** Acceso a los datos de los candidatos. */
public interface CandidatoDAO {

    Candidato obtener_por_id(int id);

    /** @return el candidato con ese número de candidato, o null si no existe */
    Candidato obtener_por_numero(int num_candidato);

    /** @return el candidato con ese tipo y número de documento, o null si no existe */
    Candidato obtener_por_documento(Tipo_doc tipo_doc, String nro_doc);

    /**
     * Búsqueda de candidatos (CU025 paso 2). Los filtros vacíos o nulos no se aplican;
     * apellido y nombre se buscan por "contiene".
     */
    List<Candidato> buscar_por_filtros(String apellido, String nombre, Integer num_candidato);
}
