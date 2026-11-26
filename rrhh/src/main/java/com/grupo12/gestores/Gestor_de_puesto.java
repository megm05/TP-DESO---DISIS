package com.grupo12.gestores;

import com.grupo12.dominio.Puesto;
import com.grupo12.dto.PuestoSeleccionDTO;
import com.grupo12.persistencia.FabricaDAO;
import com.grupo12.persistencia.PuestoDAO;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de puestos. En esta entrega solo se implementa la búsqueda, que usa la
 * evaluación de candidatos (CU025 paso 8); el ABM de puestos (CU008 a CU011)
 * queda fuera del alcance de la implementación.
 */
public class Gestor_de_puesto {

    private final PuestoDAO puestoDAO;

    public Gestor_de_puesto() {
        this(FabricaDAO.getPuestoDAO());
    }

    public Gestor_de_puesto(PuestoDAO puestoDAO) {
        this.puestoDAO = puestoDAO;
    }

    /**
     * Funciones o puestos que coinciden con los criterios, con el nombre de su empresa.
     * Los criterios nulos o vacíos no se aplican.
     */
    public List<PuestoSeleccionDTO> buscar(Integer codigo, String nombre_puesto, String nombre_empresa) {
        List<PuestoSeleccionDTO> resultado = new ArrayList<>();
        for (Puesto puesto : puestoDAO.buscar_por_filtros(codigo, nombre_puesto, nombre_empresa)) {
            resultado.add(new PuestoSeleccionDTO(puesto.getId_puesto(), puesto.getNombre(),
                    puesto.getEmpresa().getNombre()));
        }
        return resultado;
    }
}
