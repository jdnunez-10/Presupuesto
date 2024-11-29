package hn.emeka.prestamo.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hn.emeka.prestamo.dtos.ClienteDTO;
import hn.emeka.prestamo.dtos.DireccionDTO;
import hn.emeka.prestamo.dtos.PrestamosDTO;
import hn.emeka.prestamo.dtos.TablaAmortizacionDTO;
import hn.emeka.prestamo.models.Cliente;
import hn.emeka.prestamo.models.Direccion;
import hn.emeka.prestamo.models.Prestamos;
import hn.emeka.prestamo.repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    private ModelMapper modelMapper;

    private ModelMapper getModelMapper() {
        if (modelMapper == null) {
            modelMapper = new ModelMapper();
        }
        return modelMapper;
    }

    public ClienteService() {
        getModelMapper();
    }
    public Optional<ClienteDTO> buscarClientePorId(String dni) {
        try {
            Optional<Cliente> clienteBD = this.clienteRepository.findById(dni);
    
            if (clienteBD.isPresent()) {
                // Mapear la entidad Cliente a ClienteDTO
                ClienteDTO clienteDTO = this.modelMapper.map(clienteBD.get(), ClienteDTO.class);
                DireccionDTO direccionDTO = this.modelMapper.map(clienteBD.get().getDireccion(), DireccionDTO.class);
                clienteDTO.setDireccionDTO(direccionDTO);
                Set<PrestamosDTO> prestamosDTO = clienteBD.get().getPrestamos()
                .stream()
                .map(prestamo -> modelMapper.map(prestamo, PrestamosDTO.class))
                .collect(Collectors.toSet());
            clienteDTO.setPrestamosDTO(prestamosDTO);
                
                return Optional.of(clienteDTO); // Devolver el DTO envuelto en un Optional
            } else {
                return Optional.empty(); // Si no se encuentra el cliente, devolver un Optional vacío
            }
        } catch (Exception e) {
            
            
            return Optional.empty(); // Devolver un Optional vacío en caso de error
        }
    }
    

    public List<ClienteDTO> obtenerClientes() {
        try {
            // Obtener todos los clientes de la base de datos
            List<Cliente> clientes = this.clienteRepository.findAll();
    
            // Mapear cada cliente a ClienteDTO y agregar los préstamos
            List<ClienteDTO> clientesDTO = clientes.stream()
                    .map(cliente -> {
                        // Mapeo básico de Cliente a ClienteDTO
                        ClienteDTO clienteDTO = this.modelMapper.map(cliente, ClienteDTO.class);
    
                        // Mapear la dirección si existe
                        if (cliente.getDireccion() != null) {
                            DireccionDTO direccionDTO = this.modelMapper.map(cliente.getDireccion(), DireccionDTO.class);
                            clienteDTO.setDireccionDTO(direccionDTO); // Establecer la dirección en el DTO
                        }
    
                        // Mapear los préstamos si existen
                        if (cliente.getPrestamos() != null && !cliente.getPrestamos().isEmpty()) {
                            Set<PrestamosDTO> prestamosDTO = cliente.getPrestamos().stream()
                                    .map(prestamo -> {
                                        // Mapeo de cada préstamo a PrestamosDTO
                                        PrestamosDTO prestamoDTO = this.modelMapper.map(prestamo, PrestamosDTO.class);
    
                                        // Mapear la tabla de amortización relacionada al préstamo
                                        if (prestamo.getTablaAmortizaciones() != null && !prestamo.getTablaAmortizaciones().isEmpty()) {
                                            List<TablaAmortizacionDTO> tablaAmortizacionesDTO = prestamo.getTablaAmortizaciones().stream()
                                                    .map(tablaAmortizacion -> this.modelMapper.map(tablaAmortizacion, TablaAmortizacionDTO.class))
                                                    .collect(Collectors.toList());
    
                                            // Establecer las tablas de amortización en el DTO del préstamo
                                            prestamoDTO.setTablaAmortizacionesDTO(tablaAmortizacionesDTO);
                                        }
    
                                        return prestamoDTO; // Devolver el PrestamosDTO con su tabla de amortización
                                    })
                                    .collect(Collectors.toSet());
    
                            // Establecer los préstamos con sus tablas de amortización en el DTO del cliente
                            clienteDTO.setPrestamosDTO(prestamosDTO);
                        }
    
                        return clienteDTO;
                    })
                    .collect(Collectors.toList());
    
            return clientesDTO;
        } catch (Exception e) {
            return new ArrayList<>(); // Retornar una lista vacía en caso de error
        }
    }
    
    

    public String crearCliente(ClienteDTO clienteDTO) {
        try {
            // Mapear ClienteDTO a Cliente usando ModelMapper
            Cliente cliente = this.modelMapper.map(clienteDTO, Cliente.class);

            // Configurar relación bidireccional con Dirección
            Direccion direccion = cliente.getDireccion();
            if (direccion != null) {
                direccion.setCliente(cliente);
            }

            // Configurar relaciones bidireccionales con Préstamos
            Set<Prestamos> prestamos = cliente.getPrestamos();
            if (prestamos != null) {
                prestamos.forEach(prestamo -> {
                    // Inicializar colección de clientes en cada préstamo si es null
                    if (prestamo.getClientes() == null) {
                        prestamo.setClientes(new HashSet<>());
                    }
                    prestamo.getClientes().add(cliente);
                });
            }

            // Verificar si el cliente ya existe
            Optional<Cliente> clienteExistente = clienteRepository.findById(cliente.getDni());
            if (clienteExistente.isPresent()) {
                return "Error: El cliente con DNI " + cliente.getDni() + " ya existe en la base de datos.";
            }

            // Guardar cliente con relaciones
            clienteRepository.save(cliente);

            return "El cliente ha sido registrado exitosamente en el sistema.";
        } catch (Exception e) {
            return "Ha ocurrido un error al crear el cliente: " + e.getMessage();
        }
    }

}
