package hn.emeka.prestamo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hn.emeka.prestamo.models.IdTablaAmortizacion;
import hn.emeka.prestamo.models.TablaAmortizacion;

public interface TablaAmortizacionRepository extends JpaRepository<TablaAmortizacion, IdTablaAmortizacion>{
    
}
