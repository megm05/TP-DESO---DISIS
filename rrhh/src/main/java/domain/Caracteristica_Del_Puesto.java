package domain;

import lombok.Getter;
import lombok.Setter;


public class Caracteristica_Del_Puesto {
   
    private Puesto puesto;

     @Getter    @Setter  private Competencia competencia;
    private double ponderacion;
}