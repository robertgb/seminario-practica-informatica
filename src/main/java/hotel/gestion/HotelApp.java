package hotel.gestion;

import hotel.config.db.Conexion;
import hotel.config.db.dao.impl.HabitacionDAOImpl;
import hotel.config.db.dao.impl.HuespedDAOImpl;
import hotel.config.db.dao.impl.ReservaDAOImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Optional;


/**
 * Clase principal para la aplicación de gestión del Hotel Nova.
 * Contiene el método main y el menú interactivo para operar el sistema.
 * Demuestra la utilización de las clases y el manejo de excepciones.
 */

public class HotelApp {

    public static void main(String[] args) {
        String sqlFilePath = "db/db.sql";

        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             BufferedReader reader = new BufferedReader(new FileReader(sqlFilePath))) {

            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sqlBuilder.append(line);
                if (line.trim().endsWith(";")) {
                    String sqlStatement = sqlBuilder.toString();
                    stmt.execute(sqlStatement);
                    sqlBuilder.setLength(0); // Clear buffer for next statement
                }
            }

            System.out.println("SQL script executed successfully.");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }


        // Inicializar las implementaciones DAO
        HabitacionDAOImpl habitacionDAO = new HabitacionDAOImpl();
        HuespedDAOImpl huespedDAO = new HuespedDAOImpl();
        ReservaDAOImpl reservaDAO = new ReservaDAOImpl();

        // Creación del objeto Hotel, inyectando las dependencias DAO
        Hotel hotelNova = new Hotel("Hotel Nova", habitacionDAO, huespedDAO, reservaDAO);
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        // --- Inicialización de algunas habitaciones para pruebas (se persisten en DB) ---
        // Puedes comentar o eliminar estas líneas después de la primera ejecución
        // si quieres evitar duplicados en la DB.
        try {
            if (hotelNova.buscarHabitacionPorNumero(101).isEmpty()) {
                hotelNova.agregarHabitacion(new HabitacionSimple(101, 50.0));
            }
            if (hotelNova.buscarHabitacionPorNumero(102).isEmpty()) {
                hotelNova.agregarHabitacion(new HabitacionDoble(102, 80.0));
            }
            if (hotelNova.buscarHabitacionPorNumero(201).isEmpty()) {
                hotelNova.agregarHabitacion(new HabitacionSuite(201, 150.0));
            }
        } catch (HotelException e) {
            System.err.println("Error al inicializar habitaciones: " + e.getMessage());
        }
        // --------------------------------------------------------------------------------

        // Bucle principal del menú
        while (!salir) {
            mostrarMenu();
            try {
                System.out.print("Elige una opción: ");
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir el salto de línea

                switch (opcion) {
                    case 1:
                        gestionarHabitaciones(hotelNova, scanner);
                        break;
                    case 2:
                        gestionarHuespedes(hotelNova, scanner);
                        break;
                    case 3:
                        gestionarReservas(hotelNova, scanner);
                        break;
                    case 4:
                        gestionarCheckInOut(hotelNova, scanner);
                        break;
                    case 5:
                        gestionarReportes(hotelNova);
                        break;
                    case 0:
                        salir = true;
                        System.out.println("Gracias por usar el sistema Hotel Nova. ¡Hasta pronto!");
                        break;
                    default:
                        System.out.println("Opción no válida. Por favor, intenta de nuevo.");
                }
            } catch (InputMismatchException e) {
                // Tratamiento y manejo de excepciones: InputMismatchException (valor no numérico)
                System.out.println("Error: Entrada inválida. Por favor, ingresa un número.");
                scanner.nextLine(); // Limpiar el buffer del scanner
            } catch (HotelException e) {
                // Tratamiento y manejo de excepciones: Excepciones de lógica de negocio
                System.out.println("Error del Hotel: " + e.getMessage());
            } catch (Exception e) {
                // Captura cualquier otra excepción inesperada
                System.out.println("Ha ocurrido un error inesperado: " + e.getMessage());
                e.printStackTrace(); // Imprimir la pila de llamadas para depuración
            }
            System.out.println("\nPresiona Enter para continuar...");
            scanner.nextLine(); // Esperar que el usuario presione Enter
        }

        scanner.close(); // Cerrar el scanner al finalizar
    }

    /**
     * Muestra el menú principal de opciones del sistema.
     */
    private static void mostrarMenu() {
        System.out.println("\n+------------------------------------+");
        System.out.println("|        MENÚ PRINCIPAL - Hotel Nova |");
        System.out.println("+------------------------------------+");
        System.out.println("| 1. Gestión de Habitaciones         |");
        System.out.println("| 2. Gestión de Huéspedes            |");
        System.out.println("| 3. Gestión de Reservas             |");
        System.out.println("| 4. Check-in / Check-out            |");
        System.out.println("| 5. Reportes                        |");
        System.out.println("| 0. Salir                           |");
        System.out.println("+------------------------------------+");
    }

    /**
     * Submenú y lógica para la gestión de habitaciones.
     */
    private static void gestionarHabitaciones(Hotel hotel, Scanner scanner) throws HotelException {
        System.out.println("\n--- Gestión de Habitaciones ---");
        System.out.println("1. Agregar Habitación");
        System.out.println("2. Listar Habitaciones");
        System.out.println("3. Actualizar Estado de Habitación");
        System.out.println("0. Volver al Menú Principal");
        System.out.print("Elige una opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine(); // Consumir salto de línea

        switch (opcion) {
            case 1:
                System.out.print("Número de la habitación: ");
                int numHab = scanner.nextInt();
                scanner.nextLine();
                System.out.print("Tipo de habitación (Simple, Doble, Suite): ");
                String tipoHab = scanner.nextLine();
                System.out.print("Precio por noche: ");
                double precioHab = scanner.nextDouble();
                scanner.nextLine();

                switch (tipoHab.toLowerCase()) {
                    case "simple":
                        hotel.agregarHabitacion(new HabitacionSimple(numHab, precioHab));
                        break;
                    case "doble":
                        hotel.agregarHabitacion(new HabitacionDoble(numHab, precioHab));
                        break;
                    case "suite":
                        hotel.agregarHabitacion(new HabitacionSuite(numHab, precioHab));
                        break;
                    default:
                        System.out.println("Tipo de habitación no válido. No se agregó la habitación.");
                }
                break;
            case 2:
                hotel.listarHabitaciones();
                break;
            case 3:
                System.out.print("Número de habitación a actualizar: ");
                int numAct = scanner.nextInt();
                scanner.nextLine();
                System.out.print("Nuevo estado (Disponible, Ocupada, En Limpieza, Mantenimiento): ");
                String estadoAct = scanner.nextLine();
                hotel.actualizarEstadoHabitacion(numAct, estadoAct);
                break;
            case 0:
                // Volver al menú principal
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    /**
     * Submenú y lógica para la gestión de huéspedes.
     */
    private static void gestionarHuespedes(Hotel hotel, Scanner scanner) throws HotelException {
        System.out.println("\n--- Gestión de Huéspedes ---");
        System.out.println("1. Registrar Nuevo Huésped");
        System.out.println("0. Volver al Menú Principal");
        System.out.print("Elige una opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();

        switch (opcion) {
            case 1:
                System.out.print("Nombre del huésped: ");
                String nombre = scanner.nextLine();
                System.out.print("Apellido del huésped: ");
                String apellido = scanner.nextLine();
                System.out.print("DNI del huésped: ");
                String dni = scanner.nextLine();
                System.out.print("Email del huésped: ");
                String email = scanner.nextLine();
                System.out.print("Teléfono del huésped: ");
                String telefono = scanner.nextLine();
                hotel.registrarHuesped(nombre, apellido, dni, email, telefono);
                break;
            case 0:
                // Volver al menú principal
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    /**
     * Submenú y lógica para la gestión de reservas.
     */
    private static void gestionarReservas(Hotel hotel, Scanner scanner) throws HotelException {
        System.out.println("\n--- Gestión de Reservas ---");
        System.out.println("1. Crear Nueva Reserva");
        System.out.println("2. Cancelar Reserva");
        System.out.println("3. Listar Todas las Reservas");
        System.out.println("0. Volver al Menú Principal");
        System.out.print("Elige una opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();

        switch (opcion) {
            case 1:
                System.out.print("DNI del huésped existente: ");
                String dniHuesped = scanner.nextLine();
                Optional<Huesped> optHuesped = hotel.huespedDAO.findByDni(dniHuesped); // Acceso al DAO desde HotelNovaApp
                Huesped huesped = null;
                if (optHuesped.isPresent()) {
                    huesped = optHuesped.get();
                } else {
                    System.out.println("Huésped no encontrado con DNI " + dniHuesped + ". Por favor, regístralo primero.");
                    break;
                }

                System.out.print("Número de habitación a reservar: ");
                int numHabReserva = scanner.nextInt();
                scanner.nextLine();

                LocalDate checkin = null;
                LocalDate checkout = null;
                System.out.print("Fecha de Check-in (YYYY-MM-DD): ");
                String checkinStr = scanner.nextLine();
                System.out.print("Fecha de Check-out (YYYY-MM-DD): ");
                String checkoutStr = scanner.nextLine();

                try {
                    checkin = LocalDate.parse(checkinStr);
                    checkout = LocalDate.parse(checkoutStr);
                } catch (DateTimeParseException e) {
                    throw new HotelException("Formato de fecha inválido. Usa YYYY-MM-DD.");
                }

                System.out.print("Cantidad de huéspedes: ");
                int cantHuespedes = scanner.nextInt();
                scanner.nextLine();

                hotel.crearReserva(huesped, numHabReserva, checkin, checkout, cantHuespedes);
                break;
            case 2:
                System.out.print("ID DE BASE DE DATOS de la reserva a cancelar: ");
                int idReservaCancelar = scanner.nextInt();
                scanner.nextLine();
                hotel.cancelarReserva(idReservaCancelar);
                break;
            case 3:
                hotel.listarReservas();
                break;
            case 0:
                // Volver al menú principal
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    /**
     * Submenú y lógica para Check-in y Check-out.
     */
    private static void gestionarCheckInOut(Hotel hotel, Scanner scanner) throws HotelException {
        System.out.println("\n--- Check-in / Check-out ---");
        System.out.println("1. Realizar Check-in");
        System.out.println("2. Realizar Check-out");
        System.out.println("0. Volver al Menú Principal");
        System.out.print("Elige una opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();

        switch (opcion) {
            case 1:
                System.out.print("ID DE BASE DE DATOS de la reserva para Check-in: ");
                int idReservaCheckIn = scanner.nextInt();
                scanner.nextLine();
                hotel.realizarCheckIn(idReservaCheckIn);
                break;
            case 2:
                System.out.print("ID DE BASE DE DATOS de la reserva para Check-out: ");
                int idReservaCheckOut = scanner.nextInt();
                scanner.nextLine();
                hotel.realizarCheckOut(idReservaCheckOut);
                break;
            case 0:
                // Volver al menú principal
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    /**
     * Submenú y lógica para reportes.
     */
    private static void gestionarReportes(Hotel hotel) throws HotelException {
        System.out.println("\n--- Reportes ---");
        System.out.println("1. Reporte de Ocupación Actual");
        System.out.println("2. Calcular Ingresos Totales");
        System.out.println("0. Volver al Menú Principal");
        System.out.print("Elige una opción: ");
        // Usar System.in directamente, no crear un nuevo Scanner aquí.
        // Si se crea un nuevo Scanner en un método, cerrarlo puede cerrar System.in,
        // lo que afecta al Scanner principal.
        int opcion = new Scanner(System.in).nextInt(); // Captura la opción
        new Scanner(System.in).nextLine(); // Consumir salto de línea

        switch (opcion) {
            case 1:
                hotel.mostrarReporteOcupacion(); // RFS15, RFS19
                break;
            case 2:
                hotel.calcularIngresosTotales(); // RFS16
                break;
            case 0:
                // Volver al menú principal
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }
}
