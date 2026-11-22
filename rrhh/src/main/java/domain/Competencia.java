package domain;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter  
@Setter
public class Competencia {
    private UUID id;
    private String nombre;
    private String tipo;
    private List<Factor> Factores_Asociados;
    public boolean factores_Evaluables(){
        //por cada factor llamar al metodo preguntas_Suficientes();
        Iterator<Factor> iterador = Factores_Asociados.iterator();
        while(iterador.hasNext()){ //habra que revisar este iterador
            Factor factor = iterador.next();
            if(!factor.preguntas_Suficientes()){
                return false;//quizàs deberia devolver una excepción, lanzando un mensaje de error con el factor que  no tiene suficientes preguntas.
            }
        }
        return true;
    }
}