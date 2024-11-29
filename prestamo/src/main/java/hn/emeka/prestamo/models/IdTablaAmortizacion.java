package hn.emeka.prestamo.models;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class IdTablaAmortizacion implements Serializable{
     @Column(name = "numerocuota")
    private int numeroCuota;

    @Column(name = "idprestamo")
    private int idPrestamo;
}
