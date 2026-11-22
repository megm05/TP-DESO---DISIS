package domain;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter  
@Setter

public class Empresa {
    private UUID id;
    private String nombre;
    private List<Puesto> puestos_Asociados; 
}