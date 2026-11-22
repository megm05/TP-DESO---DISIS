package domain;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter  
@Setter
public class Bloque_Pregunta { 
    //Esta clase aloja las preguntas vinculadas a un cuestionario. 
    private UUID id;
    private boolean completado = false; 
    private List<Pregunta> preguntas_Parte;
    private Cuestionario cuestionario_Todo;
    public void completar(){ //creo que este metodo es para marcar el bloque como completado. 
        this.completado = true; 
    }
}