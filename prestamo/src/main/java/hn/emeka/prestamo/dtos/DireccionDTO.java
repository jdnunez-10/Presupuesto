package hn.emeka.prestamo.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DireccionDTO {
       private int idDireccion;

    private String pais;
    private String departamento;
    private String ciudad;

    private String colonia;

    private String referencia;
    
   
    private ClienteDTO clienteDTO;
}
