package hn.emeka.prestamo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hn.emeka.prestamo.models.Prestamos;

public interface PrestamosRepository extends JpaRepository<Prestamos, Integer>{
       
}
