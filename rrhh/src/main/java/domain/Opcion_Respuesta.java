package domain;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter  
@Setter
public class Opcion_Respuesta {
    private UUID id;
    private String nombre;
    private String descripcion;
  /*   public boolean obtener_detalles(){
        //que carajos devuelve esto?  
    }*/
}