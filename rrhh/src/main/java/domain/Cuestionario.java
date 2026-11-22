package domain;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter  
@Setter
public class Cuestionario {
    private UUID id;
    private Estado estado;
    private Date fecha_ini;
    private Date ultimo_ingreso;
    private Date fecha_cambio_estado;
    private Date fecha_fin;
    private String nombre;
    private String clave;
    private float tiempoMax;
    private int cant_accesos;

    private List<Bloque_Pregunta> Bloques_Pregunta_parte;
    //FALTA IMPLEMENTAR ESTOS MÉTODOS

    public void calcular_puntaje(){
 
    }
    public void activar(){
 
    }
    public boolean vencio_plazo_completado(){
        return false;
    }
    public boolean vencio_plazo_activacion(){
        return false;
    }
    public void marcar_incompleto(){
 
    }
    public void marcar_sin_contestar(){
 
    }
    public String disponibilidad(){
        return null;
    }
}
/*
-id_cuestionario:int
-estado_cuestionario: Estado
-fecha_ini: datetime
-datetime fecha_fin
-clave_cuestionario: string
-cant_accesos: int
-ultimo_ingreso: datetime
-tiempoMax: float
-fecha_cambio_estado: datetime
*/ 