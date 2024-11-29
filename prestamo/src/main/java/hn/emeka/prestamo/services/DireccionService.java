package hn.emeka.prestamo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hn.emeka.prestamo.dtos.ClienteDTO;
import hn.emeka.prestamo.dtos.DireccionDTO;
import hn.emeka.prestamo.models.Cliente;
import hn.emeka.prestamo.models.Direccion;
import hn.emeka.prestamo.repository.ClienteRepository;
import hn.emeka.prestamo.repository.DireccionRepository;

@Service
public class DireccionService {

    @Autowired
    private DireccionRepository direccionRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    private static ModelMapper modelMapper;

    private ModelMapper getModelMapper() {
        if (modelMapper == null) {
            modelMapper = new ModelMapper();
        }
        return modelMapper;
    }

    public DireccionService() {
        getModelMapper();
    }

    public List<DireccionDTO> obtenerDirecciones() {
        try {
            // Obtener todas las direcciones desde el repositorio
            List<Direccion> direcciones = this.direccionRepository.findAll();

            // Convertir cada Dirección a DirecciónDTO y asignar el ClienteDTO si existe
            List<DireccionDTO> direccionesDTO = direcciones.stream()
                    .map(direccion -> {
                        // Mapear la dirección a DirecciónDTO
                        DireccionDTO direccionDTO = modelMapper.map(direccion, DireccionDTO.class);

                        // Si la dirección tiene un cliente asociado, mapearlo a ClienteDTO y asignarlo
                        if (direccion.getCliente() != null) {
                            ClienteDTO clienteDTO = modelMapper.map(direccion.getCliente(), ClienteDTO.class);
                            direccionDTO.setClienteDTO(clienteDTO); // Establecer el clienteDTO
                        }

                        return direccionDTO;
                    })
                    .collect(Collectors.toList()); // Recoger los resultados en una lista

            // Devolver la lista de DireccionDTOs con el ClienteDTO incluido
            return direccionesDTO;
        } catch (Exception e) {
            // En caso de error, devolver una lista vacía
            return new ArrayList<>();
        }
    }

    public String crearDireccion(DireccionDTO direccionDTO) {
        try {
            // Convertir el DTO a la entidad Direccion
            Direccion direccion = modelMapper.map(direccionDTO, Direccion.class);

            // Verificar si el cliente asociado a la dirección existe
            Cliente cliente = direccion.getCliente(); // Obtener el cliente asociado
            if (cliente == null || cliente.getDni() == null) {
                return "Error: El cliente asociado a la dirección no es válido.";
            }

            // Verificar si el cliente existe en la base de datos
            Optional<Cliente> clienteExistente = this.clienteRepository.findById(cliente.getDni());
            if (!clienteExistente.isPresent()) {
                return "Error: El cliente no existe en el sistema.";
            }

            // Verificar si el cliente ya tiene una dirección registrada
            Optional<Direccion> direccionExistente = this.direccionRepository.findByCliente(clienteExistente.get());
            if (direccionExistente.isPresent()) {
                return "El cliente ya tiene una dirección registrada.";
            }

            // Si el cliente existe y no tiene dirección, guardar la nueva dirección
            this.direccionRepository.save(direccion);
            return "Se ha guardado la dirección al usuario.";
        } catch (Exception e) {
            return "Ha ocurrido un error: " + e.getMessage();
        }
    }

}
