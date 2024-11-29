package hn.emeka.prestamo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hn.emeka.prestamo.models.Cliente;

@Repository
public interface  ClienteRepository extends JpaRepository<Cliente, String>{
    Cliente findByDni(String dni);
}
