package domain;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter  
@Setter

public class Pregunta {
    private Factor factor_asociado; //factor que la pregunta evaluae
    private UUID id;
    private String nombre;
    private String descripcion;
    private boolean activo; 
    private List<Opcion_Respuesta> opciones_Asociadas;
}