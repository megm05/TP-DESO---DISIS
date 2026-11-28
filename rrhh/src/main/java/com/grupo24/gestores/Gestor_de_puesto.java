package com.grupo24.gestores;

import com.grupo24.dominio.Puesto;
import com.grupo24.dto.PuestoSeleccionDTO;
import com.grupo24.persistencia.FabricaDAO;
import com.grupo24.persistencia.PuestoDAO;
import java.util.ArrayList;
import java.util.List;

/** Gestor de puestos. */
public class Gestor_de_puesto {

    private final PuestoDAO puestoDAO;

    public Gestor_de_puesto() {
        this(FabricaDAO.getPuestoDAO());
    }

    public Gestor_de_puesto(PuestoDAO puestoDAO) {
        this.puestoDAO = puestoDAO;
    }

    public List<PuestoSeleccionDTO> buscar(Integer codigo, String nombre_puesto, String nombre_empresa) {
        List<PuestoSeleccionDTO> resultado = new ArrayList<>();
        for (Puesto puesto : puestoDAO.buscar_por_filtros(codigo, nombre_puesto, nombre_empresa)) {
            resultado.add(new PuestoSeleccionDTO(puesto.getId_puesto(), puesto.getNombre(),
                    puesto.getEmpresa().getNombre()));
        }
        return resultado;
    }
}
