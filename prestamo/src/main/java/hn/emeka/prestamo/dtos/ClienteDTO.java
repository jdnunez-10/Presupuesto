package hn.emeka.prestamo.dtos;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    private String dni;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;
    private double sueldo;

    private DireccionDTO direccionDTO;

    private Set<PrestamosDTO> prestamosDTO;
}
