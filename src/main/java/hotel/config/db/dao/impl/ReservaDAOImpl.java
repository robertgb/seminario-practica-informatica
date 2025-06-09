package hotel.config.db.dao.impl;



import hotel.config.db.Conexion;
import hotel.config.db.dao.interfaces.ReservaDAO;
import hotel.gestion.Habitacion;
import hotel.gestion.HotelException;
import hotel.gestion.Huesped;
import hotel.gestion.Reserva;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de la interfaz ReservaDAO para operaciones con la base de datos MySQL.
 */
public class ReservaDAOImpl implements ReservaDAO {

    /**
     * Guarda una nueva reserva en la base de datos.
     * Asume que huesped y habitacion ya existen en sus respectivas tablas.
     * @param reserva El objeto Reserva a guardar.
     * @throws HotelException Si ocurre un error de SQL.
     */
    @Override
    public void save(Reserva reserva) throws HotelException {
        String sql = "INSERT INTO RESERVAS (id_huesped, id_habitacion, fecha_checkin, fecha_checkout, cantidad_huespedes, estado_reserva) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Asumiendo que Huesped y Habitacion ahora tienen un getter para su ID de DB
            pstmt.setInt(1, reserva.getHuesped().getIdHuespedInterno()); // Obtener el ID de la DB del huésped
            pstmt.setInt(2, reserva.getHabitacion().getDbId()); // Obtener el ID de la DB de la habitación
            pstmt.setDate(3, Date.valueOf(reserva.getFechaCheckin()));
            pstmt.setDate(4, Date.valueOf(reserva.getFechaCheckout()));
            pstmt.setInt(5, reserva.getCantidadHuespedes());
            pstmt.setString(6, reserva.getEstado());
            pstmt.executeUpdate();

            // Obtener el ID generado por la base de datos
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reserva.setIdReservaInterno(generatedKeys.getInt(1)); // Asignar el ID autogenerado
                }
            }
            System.out.println("Reserva " + reserva.getIdReserva() + " guardada en la base de datos.");

        } catch (SQLException e) {
            throw new HotelException("Error al guardar la reserva: " + e.getMessage());
        }
    }

    /**
     * Encuentra una reserva por su ID en la base de datos.
     * @param id El ID de la reserva.
     * @return Un Optional que contiene la Reserva si se encuentra, o un Optional vacío.
     * @throws HotelException Si ocurre un error de SQL.
     */
    @Override
    public Optional<Reserva> findById(int id) throws HotelException {
        String sql = "SELECT r.id_reserva, r.id_huesped, r.id_habitacion, r.fecha_checkin, r.fecha_checkout, " +
                "r.cantidad_huespedes, r.estado_reserva FROM RESERVAS r WHERE r.id_reserva = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Necesitamos DAOs de Huesped y Habitacion para reconstruir los objetos
                    HuespedDAOImpl huespedDAO = new HuespedDAOImpl();
                    HabitacionDAOImpl habitacionDAO = new HabitacionDAOImpl();

                    Optional<Huesped> huesped = huespedDAO.findById(rs.getInt("id_huesped"));
                    Optional<Habitacion> habitacion = habitacionDAO.findById(rs.getInt("id_habitacion"));

                    if (huesped.isPresent() && habitacion.isPresent()) {
                        Reserva reserva = new Reserva(
                                String.valueOf(rs.getInt("id_reserva")), // ID externo como String
                                huesped.get(),
                                habitacion.get(),
                                rs.getDate("fecha_checkin").toLocalDate(),
                                rs.getDate("fecha_checkout").toLocalDate(),
                                rs.getInt("cantidad_huespedes")
                        );
                        reserva.setEstado(rs.getString("estado_reserva"));
                        reserva.setIdReservaInterno(rs.getInt("id_reserva")); // Establecer ID interno
                        return Optional.of(reserva);
                    } else {
                        throw new HotelException("Error al reconstruir reserva: huésped o habitación asociados no encontrados.");
                    }
                }
            }
        } catch (SQLException e) {
            throw new HotelException("Error al buscar reserva por ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Obtiene todas las reservas de la base de datos.
     * @return Una lista de objetos Reserva.
     * @throws HotelException Si ocurre un error de SQL.
     */
    @Override
    public List<Reserva> findAll() throws HotelException {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT r.id_reserva, r.id_huesped, r.id_habitacion, r.fecha_checkin, r.fecha_checkout, " +
                "r.cantidad_huespedes, r.estado_reserva FROM RESERVAS r";
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            HuespedDAOImpl huespedDAO = new HuespedDAOImpl();
            HabitacionDAOImpl habitacionDAO = new HabitacionDAOImpl();

            while (rs.next()) {
                Optional<Huesped> huesped = huespedDAO.findById(rs.getInt("id_huesped"));
                Optional<Habitacion> habitacion = habitacionDAO.findById(rs.getInt("id_habitacion"));

                if (huesped.isPresent() && habitacion.isPresent()) {
                    Reserva reserva = new Reserva(
                            String.valueOf(rs.getInt("id_reserva")),
                            huesped.get(),
                            habitacion.get(),
                            rs.getDate("fecha_checkin").toLocalDate(),
                            rs.getDate("fecha_checkout").toLocalDate(),
                            rs.getInt("cantidad_huespedes")
                    );
                    reserva.setEstado(rs.getString("estado_reserva"));
                    reserva.setIdReservaInterno(rs.getInt("id_reserva"));
                    reservas.add(reserva);
                } else {
                    System.err.println("Advertencia: No se pudo cargar reserva con ID " + rs.getInt("id_reserva") +
                            " debido a huésped o habitación faltantes. (Posible error de integridad de datos)");
                }
            }
        } catch (SQLException e) {
            throw new HotelException("Error al obtener todas las reservas: " + e.getMessage());
        }
        return reservas;
    }

    /**
     * Actualiza una reserva existente en la base de datos.
     * @param reserva El objeto Reserva con los datos actualizados (se usa su ID de DB).
     * @throws HotelException Si ocurre un error de SQL.
     */
    @Override
    public void update(Reserva reserva) throws HotelException {
        String sql = "UPDATE RESERVAS SET id_huesped = ?, id_habitacion = ?, fecha_checkin = ?, fecha_checkout = ?, " +
                "cantidad_huespedes = ?, estado_reserva = ? WHERE id_reserva = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reserva.getHuesped().getIdHuespedInterno());
            pstmt.setInt(2, reserva.getHabitacion().getDbId());
            pstmt.setDate(3, Date.valueOf(reserva.getFechaCheckin()));
            pstmt.setDate(4, Date.valueOf(reserva.getFechaCheckout()));
            pstmt.setInt(5, reserva.getCantidadHuespedes());
            pstmt.setString(6, reserva.getEstado());
            pstmt.setInt(7, reserva.getIdReservaInterno()); // Usar el ID de la DB

            pstmt.executeUpdate();
            System.out.println("Reserva " + reserva.getIdReserva() + " actualizada en la base de datos.");
        } catch (SQLException e) {
            throw new HotelException("Error al actualizar la reserva: " + e.getMessage());
        }
    }

    /**
     * Elimina una reserva de la base de datos por su ID.
     * @param id El ID de la reserva a eliminar.
     * @throws HotelException Si ocurre un error de SQL.
     */
    @Override
    public void delete(int id) throws HotelException {
        String sql = "DELETE FROM RESERVAS WHERE id_reserva = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Reserva con ID " + id + " eliminada de la base de datos.");
        } catch (SQLException e) {
            throw new HotelException("Error al eliminar la reserva: " + e.getMessage());
        }
    }
}
