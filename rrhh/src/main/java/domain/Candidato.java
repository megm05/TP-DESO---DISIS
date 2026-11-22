package domain;
import java.util.UUID;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter  
public class Candidato {
    private UUID id;
    private String nombre;
    private String apellido;
    private int num_candidato;
    private Tipo_doc tipo_doc;
    private String nro_doc;
    private String email;
    private char genero;
    private java.util.Date fecha_nac;
    private String escolaridad;

    public boolean candidato_activo() {
        // Que era este método?
        return true;
    }

    public void gestionar_preguntas() {
        // Metodo para obtener su cuestionario?
    }

}
