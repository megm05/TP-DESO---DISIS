package com.grupo12.gestores;

import com.grupo12.dominio.Candidato;
import com.grupo12.dominio.Consultor;
import com.grupo12.dominio.Cuestionario;
import com.grupo12.dominio.Estado;
import com.grupo12.dominio.Evaluacion;
import com.grupo12.dominio.OrdenMerito;
import com.grupo12.dominio.Puesto;
import com.grupo12.dominio.Renglon_Orden_Merito;
import com.grupo12.dto.OrdenMeritoCandidatoDTO;
import com.grupo12.dto.PuestoBusquedaDTO;
import com.grupo12.persistencia.CuestionarioDAO;
import com.grupo12.persistencia.EvaluacionDAO;
import com.grupo12.persistencia.FabricaDAO;
import com.grupo12.persistencia.OrdenMeritoDAO;
import com.grupo12.persistencia.PuestoDAO;
import com.grupo12.persistencia.Transaccion;
import com.grupo12.util.Texto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Controlador del CU028 - Emitir Orden de mérito. */
public class Gestor_de_orden_merito {

    private final PuestoDAO puestoDAO;
    private final EvaluacionDAO evaluacionDAO;
    private final CuestionarioDAO cuestionarioDAO;
    private final OrdenMeritoDAO ordenMeritoDAO;

    public Gestor_de_orden_merito() {
        this(FabricaDAO.getPuestoDAO(), FabricaDAO.getEvaluacionDAO(), FabricaDAO.getCuestionarioDAO(),
                FabricaDAO.getOrdenMeritoDAO());
    }

    public Gestor_de_orden_merito(PuestoDAO puestoDAO, EvaluacionDAO evaluacionDAO,
            CuestionarioDAO cuestionarioDAO, OrdenMeritoDAO ordenMeritoDAO) {
        this.puestoDAO = puestoDAO;
        this.evaluacionDAO = evaluacionDAO;
        this.cuestionarioDAO = cuestionarioDAO;
        this.ordenMeritoDAO = ordenMeritoDAO;
    }

    /**
     * Pasos 3 y 4: puestos que coinciden con los criterios de búsqueda, con la cantidad de candidatos y de
     * evaluaciones completas de cada uno.
     */
    public List<PuestoBusquedaDTO> buscar_puestos(Integer codigo, String nombre_puesto, String nombre_empresa) {
        if (codigo == null && Texto.estaVacio(nombre_puesto) && Texto.estaVacio(nombre_empresa)) {
            throw new ValidacionException("Debe ingresar datos en al menos uno de los criterios de búsqueda.");
        }
        List<PuestoBusquedaDTO> resultado = new ArrayList<>();
        for (Puesto puesto : puestoDAO.buscar_por_filtros(codigo, nombre_puesto, nombre_empresa)) {
            resultado.add(new PuestoBusquedaDTO(puesto.getId_puesto(), puesto.getNombre(),
                    puesto.getEmpresa().getNombre(), cuestionarioDAO.contar_por_puesto(puesto),
                    cuestionarioDAO.contar_completos_por_puesto(puesto)));
        }
        return resultado;
    }

    /**
     * Paso 6: evaluaciones del puesto, para que el actor seleccione sobre cuáles emitir el orden de mérito.
     */
    public List<Evaluacion> obtener_evaluaciones(int id_puesto) {
        return evaluacionDAO.obtener_por_puesto(obtenerPuesto(id_puesto));
    }

    /**
     * Paso 7: emite el orden de mérito del puesto para las evaluaciones seleccionadas y lo deja registrado.
     */
    public List<OrdenMeritoCandidatoDTO> generar(int id_puesto, List<Integer> ids_evaluacion, Consultor consultor) {
        return Transaccion.ejecutar(() -> {
            Puesto puesto = obtenerPuesto(id_puesto);
            List<Evaluacion> evaluaciones = new ArrayList<>();
            for (Evaluacion evaluacion : evaluacionDAO.obtener_por_puesto(puesto)) {
                if (ids_evaluacion == null || ids_evaluacion.isEmpty()
                        || ids_evaluacion.contains(evaluacion.getId_evaluacion())) {
                    evaluaciones.add(evaluacion);
                }
            }
            if (evaluaciones.isEmpty()) {
                throw new ValidacionException("El puesto no posee evaluaciones.");
            }

            OrdenMerito ordenMerito = new OrdenMerito();
            ordenMerito.setPuesto(puesto);
            ordenMerito.setEvaluaciones(evaluaciones);
            ordenMerito.setConsultor(consultor);
            ordenMerito.setFecha_generacion(LocalDate.now());

            for (Cuestionario cuestionario : cuestionarioDAO.obtener_por_evaluaciones(evaluaciones)) {
                // el puntaje obtenido no se informa si el cuestionario no está completo
                Double puntaje = cuestionario.getEstado_cuestionario() == Estado.COMPLETO
                        ? cuestionario.obtener_puntaje_obtenido() : null;
                ordenMerito.agregar_renglon(cuestionario.getCandidato(), cuestionario, puntaje);
            }
            ordenMerito.generar();
            ordenMeritoDAO.guardar(ordenMerito);

            List<OrdenMeritoCandidatoDTO> renglones = new ArrayList<>();
            for (Renglon_Orden_Merito renglon : ordenMerito.getRenglones()) {
                Candidato candidato = renglon.getCandidato();
                Cuestionario cuestionario = renglon.getCuestionario();
                renglones.add(new OrdenMeritoCandidatoDTO(renglon.getPosicion(), candidato.getTipo_doc().name(),
                        candidato.getNro_doc(), candidato.getApellido(), candidato.getNombre(),
                        renglon.getPuntaje_obtenido(), cuestionario.getEstado_cuestionario().name(),
                        cuestionario.getFecha_ini(), cuestionario.getFecha_fin(),
                        cuestionario.getUltimo_ingreso(), cuestionario.getCant_accesos()));
            }
            return renglones;
        });
    }

    private Puesto obtenerPuesto(int id_puesto) {
        Puesto puesto = puestoDAO.obtener_por_id(id_puesto);
        if (puesto == null) {
            throw new ValidacionException("El puesto seleccionado no existe.");
        }
        return puesto;
    }
}
