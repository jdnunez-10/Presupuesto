package hn.emeka.prestamo.services;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import hn.emeka.prestamo.TipoPrestamo;
import hn.emeka.prestamo.dtos.PrestamosDTO;
import hn.emeka.prestamo.models.Cliente;
import hn.emeka.prestamo.models.IdTablaAmortizacion;
import hn.emeka.prestamo.models.Prestamos;
import hn.emeka.prestamo.models.TablaAmortizacion;
import hn.emeka.prestamo.repository.ClienteRepository;
import hn.emeka.prestamo.repository.PrestamosRepository;
import hn.emeka.prestamo.repository.TablaAmortizacionRepository;
import jakarta.transaction.Transactional;

@Service
public class PrestamosService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TablaAmortizacionRepository tablaAmortizacionRepository;

    @Autowired
    private PrestamosRepository prestamoRepository;

    private ModelMapper modelMapper;

    private ModelMapper getModelMapper() {
        if (modelMapper == null) {
            modelMapper = new ModelMapper();
        }
        return modelMapper;
    }

    public PrestamosService() {
        getModelMapper();
    }
    @Value("${prestamo.tasa.personal}")
    private double tasaPersonal;

    @Value("${prestamo.tasa.hipotecario}")
    private double tasaHipotecario;

    @Value("${prestamo.tasa.vehicular}")
    private double tasaVehicular;

    @Transactional
    public String crearPrestamo(PrestamosDTO prestamosDTO) {
        // Mapeo el DTO al modelo de entidad Prestamos
        Prestamos prestamo = this.modelMapper.map(prestamosDTO, Prestamos.class);

        // Validar que el plazo sea al menos 1 año
        if (prestamo.getPlazo() < 1) {
            return "El plazo mínimo para un préstamo es de 1 año.";
        }

        // Obtener y establecer la tasa de interés y la cuota
        double tasaDeInteres = obtenerTasaInteres(prestamo.getTipoPrestamo());
        double cuota = obtenerCuota(prestamo.getMonto(), tasaDeInteres, prestamo.getPlazo());
        prestamo.setTasaInteres(tasaDeInteres);
        prestamo.setCuota(cuota);

        // Validar clientes del préstamo
        Set<Cliente> clientesDelDTO = prestamo.getClientes();
        if (clientesDelDTO == null || clientesDelDTO.isEmpty()) {
            return "El préstamo debe tener al menos un cliente.";
        }

        // Reemplazar clientes del DTO por clientes en estado "managed"
        prestamo.setClientes(new HashSet<>()); // Limpia los clientes existentes

        List<String> listaDeDNI = clientesDelDTO.stream()
                .map(Cliente::getDni)
                .collect(Collectors.toList());

        for (String dni : listaDeDNI) {
            Optional<Cliente> clienteABuscar = clienteRepository.findById(dni);
            if (clienteABuscar.isPresent()) {
                Cliente cliente = clienteABuscar.get();

                // Calcular el nivel de endeudamiento
                double totalEgresos = obtenerTotalDeEgresos(cliente);
                double sueldo = cliente.getSueldo();
                double nivelEndeudamiento = totalEgresos / sueldo;

                if (nivelEndeudamiento > 0.40) {
                    return "El nivel de endeudamiento del cliente con DNI " + dni + " es superior al 40%. No se puede crear el préstamo.";
                }

                // Añadir cliente al préstamo si pasa las validaciones
                prestamo.getClientes().add(cliente);
            } else {
                return "Cliente con DNI " + dni + " no encontrado. No se puede crear el préstamo.";
            }
        }

        prestamo.setEstado('P');
// Guardar el préstamo en la base de datos
        this.prestamoRepository.save(prestamo);
        // Crear la tabla de amortización para el préstamo
        crearTablaAmortizacion(prestamo, cuota, tasaDeInteres);

        return "Préstamo y tabla de amortización creados exitosamente.";
    }

    public String crearSoloPrestamo(PrestamosDTO prestamoDTO) {
        Prestamos prestamo = this.modelMapper.map(prestamoDTO, Prestamos.class);
        // Validar que el plazo sea al menos 1 año
        if (prestamo.getPlazo() < 1) {
            return "El plazo mínimo para un préstamo es de 1 año.";
        }

        // Obtener y establecer la tasa de interés y la cuota
        double tasaDeInteres = obtenerTasaInteres(prestamo.getTipoPrestamo());
        double cuota = obtenerCuota(prestamo.getMonto(), tasaDeInteres, prestamo.getPlazo());
        prestamo.setTasaInteres(tasaDeInteres);
        prestamo.setCuota(cuota);
        this.prestamoRepository.save(prestamo);

        return "Prestamo creado exitosamente";
    }

    public String asociarPrestamoACliente(PrestamosDTO prestamoDTO) {
        // Mapeo el DTO al modelo de entidad Prestamos
        Prestamos prestamo = this.modelMapper.map(prestamoDTO, Prestamos.class);
        Optional< Prestamos> prestamoExistente = this.prestamoRepository.findById(prestamo.getIdPrestamo());
        if (prestamoExistente.isPresent()) {
            prestamo = prestamoExistente.get();
        } else {
            return "El prestamo no existe o esta vacio";
        }

        // Validar clientes del préstamo
        Set<Cliente> clientes = prestamo.getClientes();
        if (clientes == null || clientes.isEmpty()) {
            return "El préstamo debe tener al menos un cliente.";
        }

        List<String> listaDeDNI = clientes.stream()
                .map(Cliente::getDni)
                .collect(Collectors.toList());

        for (String dni : listaDeDNI) {
            Optional<Cliente> clienteABuscar = clienteRepository.findById(dni);
            if (clienteABuscar.isPresent()) {
                Cliente cliente = clienteABuscar.get();

                // Calcular el nivel de endeudamiento
                double totalEgresos = obtenerTotalDeEgresos(cliente);
                double sueldo = cliente.getSueldo();
                double nivelEndeudamiento = totalEgresos / sueldo;

                if (nivelEndeudamiento > 0.40) {
                    return "El nivel de endeudamiento del cliente con DNI " + dni + " es superior al 40%. No se puede crear el préstamo.";
                }

                // Añadir cliente al préstamo si pasa las validaciones
                prestamo.getClientes().add(cliente);
            } else {
                return "Cliente con DNI " + dni + " no encontrado. No se puede crear el préstamo.";
            }
        }

        prestamo.setEstado('P');
        // Guardar el préstamo en la base de datos
        this.prestamoRepository.save(prestamo);
        // Crear la tabla de amortización para el préstamo
        crearTablaAmortizacion(prestamo, prestamo.getCuota(), prestamo.getTasaInteres());

        return "Se asociozó correctamente el prestamo N: ";
    }

    /**
     * Obtiene la cuota
     *
     * @param monto
     * @param tasaDeInteres
     * @param plazo
     * @return cuota
     */
    private double obtenerCuota(double monto, double tasaDeInteres, int plazo) {
        // P es el monto del préstamo
        double p = monto;

        // r es la tasa de interés mensual
        double r = (tasaDeInteres / 12) / 100; // Dividir entre 100 para convertir a porcentaje mensual
        // n es el número total de pagos
        int n = plazo * 12;  // Plazo en años convertido a meses
        // Calculamos la cuota usando la fórmula
        double cuota = (p * r * Math.pow((1 + r), n)) / (Math.pow((1 + r), n) - 1);

        return cuota;
    }

    // Método para obtener la tasa según el tipo de préstamo
    private double obtenerTasaInteres(TipoPrestamo tipoPrestamo) {
        switch (tipoPrestamo) {
            case V -> {
                return tasaVehicular;
            }
            case P -> {
                return tasaPersonal;
            }
            case H -> {
                return tasaHipotecario;
            }
            default ->
                throw new IllegalArgumentException("Tasa de interés no disponible para el tipo de préstamo " + tipoPrestamo);
        }
    }

    private double obtenerTotalDeEgresos(Cliente cliente) {
        // Inicializar la variable totalEgresos en 0
        double totalEgresos = 0;

// Iterar sobre todos los préstamos del cliente
        for (Prestamos prestamo : cliente.getPrestamos()) {
            // Verificar si el préstamo está pendiente (estado 'P')
            if (prestamo.getEstado() == 'P') {
                // Sumar la cuota del préstamo al total de egresos
                totalEgresos += prestamo.getCuota();
            }
        }
        return totalEgresos;
    }

    private void crearTablaAmortizacion(Prestamos prestamo, double cuota, double tasaDeInteres) {
        double saldo = prestamo.getMonto(); // Saldo inicial es el monto del préstamo
        LocalDate fechaVencimiento = LocalDate.now(); // Obtener la fecha actual del sistema
        // Crear y agregar el registro de la cuota a la tabla de amortización
        TablaAmortizacion tablaAmortizacion = new TablaAmortizacion();
        // Asignar correctamente el número de cuota en la clave primaria compuesta
        IdTablaAmortizacion idTablaAmortizacion = new IdTablaAmortizacion();
        // Asociar el préstamo
        tablaAmortizacion.setPrestamo(prestamo);

        idTablaAmortizacion.setIdPrestamo(prestamo.getIdPrestamo()); // Asignar el ID del préstamo
        idTablaAmortizacion.setNumeroCuota(0); // Las cuotas empiezan desde 1, no 0

        tablaAmortizacion.setId(idTablaAmortizacion); // Asignar la clave primaria compuesta

        tablaAmortizacion.setInteres(0);
        tablaAmortizacion.setCapital(0);
        tablaAmortizacion.setSaldo(saldo);
        tablaAmortizacion.setEstado('A');
        tablaAmortizacion.setFechaVencimiento(fechaVencimiento);

        // Guardar cada cuota en la base de datos (presumiblemente con un repositorio)
        tablaAmortizacionRepository.save(tablaAmortizacion);

        for (int i = 0; i < prestamo.getPlazo() * 12; i++) {
            // Calcular el interés para la cuota actual
            double interes = (tasaDeInteres / 100) / 12 * saldo;

            // Calcular el capital de la cuota
            double capital = cuota - interes;

            // Calcular el saldo después de la cuota
            saldo = saldo - capital;

            // Crear y agregar el registro de la cuota a la tabla de amortización
            tablaAmortizacion = new TablaAmortizacion();

            // Asociar el préstamo
            tablaAmortizacion.setPrestamo(prestamo);

            // Asignar correctamente el número de cuota en la clave primaria compuesta
            idTablaAmortizacion = new IdTablaAmortizacion();
            idTablaAmortizacion.setIdPrestamo(prestamo.getIdPrestamo()); // Asignar el ID del préstamo
            idTablaAmortizacion.setNumeroCuota(i + 1); // Las cuotas empiezan desde 1, no 0

            tablaAmortizacion.setId(idTablaAmortizacion); // Asignar la clave primaria compuesta

            tablaAmortizacion.setInteres(interes);
            tablaAmortizacion.setCapital(capital);
            tablaAmortizacion.setSaldo(saldo);
            tablaAmortizacion.setEstado('P');
            tablaAmortizacion.setFechaVencimiento(fechaVencimiento);

            // Guardar cada cuota en la base de datos (presumiblemente con un repositorio)
            tablaAmortizacionRepository.save(tablaAmortizacion);

            // Incrementar la fecha para la siguiente cuota (un mes)
            fechaVencimiento = fechaVencimiento.plusMonths(1);
        }
    }

}
