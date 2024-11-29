package hn.emeka.prestamo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import hn.emeka.prestamo.models.Cliente;
import hn.emeka.prestamo.models.Direccion;

public interface DireccionRepository extends  JpaRepository<Direccion, Integer> {

    Optional<Direccion> findByCliente(Cliente cliente);
    
}
