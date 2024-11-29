package hn.emeka.prestamo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hn.emeka.prestamo.dtos.ClienteDTO;
import hn.emeka.prestamo.dtos.DireccionDTO;
import hn.emeka.prestamo.dtos.PrestamosDTO;
import hn.emeka.prestamo.services.ClienteService;
import hn.emeka.prestamo.services.DireccionService;
import hn.emeka.prestamo.services.PrestamosService;
import io.swagger.v3.oas.annotations.Operation;




@RestController
@RequestMapping("api/v1/prestamos")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;
    @Autowired
    private DireccionService direccionService;
    @Autowired
    private PrestamosService prestamoService;
    @GetMapping("/clientes")
    public List<ClienteDTO> obtenerClientes() {
        return this.clienteService.obtenerClientes();
    }
    @GetMapping("/clientes/{dni}")
    public Optional<ClienteDTO> buscarClientePorId(@PathVariable String dni) {
        return this.clienteService.buscarClientePorId(dni);
    }
    
    @Operation(summary = "Crear una nueva direccion al cliente", description = "Crea una direccion al cliente")
    @PostMapping("/direcciones/crear")
    public String crearDireccion(@RequestBody DireccionDTO direccionDTO) {    
        
        return this.direccionService.crearDireccion(direccionDTO);
    }

    @Operation(summary = "Mostrar direcciones", description = "Obtiene una lista de direcciones")
    @GetMapping("/direcciones")
    public List<DireccionDTO> obtenerDirecciones() {    
        
        return this.direccionService.obtenerDirecciones();
    }
    @Operation(summary = "Crear un nuevo cliente", description = "Crea un nuevo cliente con su dirección y préstamos asociados")
    @PostMapping("/clientes/crear")
    public String crearCliente(@RequestBody ClienteDTO cliente) {
        
        return this.clienteService.crearCliente(cliente);
    }
@Operation(summary="Crear prestamo", description="Crea un prestamo al cliente")
    @PostMapping("/prestamos/crear")
    public String crearPrestamo(@RequestBody PrestamosDTO prestamosDTO) {        
        
        return this.prestamoService.crearPrestamo(prestamosDTO);
    }
    

    @Operation(summary="Asociar prestamo", description="Asocia un prestamo a un cliente")
    @PostMapping("/prestamos/asociar")
    public String asociarPrestamoACliente(@RequestBody PrestamosDTO prestamosDTO ) {        
        
        return this.prestamoService.asociarPrestamoACliente(prestamosDTO);
    }
    
    
    
    
}
