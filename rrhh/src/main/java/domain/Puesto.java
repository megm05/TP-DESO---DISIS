package domain;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import java.util.Iterator; 



public class Puesto {
    @Getter  
    @Setter
    private UUID id;
    private String nombre;
    private String descripcion;
    private Empresa empresa_asociada;
    private List<Caracteristica_Del_Puesto> Caracteristicas;
    
    public boolean evaluable(){
        Iterator<Caracteristica_Del_Puesto> iterador = Caracteristicas.iterator();
        while(iterador.hasNext()){
            Caracteristica_Del_Puesto caracteristica = iterador.next();
            if(!caracteristica.getCompetencia().factores_Evaluables()){
                return false; 
            }
        }
        return true; 
        //USAR PATRON ITERADOR; POR CADA COMPETENCIA, VER SI LOS FACTORES 
        // ASOCIADOS TIENEN AL MENOS 2 PREGUNTAS USANDO factores_Evaluables().
    }
}