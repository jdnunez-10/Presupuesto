package hn.emeka.prestamo.models;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tabla_amortizacion")
public class TablaAmortizacion {

    @EmbeddedId
    private IdTablaAmortizacion id; // Clave primaria compuesta

   
    @Column(columnDefinition = "DECIMAL(14,2)")
    private double interes;
    @Column(columnDefinition = "DECIMAL(14,2)")
    private double capital;
    @Column(columnDefinition = "DECIMAL(14,2)")
    private double saldo;
    private char estado;
    @Column(name = "fechavencimiento")
    private LocalDate fechaVencimiento;

    // Usamos insertable=false, updatable=false para evitar la duplicación de la columna idprestamo
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idprestamo", referencedColumnName = "idprestamo", insertable = false, updatable = false)
    private Prestamos prestamo;

}
