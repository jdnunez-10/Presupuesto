package hn.emeka.prestamo.dtos;

import java.util.List;
import java.util.Set;

import hn.emeka.prestamo.TipoPrestamo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PrestamosDTO {

    private int idPrestamo;
    private double monto;
    private int plazo;
    private double tasaInteres;
    private double cuota;
    private char estado;

    private TipoPrestamo tipoPrestamo;

    private Set<ClienteDTO> clientesDTO;

    private List<TablaAmortizacionDTO> tablaAmortizacionesDTO;
}
