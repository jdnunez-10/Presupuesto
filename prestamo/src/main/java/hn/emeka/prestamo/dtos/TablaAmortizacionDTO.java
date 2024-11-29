package hn.emeka.prestamo.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TablaAmortizacionDTO {

    private int numeroCuota;

    private double interes;

    private double capital;

    private double saldo;
    private char estado;

    private LocalDate fechaVencimiento;

    private PrestamosDTO prestamoDTO;
}
