package hn.emeka.prestamo.models;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    private String dni;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;
    @Column(columnDefinition="DECIMAL(14,2)")
    private double sueldo;
    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
    private Direccion direccion;
    @ManyToMany(mappedBy = "clientes", cascade = CascadeType.ALL)
    private Set<Prestamos> prestamos;
}
