package hn.emeka.prestamo.models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hn.emeka.prestamo.TipoPrestamo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "prestamos")
public class Prestamos {

    @Id
    @Column(name = "idprestamo")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPrestamo;
    @Column(columnDefinition="DECIMAL(14,2)")
    private double monto;
    private int plazo;
    @Column(columnDefinition="DECIMAL(14,2)")
    private double tasaInteres;
    @Column(columnDefinition="DECIMAL(14,2)")
    private double cuota;
    private char estado;
    
    @Column(name = "tipoprestamo")
    private TipoPrestamo tipoPrestamo;


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "cliente_prestamos",
            joinColumns = @JoinColumn(name = "idprestamo", referencedColumnName = "idprestamo"),
            inverseJoinColumns = @JoinColumn(name = "dni", referencedColumnName = "dni")
    )
   private Set<Cliente> clientes = new HashSet<>();


    @OneToMany(mappedBy = "prestamo", cascade = CascadeType.ALL)
    private List<TablaAmortizacion> tablaAmortizaciones;

}
