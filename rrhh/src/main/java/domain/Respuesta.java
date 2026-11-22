package domain;
import java.util.Date;

import lombok.Getter;
import lombok.Setter; 
//esta clase se encarga de asociar las respuestas de los candidatos a los bloques preguntas del cuestionario.
public class Respuesta {
@Getter @Setter     private Date fecha;
@Getter @Setter     private String valor_elegido;


}
