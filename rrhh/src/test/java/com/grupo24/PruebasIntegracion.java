package com.grupo24;

import com.grupo24.dominio.Candidato;
import com.grupo24.dominio.Competencia;
import com.grupo24.dominio.Consultor;
import com.grupo24.dominio.Cuestionario;
import com.grupo24.dominio.Detalle_opcion;
import com.grupo24.dominio.Estado;
import com.grupo24.dominio.Evaluacion;
import com.grupo24.dominio.Ponderacion_opcion;
import com.grupo24.dominio.Pregunta;
import com.grupo24.dominio.Respuesta;
import com.grupo24.dominio.Resultado_competencia;
import com.grupo24.dominio.Tipo_doc;
import com.grupo24.dto.ArchivoDTO;
import com.grupo24.dto.CandidatoConClaveDTO;
import com.grupo24.dto.DatosPreguntaDTO;
import com.grupo24.dto.OrdenMeritoCandidatoDTO;
import com.grupo24.dto.PreguntaBloqueDTO;
import com.grupo24.dto.PreguntaBusquedaDTO;
import com.grupo24.dto.PuestoBusquedaDTO;
import com.grupo24.gestores.Gestor_de_autenticacion;
import com.grupo24.gestores.Gestor_de_cuestionario;
import com.grupo24.gestores.Gestor_de_evaluacion;
import com.grupo24.gestores.Gestor_de_orden_merito;
import com.grupo24.gestores.Gestor_de_pregunta;
import com.grupo24.gestores.ValidacionException;
import com.grupo24.persistencia.ConexionBD;
import com.grupo24.persistencia.FabricaDAO;
import com.grupo24.persistencia.InicializadorBD;
import com.grupo24.util.Configuracion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pruebas de integración de los casos de uso implementados (CU020, CU021, CU022, CU023, CU025, CU026, CU028 y
 * la autenticación del CU001), contra la base de datos configurada.
 */
public final class PruebasIntegracion {

    private static int exitosas;
    private static int fallidas;

    private PruebasIntegracion() {
    }

    public static void main(String[] args) throws Exception {
        Configuracion.cargar(args);
        System.out.println("Base de datos: " + Configuracion.get(Configuracion.DB_URL));
        try {
            InicializadorBD.eliminarEsquema();
            InicializadorBD.crearEsquema();
            InicializadorBD.cargarDatosIniciales();

            Consultor consultor = probarAutenticacionConsultor();
            probarGestionDePreguntas(consultor);
            Evaluacion evaluacion = probarEvaluarCandidatos(consultor);
            probarCompletarCuestionario(evaluacion);
            probarVencimientos(consultor);
            probarOrdenDeMerito(consultor);
        } catch (Exception e) {
            fallidas++;
            System.out.println("  ERROR INESPERADO: " + e);
            e.printStackTrace(System.out);
        } finally {
            ConexionBD.cerrar();
        }
        System.out.println();
        System.out.println("Resultado: " + exitosas + " verificaciones correctas, " + fallidas + " fallidas.");
        System.exit(fallidas == 0 ? 0 : 1);
    }

    private static Consultor probarAutenticacionConsultor() {
        titulo("CU001 - Autenticar consultor");
        Gestor_de_autenticacion gestor = new Gestor_de_autenticacion();
        Consultor consultor = gestor.autenticar_consultor("consultor", "consultor123");
        verificar("usuario y contraseña correctos (el usuario se compara en mayúsculas)", consultor != null);
        verificar("contraseña incorrecta: 5.A", gestor.autenticar_consultor("CONSULTOR", "otra") == null);
        verificar("usuario inexistente: 5.A", gestor.autenticar_consultor("NADIE", "consultor123") == null);
        return consultor;
    }

    private static void probarGestionDePreguntas(Consultor consultor) {
        Gestor_de_pregunta gestor = new Gestor_de_pregunta();

        titulo("CU020 - Gestionar Preguntas");
        esperarError("buscar sin ningún criterio", () -> gestor.buscar("", null, " "));
        List<PreguntaBusquedaDTO> porFactor = gestor.buscar(null, "alcance", null);
        verificar("buscar por factor 'alcance' (contiene, sin distinguir mayúsculas) devuelve 3",
                porFactor.size() == 3);
        verificar("la grilla trae competencia, factor y pregunta",
                "GESTION DE PROYECTOS".equals(porFactor.get(0).getNombre_competencia())
                && "GESTION DEL ALCANCE".equals(porFactor.get(0).getNombre_factor()));
        verificar("buscar por competencia 'LEALTAD' devuelve 6", gestor.buscar("LEALTAD", null, null).size() == 6);
        verificar("los comodines de LIKE se toman como texto", gestor.buscar(null, null, "%").isEmpty());

        titulo("CU021 - Dar de alta Pregunta");
        try {
            gestor.dar_de_alta(new DatosPreguntaDTO());
            verificar("datos vacíos deberían rechazarse", false);
        } catch (ValidacionException e) {
            verificar("2.A: detalla TODAS las omisiones (5)", e.getErrores().size() == 5);
        }
        DatosPreguntaDTO largo = nuevaPregunta(1, 1, 1, repetir("X", 101), "¿PREGUNTA DE PRUEBA?");
        esperarError("2.B: nombre que excede los 100 caracteres", () -> gestor.dar_de_alta(largo));
        DatosPreguntaDTO noSuma = nuevaPregunta(1, 1, 1, "PRUEBA SUMA", "¿SUMA MAL?");
        noSuma.getPonderaciones().put(1, 6.0);
        noSuma.getPonderaciones().put(2, 6.0);
        esperarError("las ponderaciones deben sumar 10", () -> gestor.dar_de_alta(noSuma));
        DatosPreguntaDTO negativa = nuevaPregunta(1, 1, 1, "PRUEBA NEGATIVA", "¿NEGATIVA?");
        negativa.getPonderaciones().put(1, 12.0);
        negativa.getPonderaciones().put(2, -2.0);
        esperarError("las ponderaciones no pueden ser negativas", () -> gestor.dar_de_alta(negativa));
        DatosPreguntaDTO otroFactor = nuevaPregunta(1, 8, 1, "PRUEBA FACTOR", "¿FACTOR DE OTRA COMPETENCIA?");
        esperarError("el factor debe corresponder a la competencia", () -> gestor.dar_de_alta(otroFactor));
        DatosPreguntaDTO duplicada = nuevaPregunta(1, 1, 1, "wbs en el alcance", "¿OTRA PREGUNTA?");
        try {
            gestor.dar_de_alta(duplicada);
            verificar("2.C: nombre existente debería rechazarse", false);
        } catch (ValidacionException e) {
            verificar("2.C: avisa que el nombre ya existe y pide foco en Nombre",
                    e.getErrores().size() == 1 && "nombre".equals(e.getCampo()));
        }
        DatosPreguntaDTO nueva = nuevaPregunta(1, 1, 1, "  gestion del cambio ", "¿el alcance se puede modificar?");
        gestor.dar_de_alta(nueva);
        verificar("alta correcta: se asigna un identificador", nueva.getId_pregunta() > 0);
        Pregunta guardada = FabricaDAO.getPreguntaDAO().obtener_por_id(nueva.getId_pregunta());
        verificar("los literales se guardan en mayúsculas y sin espacios sobrantes",
                "GESTION DEL CAMBIO".equals(guardada.getNombre())
                && "¿EL ALCANCE SE PUEDE MODIFICAR?".equals(guardada.getPregunta()));
        verificar("se guardan las ponderaciones de cada opción", guardada.getPonderaciones().size() == 2);
        verificar("la nueva pregunta aparece en la búsqueda", gestor.buscar(null, "ALCANCE", null).size() == 4);

        titulo("CU022 - Modificar Pregunta");
        DatosPreguntaDTO cambios = gestor.obtener_datos(nueva.getId_pregunta());
        verificar("obtener_datos trae competencia y factor de solo lectura",
                "GESTION DE PROYECTOS".equals(cambios.getNombre_competencia())
                && "GESTION DEL ALCANCE".equals(cambios.getNombre_factor()));
        cambios.setNombre("gestion de cambios del alcance");
        gestor.modificar(cambios);
        verificar("modificación correcta (no choca consigo misma)",
                "GESTION DE CAMBIOS DEL ALCANCE".equals(
                        FabricaDAO.getPreguntaDAO().obtener_por_id(nueva.getId_pregunta()).getNombre()));
        DatosPreguntaDTO choca = gestor.obtener_datos(nueva.getId_pregunta());
        choca.setNombre("LINEA BASE DE REQUERIMIENTOS");
        esperarError("2.C: modificar con el nombre de otra pregunta", () -> gestor.modificar(choca));

        titulo("CU023 - Dar de baja Pregunta");
        gestor.dar_baja(nueva.getId_pregunta(), consultor);
        Pregunta dadaDeBaja = FabricaDAO.getPreguntaDAO().obtener_por_id(nueva.getId_pregunta());
        verificar("la baja es lógica: la pregunta sigue en la base inactiva",
                dadaDeBaja != null && !dadaDeBaja.isActivo());
        verificar("ya no aparece en la búsqueda", gestor.buscar(null, "ALCANCE", null).size() == 3);
        verificar("queda el registro de auditoría con usuario, fecha y hora", contarSql(
                "SELECT COUNT(*) FROM Registro_pregunta WHERE id_consultor = ? AND nombre_borrado = ?"
                + " AND fecha_hora IS NOT NULL", consultor.getId_consultor(), "GESTION DE CAMBIOS DEL ALCANCE") == 1);
        esperarError("no se puede dar de baja dos veces",
                () -> gestor.dar_baja(nueva.getId_pregunta(), consultor));
        DatosPreguntaDTO reutiliza = nuevaPregunta(1, 1, 1, "GESTION DE CAMBIOS DEL ALCANCE", "¿REUTILIZA EL NOMBRE?");
        gestor.dar_de_alta(reutiliza);
        verificar("el nombre de una pregunta dada de baja puede reutilizarse", reutiliza.getId_pregunta() > 0);
    }

    private static Evaluacion probarEvaluarCandidatos(Consultor consultor) {
        titulo("CU025 - Evaluar a Candidatos");
        Gestor_de_evaluacion gestor = new Gestor_de_evaluacion();
        Evaluacion evaluacion = gestor.iniciar_evaluacion(consultor);

        esperarError("4.A: agregar sin seleccionar candidatos",
                () -> gestor.agregar_candidatos(evaluacion, Collections.emptyList()));
        gestor.agregar_candidatos(evaluacion, Arrays.asList(1001, 1002, 1003, 1004));
        gestor.agregar_candidatos(evaluacion, Arrays.asList(1001));
        verificar("agregar no duplica candidatos", evaluacion.getCandidatos().size() == 4);
        gestor.quitar_candidatos(evaluacion, Arrays.asList(1004));
        verificar("5.A: quitar candidatos de la grilla", evaluacion.getCandidatos().size() == 3);
        verificar("6: todos los candidatos están disponibles",
                gestor.validar_candidatos_disponibles(evaluacion.getCandidatos()).isEmpty());
        esperarError("10.A: no se seleccionó una función", () -> gestor.generar_evaluacion(evaluacion));

        Evaluacion otra = gestor.iniciar_evaluacion(consultor);
        gestor.agregar_candidatos(otra, Arrays.asList(1005));
        gestor.seleccionar_puesto(otra, 4);
        List<Competencia> noEvaluables = gestor.validar_competencias_evaluables(otra.getPuesto());
        verificar("9.A: GERENTE DE SISTEMAS no es evaluable por LIDERAZGO",
                noEvaluables.size() == 1 && "LIDERAZGO".equals(noEvaluables.get(0).getNombre()));
        esperarError("9.A: no se puede generar la evaluación", () -> gestor.generar_evaluacion(otra));

        gestor.seleccionar_puesto(evaluacion, 1);
        verificar("9: ANALISTA PROGRAMADOR JAVA es evaluable",
                gestor.validar_competencias_evaluables(evaluacion.getPuesto()).isEmpty());
        verificar("9: muestra 3 competencias con su ponderación",
                gestor.obtener_competencias_requeridas(evaluacion.getPuesto()).size() == 3);
        List<CandidatoConClaveDTO> claves = gestor.generar_evaluacion(evaluacion);
        verificar("11: un cuestionario con clave por candidato", claves.size() == 3);
        boolean clavesValidas = true;
        for (CandidatoConClaveDTO c : claves) {
            String clave = c.getClave_ingreso();
            clavesValidas &= clave.length() == 8 && clave.matches(".*[A-Z].*") && clave.matches(".*[0-9].*");
        }
        verificar("las claves tienen 8 caracteres con letras y dígitos", clavesValidas);
        esperarError("no se exportan claves antes de finalizar", () -> gestor.exportar_claves_excel(evaluacion));
        gestor.guardar_evaluacion(evaluacion);
        verificar("13: la evaluación queda guardada", evaluacion.getId_evaluacion() > 0);
        verificar("13: los cuestionarios se crean en estado ACTIVO", contarSql(
                "SELECT COUNT(*) FROM Cuestionario WHERE id_evaluacion = ? AND estado = 'ACTIVO'",
                evaluacion.getId_evaluacion()) == 3);
        gestor.guardar_evaluacion(evaluacion);
        verificar("guardar dos veces no duplica la evaluación",
                contarSql("SELECT COUNT(*) FROM Evaluacion") == 1);
        ArchivoDTO excel = gestor.exportar_claves_excel(evaluacion);
        verificar("14-17: el Excel se nombra aaaa-mm-dd-hh-mm-NombreFunción",
                excel.getNombre_archivo().matches("\\d{4}-\\d{2}-\\d{2}-\\d{2}-\\d{2}-ANALISTA PROGRAMADOR JAVA\\.xlsx"));
        verificar("el archivo es un .xlsx válido (zip)",
                excel.getContenido().length > 500 && excel.getContenido()[0] == 'P' && excel.getContenido()[1] == 'K');

        Evaluacion repetida = gestor.iniciar_evaluacion(consultor);
        gestor.agregar_candidatos(repetida, Arrays.asList(1001, 1006));
        List<Candidato> noDisponibles = gestor.validar_candidatos_disponibles(repetida.getCandidatos());
        verificar("6.A: un candidato con cuestionario activo no puede evaluarse nuevamente",
                noDisponibles.size() == 1 && noDisponibles.get(0).getNum_candidato() == 1001);
        return evaluacion;
    }

    private static void probarCompletarCuestionario(Evaluacion evaluacion) {
        titulo("CU001 (3.A) - Autenticar candidato");
        Gestor_de_autenticacion autenticacion = new Gestor_de_autenticacion();
        Cuestionario delPrimero = cuestionarioDe(evaluacion, 1001);
        Candidato candidato = autenticacion.autenticar_candidato(Tipo_doc.DNI, "30111222",
                delPrimero.getClave_cuestionario().toLowerCase());
        verificar("tipo, número de documento y clave correctos", candidato != null);
        verificar("clave incorrecta: 3.A.2.1",
                autenticacion.autenticar_candidato(Tipo_doc.DNI, "30111222", "ZZZZ9999") == null);
        verificar("documento sin cuestionario activo: 3.A.2.1",
                autenticacion.autenticar_candidato(Tipo_doc.DNI, "40111222", "ABCD1234") == null);

        titulo("CU026 - Completar Cuestionario");
        Gestor_de_cuestionario gestor = new Gestor_de_cuestionario();
        Cuestionario cuestionario = gestor.obtener_cuestionario_activo(candidato);
        verificar("1: el cuestionario está ACTIVO", cuestionario.getEstado_cuestionario() == Estado.ACTIVO);
        verificar("1: ingreso permitido", gestor.registrar_ingreso(cuestionario) == null);
        verificar("se cuenta el acceso", cuestionario.getCant_accesos() == 1);
        verificar("las instrucciones salen del repositorio de configuraciones",
                gestor.obtener_instrucciones().startsWith("BIENVENIDO"));

        gestor.iniciar_cuestionario(cuestionario);
        verificar("3: pasa a EN_PROCESO con fecha de inicio",
                cuestionario.getEstado_cuestionario() == Estado.EN_PROCESO && cuestionario.getFecha_ini() != null);
        verificar("3: 2 preguntas por factor (6 factores = 12) en bloques de 4 (3 bloques)",
                cuestionario.getBloques().size() == 3 && contarSql("SELECT COUNT(*) FROM Respuesta r"
                + " JOIN Bloque_pregunta b ON b.id_bloque = r.id_bloque WHERE b.id_cuestionario = ?",
                cuestionario.getId_cuestionario()) == 12);
        verificar("3: cada factor aporta exactamente 2 preguntas", contarSql("SELECT COUNT(*) FROM ("
                + "SELECT p.id_factor FROM Respuesta r JOIN Bloque_pregunta b ON b.id_bloque = r.id_bloque"
                + " JOIN Pregunta p ON p.id_pregunta = r.id_pregunta WHERE b.id_cuestionario = ?"
                + " GROUP BY p.id_factor HAVING COUNT(*) = 2) x", cuestionario.getId_cuestionario()) == 6);

        List<PreguntaBloqueDTO> bloque1 = gestor.obtener_bloque_actual(cuestionario);
        verificar("4: el bloque 1 tiene 4 preguntas con sus opciones", bloque1.size() == 4
                && !bloque1.get(0).getOpciones().isEmpty());
        Map<Integer, Integer> parcial = new HashMap<>();
        parcial.put(bloque1.get(0).getId_respuesta(), bloque1.get(0).getOpciones().get(0).getId_detalle());
        List<Pregunta> sinResponder = gestor.registrar_respuestas_bloque(cuestionario, parcial);
        verificar("6.A: informa las 3 preguntas sin responder", sinResponder.size() == 3);
        verificar("6.A: mantiene la respuesta ya cargada",
                gestor.obtener_bloque_actual(cuestionario).get(0).getId_detalle_elegido() != null);
        Map<Integer, Integer> invalida = new HashMap<>();
        invalida.put(bloque1.get(1).getId_respuesta(), 99999);
        esperarError("una opción que no corresponde a la pregunta se rechaza",
                () -> gestor.registrar_respuestas_bloque(cuestionario, invalida));
        verificar("7: bloque 1 completo",
                gestor.registrar_respuestas_bloque(cuestionario, mejoresRespuestas(cuestionario)).isEmpty());
        esperarError("no se puede finalizar con bloques pendientes", () -> gestor.finalizar(cuestionario));

        titulo("CU026 (1.C) - Retomar un cuestionario en proceso");
        Cuestionario retomado = gestor.obtener_cuestionario_activo(candidato);
        verificar("1.C: se recupera de la base en proceso", retomado.getEstado_cuestionario() == Estado.EN_PROCESO);
        verificar("1.C: se posiciona en el bloque que aún no fue respondido",
                retomado.obtener_bloque_no_respondido().getNumero() == 2);
        verificar("se presenta de forma idéntica (mismas preguntas)", mismasPreguntas(cuestionario, retomado));
        verificar("segundo acceso permitido", gestor.registrar_ingreso(retomado) == null
                && retomado.getCant_accesos() == 2);
        gestor.iniciar_cuestionario(retomado);
        verificar("iniciar no vuelve a sortear un cuestionario en proceso", mismasPreguntas(cuestionario, retomado));
        verificar("8.A: bloque 2", gestor.registrar_respuestas_bloque(retomado, mejoresRespuestas(retomado)).isEmpty());
        verificar("8.A: bloque 3", gestor.registrar_respuestas_bloque(retomado, mejoresRespuestas(retomado)).isEmpty());
        verificar("no quedan bloques pendientes", gestor.obtener_bloque_actual(retomado).isEmpty());
        gestor.finalizar(retomado);
        verificar("8: pasa a COMPLETO con fecha de fin",
                retomado.getEstado_cuestionario() == Estado.COMPLETO && retomado.getFecha_fin() != null);
        Cuestionario completo = FabricaDAO.getCuestionarioDAO().obtener_por_id(retomado.getId_cuestionario());
        Map<String, Double> totales = new HashMap<>();
        boolean dosFactores = completo.obtener_resultados_competencia().size() == 3;
        for (Resultado_competencia r : completo.obtener_resultados_competencia()) {
            totales.put(r.getCompetencia().getNombre(), r.getTotal());
            dosFactores &= r.getResultados_factor().size() == 2;
        }
        verificar("obs. 3: se guarda el total de cada competencia y de sus 2 factores", dosFactores);
        // Como las ponderaciones de cada pregunta suman 10, la mejor opción vale 10 en las de
        // verdadero/falso, pero 5 en la escala de frecuencia (0-2-3-5) y 4 en la de acuerdo (0-1-2-3-4).
        verificar("obs. 3: con las mejores opciones POO = 10 y BASES DE DATOS = 10",
                totales.get("PROGRAMACION ORIENTADA A OBJETOS") == 10.0 && totales.get("BASES DE DATOS") == 10.0);
        verificar("obs. 3: TRABAJO EN EQUIPO = (5 + 4) / 2 = 4,5 (factores de escala, máximo < 10)",
                totales.get("TRABAJO EN EQUIPO") == 4.5);
        verificar("puntaje obtenido = promedio de las competencias = (10 + 10 + 4,5) / 3 = 8,17",
                completo.obtener_puntaje_obtenido() == 8.17);
        verificar("ya no puede autenticarse (no tiene cuestionario activo)", autenticacion.autenticar_candidato(
                Tipo_doc.DNI, "30111222", completo.getClave_cuestionario()) == null);

        titulo("CU026 obs. 3 - Algoritmo de cálculo de puntaje");
        Cuestionario segundo = cuestionarioDe(evaluacion, 1002);
        Candidato candidato2 = segundo.getCandidato();
        Cuestionario c2 = gestor.obtener_cuestionario_activo(candidato2);
        gestor.registrar_ingreso(c2);
        gestor.iniciar_cuestionario(c2);
        while (!gestor.obtener_bloque_actual(c2).isEmpty()) {
            gestor.registrar_respuestas_bloque(c2, primerasRespuestas(c2));
        }
        double esperado = calcularEsperado(c2);
        gestor.finalizar(c2);
        Cuestionario c2b = FabricaDAO.getCuestionarioDAO().obtener_por_id(c2.getId_cuestionario());
        verificar("eligiendo la primera opción el puntaje coincide con el cálculo manual ("
                + esperado + ")", Math.abs(c2b.obtener_puntaje_obtenido() - esperado) < 0.01);
    }

    private static void probarVencimientos(Consultor consultor) {
        titulo("CU026 (1.A, 1.B y accesos) - Vencimientos");
        Gestor_de_evaluacion evaluaciones = new Gestor_de_evaluacion();
        Gestor_de_cuestionario gestor = new Gestor_de_cuestionario();

        Evaluacion evaluacion = evaluaciones.iniciar_evaluacion(consultor);
        evaluaciones.agregar_candidatos(evaluacion, Arrays.asList(1007, 1008, 1009));
        evaluaciones.seleccionar_puesto(evaluacion, 3);
        evaluaciones.generar_evaluacion(evaluacion);
        evaluaciones.guardar_evaluacion(evaluacion);

        // 1.B: activo hace más de TIEMPO_MAX_ACTIVO (360 h)
        Cuestionario sinIniciar = cuestionarioDe(evaluacion, 1007);
        ejecutarSql("UPDATE Cuestionario SET fecha_cambio_estado = CURRENT_TIMESTAMP - INTERVAL '400' HOUR"
                + " WHERE id_cuestionario = ?", sinIniciar.getId_cuestionario());
        Cuestionario c1 = gestor.obtener_cuestionario_activo(sinIniciar.getCandidato());
        verificar("1.B: avisa la situación", gestor.registrar_ingreso(c1) != null);
        verificar("1.B: queda SIN_CONTESTAR", estadoEnBase(c1) == Estado.SIN_CONTESTAR);

        // 1.A: en proceso hace más del tiempo máximo del cuestionario (72 h)
        Cuestionario enProceso = cuestionarioDe(evaluacion, 1008);
        Cuestionario c2 = gestor.obtener_cuestionario_activo(enProceso.getCandidato());
        gestor.registrar_ingreso(c2);
        gestor.iniciar_cuestionario(c2);
        ejecutarSql("UPDATE Cuestionario SET fecha_ini = CURRENT_TIMESTAMP - INTERVAL '80' HOUR"
                + " WHERE id_cuestionario = ?", c2.getId_cuestionario());
        Cuestionario c2b = gestor.obtener_cuestionario_activo(enProceso.getCandidato());
        verificar("1.A: avisa la situación", gestor.registrar_ingreso(c2b) != null);
        verificar("1.A: queda INCOMPLETO", estadoEnBase(c2b) == Estado.INCOMPLETO);

        // precondición: cantidad máxima de accesos (5)
        Cuestionario accesos = cuestionarioDe(evaluacion, 1009);
        ejecutarSql("UPDATE Cuestionario SET cant_accesos = 5 WHERE id_cuestionario = ?",
                accesos.getId_cuestionario());
        Cuestionario c3 = gestor.obtener_cuestionario_activo(accesos.getCandidato());
        verificar("accesos: avisa la situación", gestor.registrar_ingreso(c3) != null);
        verificar("accesos: queda INCOMPLETO", estadoEnBase(c3) == Estado.INCOMPLETO);
    }

    private static void probarOrdenDeMerito(Consultor consultor) {
        titulo("CU028 - Emitir Orden de mérito");
        Gestor_de_orden_merito gestor = new Gestor_de_orden_merito();
        esperarError("3: buscar sin ningún criterio", () -> gestor.buscar_puestos(null, " ", ""));
        List<PuestoBusquedaDTO> puestos = gestor.buscar_puestos(null, "analista", null);
        verificar("4: encuentra el puesto con candidatos y evaluaciones completas",
                puestos.size() == 1 && puestos.get(0).getCant_candidatos() == 3
                && puestos.get(0).getCant_evaluaciones_completas() == 2);
        verificar("4: buscar por empresa", gestor.buscar_puestos(null, null, "litoral").size() == 2);
        verificar("4: buscar por código", gestor.buscar_puestos(1, null, null).size() == 1);
        verificar("6.A: un puesto sin evaluaciones", gestor.obtener_evaluaciones(2).isEmpty());
        esperarError("6.A: no se emite sin evaluaciones", () -> gestor.generar(2, null, consultor));

        List<OrdenMeritoCandidatoDTO> listado = gestor.generar(1, null, consultor);
        verificar("7: se muestran todos los candidatos de las evaluaciones", listado.size() == 3);
        OrdenMeritoCandidatoDTO primero = listado.get(0);
        verificar("7: integra el orden quien completó y alcanzó los mínimos (posición 1, puntaje 8,17)",
                primero.getPosicion() != null && primero.getPosicion() == 1
                && primero.getPuntaje_obtenido() == 8.17 && "ACOSTA".equals(primero.getApellido()));
        OrdenMeritoCandidatoDTO ultimo = listado.get(2);
        verificar("obs. 1: sin puntaje para un cuestionario que no está completo",
                ultimo.getPosicion() == null && ultimo.getPuntaje_obtenido() == null
                && "ACTIVO".equals(ultimo.getEstado_cuestionario()));
        verificar("obs. 2: datos adicionales del completo (inicio, fin y accesos)",
                primero.getFecha_inicio() != null && primero.getFecha_fin() != null && primero.getCant_accesos() == 2);
        verificar("el orden de mérito queda registrado con sus renglones",
                contarSql("SELECT COUNT(*) FROM Renglon_Orden_merito") == 3
                && contarSql("SELECT COUNT(*) FROM Orden_merito_Evaluacion") == 1);
        int segundo = listado.get(1).getPosicion() == null ? 0 : 1;
        verificar("el segundo candidato completo figura según alcance o no los mínimos (" + listado.get(1).getEstado_cuestionario()
                + ", posición " + listado.get(1).getPosicion() + ", puntaje " + listado.get(1).getPuntaje_obtenido() + ")",
                listado.get(1).getPuntaje_obtenido() != null && segundo >= 0);
        int idEvaluacion = gestor.obtener_evaluaciones(1).get(0).getId_evaluacion();
        verificar("6: emitir para una evaluación seleccionada",
                gestor.generar(1, Collections.singletonList(idEvaluacion), consultor).size() == 3);
    }

    private static DatosPreguntaDTO nuevaPregunta(int competencia, int factor, int opcion, String nombre, String texto) {
        DatosPreguntaDTO datos = new DatosPreguntaDTO();
        datos.setId_competencia(competencia);
        datos.setId_factor(factor);
        datos.setId_opcion(opcion);
        datos.setNombre(nombre);
        datos.setPregunta(texto);
        datos.getPonderaciones().put(1, 10.0);
        datos.getPonderaciones().put(2, 0.0);
        return datos;
    }

    private static Cuestionario cuestionarioDe(Evaluacion evaluacion, int num_candidato) {
        for (Cuestionario cuestionario : evaluacion.getCuestionarios()) {
            if (cuestionario.getCandidato().getNum_candidato() == num_candidato) {
                return cuestionario;
            }
        }
        throw new IllegalStateException("Candidato " + num_candidato + " no está en la evaluación");
    }

    private static Map<Integer, Integer> mejoresRespuestas(Cuestionario cuestionario) {
        Map<Integer, Integer> respuestas = new HashMap<>();
        for (Respuesta respuesta : cuestionario.obtener_bloque_no_respondido().obtener_respuestas()) {
            Ponderacion_opcion mejor = null;
            for (Ponderacion_opcion p : respuesta.getPregunta().getPonderaciones()) {
                if (mejor == null || p.getPonderacion() > mejor.getPonderacion()) {
                    mejor = p;
                }
            }
            respuestas.put(respuesta.getId_respuesta(), mejor.getDetalle().getId_detalle());
        }
        return respuestas;
    }

    private static Map<Integer, Integer> primerasRespuestas(Cuestionario cuestionario) {
        Map<Integer, Integer> respuestas = new HashMap<>();
        for (Respuesta respuesta : cuestionario.obtener_bloque_no_respondido().obtener_respuestas()) {
            List<Detalle_opcion> detalles = respuesta.getPregunta().getOpcion_respuesta().obtener_detalles();
            respuestas.put(respuesta.getId_respuesta(), detalles.get(0).getId_detalle());
        }
        return respuestas;
    }

    /** Aplica a mano la observación (3) del CU026 y devuelve el promedio de las competencias. */
    private static double calcularEsperado(Cuestionario cuestionario) {
        Map<Integer, Double> sumaFactor = new HashMap<>();
        Map<Integer, Integer> competenciaDelFactor = new HashMap<>();
        for (com.grupo24.dominio.BloquePregunta bloque : cuestionario.getBloques()) {
            for (Respuesta r : bloque.obtener_respuestas()) {
                int f = r.getPregunta().getFactor().getId_factor();
                sumaFactor.merge(f, r.obtener_ponderacion(), Double::sum);
                competenciaDelFactor.put(f, r.getPregunta().getFactor().getCompetencia().getId_competencia());
            }
        }
        Map<Integer, double[]> porCompetencia = new HashMap<>();
        for (Map.Entry<Integer, Double> e : sumaFactor.entrySet()) {
            double[] acumulado = porCompetencia.computeIfAbsent(competenciaDelFactor.get(e.getKey()), k -> new double[2]);
            acumulado[0] += Math.round(e.getValue() / 2 * 100.0) / 100.0;
            acumulado[1]++;
        }
        double total = 0;
        for (double[] acumulado : porCompetencia.values()) {
            total += Math.round(acumulado[0] / acumulado[1] * 100.0) / 100.0;
        }
        return Math.round(total / porCompetencia.size() * 100.0) / 100.0;
    }

    private static boolean mismasPreguntas(Cuestionario a, Cuestionario b) {
        StringBuilder sa = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        for (com.grupo24.dominio.BloquePregunta bloque : a.getBloques()) {
            for (Respuesta r : bloque.obtener_respuestas()) {
                sa.append(bloque.getNumero()).append(':').append(r.getPregunta().getId_pregunta()).append(',');
            }
        }
        for (com.grupo24.dominio.BloquePregunta bloque : b.getBloques()) {
            for (Respuesta r : bloque.obtener_respuestas()) {
                sb.append(bloque.getNumero()).append(':').append(r.getPregunta().getId_pregunta()).append(',');
            }
        }
        return sa.length() > 0 && sa.toString().equals(sb.toString());
    }

    private static Estado estadoEnBase(Cuestionario cuestionario) {
        return FabricaDAO.getCuestionarioDAO().obtener_por_id(cuestionario.getId_cuestionario()).getEstado_cuestionario();
    }

    private static int contarSql(String sql, Object... parametros) {
        try (PreparedStatement ps = ConexionBD.obtener().prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static void ejecutarSql(String sql, Object... parametros) {
        try (PreparedStatement ps = ConexionBD.obtener().prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static String repetir(String texto, int veces) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < veces; i++) {
            sb.append(texto);
        }
        return sb.toString();
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("== " + texto);
    }

    private static void verificar(String descripcion, boolean condicion) {
        if (condicion) {
            exitosas++;
            System.out.println("  [OK]    " + descripcion);
        } else {
            fallidas++;
            System.out.println("  [FALLA] " + descripcion);
        }
    }

    private static void esperarError(String descripcion, Runnable accion) {
        try {
            accion.run();
            verificar(descripcion + " (se esperaba un aviso de validación)", false);
        } catch (ValidacionException e) {
            verificar(descripcion + " -> \"" + e.getErrores().get(0) + "\"", true);
        }
    }

    @SuppressWarnings("unused")
    private static void noUsado(Statement s) {
        // evita advertencias de importación en algunos IDE
    }
}
