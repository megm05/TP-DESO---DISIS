package domain;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter  
@Setter
public class Factor {
    private UUID id;
    private String nombre;
    private String descripcion;
    private List<Pregunta> preguntas_Asociadas;
    public boolean preguntas_Suficientes(){
        return (preguntas_Asociadas.size() >= 2); //devolver true si size pregunta_asociadas >=2 ?        
    }
}