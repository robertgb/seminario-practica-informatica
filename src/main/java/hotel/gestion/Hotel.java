package hotel.gestion;

import hotel.config.db.dao.interfaces.HabitacionDAO;
import hotel.config.db.dao.interfaces.HuespedDAO;
import hotel.config.db.dao.interfaces.ReservaDAO;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

/**
 * Clase que representa el Hotel Nova y gestiona sus habitaciones, huéspedes y reservas
 * utilizando Data Access Objects (DAOs) para la persistencia.
 */
public class Hotel {
    private String nombre;
    // Inyección de dependencias: Hotel ahora depende de las interfaces DAO
    private HabitacionDAO habitacionDAO;
    HuespedDAO huespedDAO;
    private ReservaDAO reservaDAO;

    /**
     * Constructor de la clase Hotel.
     * Las implementaciones DAO son inyectadas (pasadas como argumentos).
     * @param nombre El nombre del hotel.
     * @param habitacionDAO El DAO para la gestión de habitaciones.
     * @param huespedDAO El DAO para la gestión de huéspedes.
     * @param reservaDAO El DAO para la gestión de reservas.
     */
    public Hotel(String nombre, HabitacionDAO habitacionDAO, HuespedDAO huespedDAO, ReservaDAO reservaDAO) {
        this.nombre = nombre;
        this.habitacionDAO = habitacionDAO;
        this.huespedDAO = huespedDAO;
        this.reservaDAO = reservaDAO;
    }

    // --- Métodos de Gestión de Habitaciones (RFS05, RFS11, RFS12, RFS13) ---

    /**
     * Agrega una nueva habitación al hotel y la persiste en la base de datos.
     * @param habitacion El objeto Habitacion a agregar.
     * @throws HotelException Si ocurre un error durante la persistencia.
     */
    public void agregarHabitacion(Habitacion habitacion) throws HotelException {
        // Antes de guardar, verificar si ya existe una habitación con ese número
        Optional<Habitacion> existing = habitacionDAO.findByNumero(habitacion.getNumero());
        if (existing.isPresent()) {
            throw new HotelException("Ya existe una habitación con el número " + habitacion.getNumero() + ".");
        }
        habitacionDAO.save(habitacion);
    }

    /**
     * Busca una habitación por su número en la base de datos.
     * @param numero El número de la habitación a buscar.
     * @return Un Optional que contiene la Habitacion si se encuentra, o un Optional vacío.
     * @throws HotelException Si ocurre un error durante la búsqueda en la DB.
     */
    public Optional<Habitacion> buscarHabitacionPorNumero(int numero) throws HotelException {
        return habitacionDAO.findByNumero(numero);
    }

    /**
     * Lista todas las habitaciones registradas en el hotel, obteniéndolas de la base de datos (RFS13).
     * @throws HotelException Si ocurre un error durante la consulta en la DB.
     */
    public void listarHabitaciones() throws HotelException {
        List<Habitacion> habitaciones = habitacionDAO.findAll();
        if (habitaciones.isEmpty()) {
            System.out.println("No hay habitaciones registradas en el " + nombre + ".");
            return;
        }
        System.out.println("\n--- Listado de Habitaciones en " + nombre + " ---");
        for (Habitacion h : habitaciones) {
            System.out.println(h);
        }
        System.out.println("------------------------------------------");
    }

    /**
     * Actualiza el estado de una habitación existente en la base de datos (RFS11, RFS12).
     * @param numeroHabitacion El número de la habitación a actualizar.
     * @param nuevoEstado El nuevo estado (ej: "Disponible", "En Limpieza", "Mantenimiento").
     * @throws HotelException Si la habitación no se encuentra o el estado no es válido.
     */
    public void actualizarEstadoHabitacion(int numeroHabitacion, String nuevoEstado) throws HotelException {
        Optional<Habitacion> optHabitacion = habitacionDAO.findByNumero(numeroHabitacion);
        if (optHabitacion.isPresent()) {
            Habitacion h = optHabitacion.get();
            // Validación básica de estados permitidos
            if (nuevoEstado.equals("Disponible") || nuevoEstado.equals("En Limpieza") ||
                    nuevoEstado.equals("Mantenimiento") || nuevoEstado.equals("Ocupada")) {
                h.setEstado(nuevoEstado);
                habitacionDAO.update(h); // Persistir el cambio de estado
                System.out.println("Estado de la habitación " + numeroHabitacion + " actualizado a: " + nuevoEstado + ".");
            } else {
                throw new HotelException("Estado '" + nuevoEstado + "' no válido para la habitación.");
            }
        } else {
            throw new HotelException("Habitación con número " + numeroHabitacion + " no encontrada.");
        }
    }

    // --- Métodos de Gestión de Huéspedes ---

    /**
     * Registra un nuevo huésped en el sistema y lo persiste en la base de datos.
     * @param nombre Nombre del huésped.
     * @param apellido Apellido del huésped.
     * @param dni DNI del huésped (se usa para identificar si ya existe).
     * @param email Email del huésped.
     * @param telefono Teléfono del huésped.
     * @return El objeto Huesped creado o encontrado.
     * @throws HotelException Si ocurre un error durante la persistencia o si el DNI ya existe.
     */
    public Huesped registrarHuesped(String nombre, String apellido, String dni, String email, String telefono) throws HotelException {
        // Buscar si el huésped ya existe por DNI (algoritmo de búsqueda)
        Optional<Huesped> existingHuesped = huespedDAO.findByDni(dni);
        if (existingHuesped.isPresent()) {
            System.out.println("Huésped con DNI " + dni + " ya registrado. Se utilizará el existente.");
            return existingHuesped.get();
        } else {
            // El ID externo como String puede ser un marcador o el DNI para este prototipo
            Huesped nuevoHuesped = new Huesped(dni, nombre, apellido, dni, email, telefono);
            huespedDAO.save(nuevoHuesped); // El ID_DB se asigna en el DAO al guardar
            return nuevoHuesped;
        }
    }

    // --- Métodos de Gestión de Reservas (RFS02, RFS03, RFS04, RFS05) ---

    /**
     * Crea una nueva reserva para un huésped y una habitación y la persiste en la base de datos (RFS02, RFS03).
     * @param huesped El objeto Huesped que realiza la reserva (debe estar ya persistido con un ID de DB).
     * @param numeroHabitacion El número de la habitación deseada.
     * @param fechaCheckin La fecha de check-in.
     * @param fechaCheckout La fecha de check-out.
     * @param cantidadHuespedes La cantidad de huéspedes en la reserva.
     * @return El objeto Reserva creado.
     * @throws HotelException Si la habitación no existe, no está disponible o las fechas son inválidas.
     */
    public Reserva crearReserva(Huesped huesped, int numeroHabitacion,
                                LocalDate fechaCheckin, LocalDate fechaCheckout,
                                int cantidadHuespedes) throws HotelException {

        // Verificar que el huésped esté persistido (tenga un ID de DB)
        if (huesped.getIdHuespedInterno() == 0) {
            throw new HotelException("El huésped no ha sido persistido en la base de datos. Por favor, registre al huésped primero.");
        }

        Optional<Habitacion> optHabitacion = habitacionDAO.findByNumero(numeroHabitacion);

        if (!optHabitacion.isPresent()) {
            throw new HotelException("Habitación con número " + numeroHabitacion + " no encontrada.");
        }

        Habitacion habitacion = getHabitacion(numeroHabitacion, optHabitacion);

        // El ID externo de la reserva puede ser un UUID o un timestamp para este prototipo
        String idReservaExterno = "RES-" + System.currentTimeMillis();
        Reserva nuevaReserva = new Reserva(idReservaExterno, huesped, habitacion, fechaCheckin, fechaCheckout, cantidadHuespedes);

        reservaDAO.save(nuevaReserva); // Persistir la reserva. El ID_DB se asigna en el DAO.

        // Marcar la habitación como "Ocupada" si la reserva es para hoy y se asume check-in inmediato
        // o si es una reserva a futuro, se marcará "Confirmada" y luego "Ocupada" en el check-in.
        if (!nuevaReserva.getFechaCheckin().isAfter(LocalDate.now())) {
            habitacion.setEstado("Ocupada");
            habitacionDAO.update(habitacion); // Actualizar el estado en la DB
        } else {
            System.out.println("Nota: Habitación " + habitacion.getNumero() + " marcada como reservada, pero su estado físico sigue siendo 'Disponible' hasta el check-in real.");
        }

        System.out.println("Reserva " + nuevaReserva.getIdReserva() + " creada exitosamente para " + huesped.getNombre() + " " + huesped.getApellido() + ".");
        return nuevaReserva;
    }

    private static Habitacion getHabitacion(int numeroHabitacion, Optional<Habitacion> optHabitacion) throws HotelException {
        Habitacion habitacion = optHabitacion.get();

        // RFS03: Verificar disponibilidad (simplificado para el prototipo: solo estado 'Disponible')
        // En un sistema real, se verificaría que la habitación no esté ocupada durante las fechas de la reserva
        // consultando las reservas existentes para esas fechas. Para el prototipo, asumimos si no está Ocupada.
        if (!habitacion.getEstado().equals("Disponible")) {
            throw new HotelException("Habitación " + numeroHabitacion + " no disponible. Estado actual: " + habitacion.getEstado() + "."); // RFS17
        }
        return habitacion;
    }

    /**
     * Busca una reserva por su ID interno de base de datos.
     * @param idReservaDB El ID interno de la reserva a buscar.
     * @return Un Optional que contiene la Reserva si se encuentra, o un Optional vacío.
     * @throws HotelException Si ocurre un error durante la búsqueda en la DB.
     */
    public Optional<Reserva> buscarReservaPorId(int idReservaDB) throws HotelException {
        return reservaDAO.findById(idReservaDB);
    }

    /**
     * Cancela una reserva existente (RFS05).
     * @param idReservaDB El ID interno de la reserva a cancelar.
     * @throws HotelException Si la reserva no se encuentra o ya ha sido cancelada/finalizada.
     */
    public void cancelarReserva(int idReservaDB) throws HotelException {
        Optional<Reserva> optReserva = reservaDAO.findById(idReservaDB);
        if (optReserva.isPresent()) {
            Reserva reserva = optReserva.get();
            if (reserva.getEstado().equals("Confirmada")) {
                reserva.setEstado("Cancelada");
                reservaDAO.update(reserva); // Actualizar estado en DB

                // Liberar la habitación y actualizar su estado en la DB
                reserva.getHabitacion().setEstado("Disponible");
                habitacionDAO.update(reserva.getHabitacion());

                System.out.println("Reserva " + idReservaDB + " cancelada y habitación " + reserva.getHabitacion().getNumero() + " liberada.");
            } else {
                throw new HotelException("La reserva " + idReservaDB + " no puede ser cancelada. Estado actual: " + reserva.getEstado() + ".");
            }
        } else {
            throw new HotelException("Reserva con ID " + idReservaDB + " no encontrada.");
        }
    }

    // --- Métodos de Check-in/Check-out (RFS06, RFS07) ---

    /**
     * Realiza el check-in de una reserva (RFS06).
     * @param idReservaDB El ID interno de la reserva a la que se le hará check-in.
     * @throws HotelException Si la reserva no existe, no está confirmada o la habitación no está disponible.
     */
    public void realizarCheckIn(int idReservaDB) throws HotelException {
        Optional<Reserva> optReserva = reservaDAO.findById(idReservaDB);
        if (optReserva.isPresent()) {
            Reserva reserva = optReserva.get();
            if (reserva.getEstado().equals("Confirmada")) {
                Habitacion habitacion = reserva.getHabitacion();
                if (habitacion.getEstado().equals("Disponible")) { // RFS17
                    reserva.setEstado("Check-in");
                    reservaDAO.update(reserva); // Actualizar estado en DB

                    habitacion.setEstado("Ocupada");
                    habitacionDAO.update(habitacion); // Actualizar estado en DB

                    System.out.println("Check-in realizado para la reserva " + idReservaDB + " en la habitación " + habitacion.getNumero() + ".");
                } else {
                    throw new HotelException("Habitación " + habitacion.getNumero() + " no disponible para check-in. Estado: " + habitacion.getEstado() + "."); // RFS17
                }
            } else {
                throw new HotelException("La reserva " + idReservaDB + " no está en estado 'Confirmada' para realizar el check-in. Estado: " + reserva.getEstado() + ".");
            }
        } else {
            throw new HotelException("Reserva con ID " + idReservaDB + " no encontrada.");
        }
    }

    /**
     * Realiza el check-out de una reserva (RFS07).
     * @param idReservaDB El ID interno de la reserva a la que se le hará check-out.
     * @throws HotelException Si la reserva no existe o no está en estado de check-in.
     */
    public void realizarCheckOut(int idReservaDB) throws HotelException {
        Optional<Reserva> optReserva = reservaDAO.findById(idReservaDB);
        if (optReserva.isPresent()) {
            Reserva reserva = optReserva.get();
            if (reserva.getEstado().equals("Check-in")) {
                // Lógica de facturación (simplificada, solo calcula y muestra)
                double costoTotal = reserva.calcularCostoTotal();
                System.out.println("\n--- Factura para Reserva " + idReservaDB + " ---");
                System.out.println("Huésped: " + reserva.getHuesped().getNombre() + " " + reserva.getHuesped().getApellido());
                System.out.println("Habitación: " + reserva.getHabitacion().getNumero() + " (" + reserva.getHabitacion().getTipo() + ")");
                System.out.println("Estancia: del " + reserva.getFechaCheckin() + " al " + reserva.getFechaCheckout());
                System.out.println("Costo total de la estancia: $" + String.format("%.2f", costoTotal));
                // Aquí se añadiría lógica para RFS08 (cargos adicionales) y RFS09 (generación de facturas detalladas)
                // y RFS10 (registro de pagos) con sus propios DAOs.

                reserva.setEstado("Check-out");
                reservaDAO.update(reserva); // Actualizar estado en DB

                reserva.getHabitacion().setEstado("En Limpieza"); // Marcar para limpieza después del check-out
                habitacionDAO.update(reserva.getHabitacion()); // Actualizar estado en DB

                System.out.println("Check-out realizado para la reserva " + idReservaDB + ". Habitación " + reserva.getHabitacion().getNumero() + " marcada como 'En Limpieza'.");
            } else {
                throw new HotelException("La reserva " + idReservaDB + " no está en estado 'Check-in' para realizar el check-out. Estado: " + reserva.getEstado() + ".");
            }
        } else {
            throw new HotelException("Reserva con ID " + idReservaDB + " no encontrada.");
        }
    }

    // --- Métodos de Reportes (RFS15, RFS16, RFS19) ---

    /**
     * Calcula los ingresos totales generados por todas las reservas finalizadas (RFS16),
     * obteniéndolas de la base de datos.
     * @return El total de ingresos.
     * @throws HotelException Si ocurre un error durante la consulta en la DB.
     */
    public double calcularIngresosTotales() throws HotelException {
        double totalIngresos = 0;
        List<Reserva> reservas = reservaDAO.findAll(); // Obtener todas las reservas de la DB
        for (Reserva r : reservas) {
            if (r.getEstado().equals("Check-out")) {
                totalIngresos += r.calcularCostoTotal();
            }
        }
        System.out.println("\n--- Reporte de Ingresos ---");
        System.out.println("Ingresos totales de reservas finalizadas: $" + String.format("%.2f", totalIngresos));
        return totalIngresos;
    }

    /**
     * Muestra un reporte de ocupación simple, obteniendo los datos de la base de datos (RFS15, RFS19 - historial).
     * @throws HotelException Si ocurre un error durante la consulta en la DB.
     */
    public void mostrarReporteOcupacion() throws HotelException {
        List<Habitacion> habitaciones = habitacionDAO.findAll(); // Obtener todas las habitaciones de la DB
        long habitacionesDisponibles = habitaciones.stream().filter(h -> h.getEstado().equals("Disponible")).count();
        long habitacionesOcupadas = habitaciones.stream().filter(h -> h.getEstado().equals("Ocupada")).count();
        long habitacionesEnLimpieza = habitaciones.stream().filter(h -> h.getEstado().equals("En Limpieza")).count();
        long habitacionesEnMantenimiento = habitaciones.stream().filter(h -> h.getEstado().equals("Mantenimiento")).count();

        System.out.println("\n--- Reporte de Ocupación Actual ---");
        System.out.println("Total de Habitaciones: " + habitaciones.size());
        System.out.println("Disponibles: " + habitacionesDisponibles);
        System.out.println("Ocupadas: " + habitacionesOcupadas);
        System.out.println("En Limpieza: " + habitacionesEnLimpieza);
        System.out.println("En Mantenimiento: " + habitacionesEnMantenimiento);
        System.out.println("------------------------------------");
    }

    /**
     * Muestra todas las reservas en el sistema, obteniéndolas de la base de datos.
     * @throws HotelException Si ocurre un error durante la consulta en la DB.
     */
    public void listarReservas() throws HotelException {
        List<Reserva> reservas = reservaDAO.findAll(); // Obtener todas las reservas de la DB
        if (reservas.isEmpty()) {
            System.out.println("No hay reservas registradas.");
            return;
        }
        System.out.println("\n--- Listado de Reservas ---");
        for (Reserva r : reservas) {
            System.out.println(r);
        }
        System.out.println("--------------------------");
    }
}
