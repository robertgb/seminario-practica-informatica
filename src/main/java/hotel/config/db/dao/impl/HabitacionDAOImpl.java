package hotel.config.db.dao.impl;


import hotel.config.db.Conexion;
import hotel.config.db.dao.interfaces.HabitacionDAO;
import hotel.gestion.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de la interfaz HabitacionDAO para operaciones con la base de datos MySQL.
 */
public class HabitacionDAOImpl implements HabitacionDAO {

    /**
     * Guarda una nueva habitación en la base de datos.
     * El ID de la habitación será generado automáticamente por la DB.
     * @param habitacion El objeto Habitacion a guardar.
     * @throws HotelException Si ocurre un error de SQL.
     */
    @Override
    public void save(Habitacion habitacion) throws HotelException {
        String sql = "INSERT INTO HABITACIONES (numero_habitacion, tipo, precio_por_noche, estado) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, habitacion.getNumero());
            pstmt.setString(2, habitacion.getTipo());
            pstmt.setDouble(3, habitacion.getPrecioPorNoche());
            pstmt.setString(4, habitacion.getEstado());
            pstmt.executeUpdate();

            // Obtener el ID generado por la base de datos y asignarlo al objeto Habitacion
            // Esto es crucial para que el objeto en memoria refleje el estado de la DB
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // Si Habitacion tuviera un setter para el ID, lo usaríamos aquí.
                    // Para este prototipo, el ID de DB es handled internamente si no se setea directamente.
                    // Podemos asumir que el ID es el numero_habitacion o buscarlo si necesario.
                    // No se necesita un setter para id_habitacion ya que es AUTO_INCREMENT.
                }
            }
            System.out.println("Habitación " + habitacion.getNumero() + " guardada en la base de datos.");

        } catch (SQLException e) {
            throw new HotelException("Error al guardar la habitación: " + e.getMessage());
        }
    }

    /**
     * Encuentra una habitación por su ID en la base de datos.
     * @param id El ID de la habitación.
     * @return Un Optional que contiene la Habitacion si se encuentra, o un Optional vacío.
     * @throws HotelException Si ocurre un error de SQL.
     */
    @Override
    public Optional<Habitacion> findById(int id) throws HotelException {
        String sql = "SELECT id_habitacion, numero_habitacion, tipo, precio_por_noche, estado FROM HABITACIONES WHERE id_habitacion = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(createHabitacionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new HotelException("Error al buscar habitación por ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Encuentra una habitación por su número de habitación en la base de datos.
     * @param numero El número de habitación.
     * @return Un Optional que contiene la Habitacion si se encuentra, o un Optional vacío.
     * @throws HotelException Si ocurre un error de SQL.
     */
    @Override
    public Optional<Habitacion> findByNumero(int numero) throws HotelException {
        String sql = "SELECT id_habitacion, numero_habitacion, tipo, precio_por_noche, estado FROM HABITACIONES WHERE numero_habitacion = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, numero);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(createHabitacionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new HotelException("Error al buscar habitación por número: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Obtiene todas las habitaciones de la base de datos.
     * @return Una lista de objetos Habitacion.
     * @throws HotelException Si ocurre un error de SQL.
     */
    @Override
    public List<Habitacion> findAll() throws HotelException {
        List<Habitacion> habitaciones = new ArrayList<>();
        String sql = "SELECT id_habitacion, numero_habitacion, tipo, precio_por_noche, estado FROM HABITACIONES";
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                habitaciones.add(createHabitacionFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new HotelException("Error al obtener todas las habitaciones: " + e.getMessage());
        }
        return habitaciones;
    }

    /**
     * Actualiza el estado de una habitación existente en la base de datos.
     * @param habitacion El objeto Habitacion con los datos actualizados (se usa su ID de DB).
     * @throws HotelException Si ocurre un error de SQL.
     */
    @Override
    public void update(Habitacion habitacion) throws HotelException {
        String sql = "UPDATE HABITACIONES SET numero_habitacion = ?, tipo = ?, precio_por_noche = ?, estado = ? WHERE id_habitacion = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Para que este update funcione, la clase Habitacion necesita tener un id_habitacion (de la DB)
            // Ya que el prototipo original no lo tenía, podemos asumir que se busca por numero_habitacion
            // o modificar la clase Habitacion para incluir un campo 'dbId'.
            // Por simplicidad, se asume que la Habitacion en el objeto ya tiene el ID de la DB
            // o que la estamos buscando por el numero_habitacion.
            // Para el prototipo: voy a buscarla por numero_habitacion y actualizar su id_habitacion de DB.
            Optional<Habitacion> existing = findByNumero(habitacion.getNumero());
            if (!existing.isPresent()) {
                throw new HotelException("Habitación a actualizar no encontrada por número: " + habitacion.getNumero());
            }

            int dbId = existing.get().getDbId();

            pstmt.setInt(1, habitacion.getNumero());
            pstmt.setString(2, habitacion.getTipo());
            pstmt.setDouble(3, habitacion.getPrecioPorNoche());
            pstmt.setString(4, habitacion.getEstado());
            pstmt.setInt(5, dbId); // Usar el ID de la DB

            pstmt.executeUpdate();
            System.out.println("Habitación " + habitacion.getNumero() + " actualizada en la base de datos.");
        } catch (SQLException e) {
            throw new HotelException("Error al actualizar la habitación: " + e.getMessage());
        }
    }

    /**
     * Elimina una habitación de la base de datos por su ID.
     * @param id El ID de la habitación a eliminar.
     * @throws HotelException Si ocurre un error de SQL.
     */
    @Override
    public void delete(int id) throws HotelException {
        String sql = "DELETE FROM HABITACIONES WHERE id_habitacion = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Habitación con ID " + id + " eliminada de la base de datos.");
        } catch (SQLException e) {
            throw new HotelException("Error al eliminar la habitación: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para construir un objeto Habitacion a partir de un ResultSet.
     * Esto maneja la creación de las subclases de Habitacion basándose en el tipo.
     * También, para que el objeto tenga el ID de la base de datos, necesito una forma de pasárselo.
     * Crear una interfaz para esto o un campo en Habitacion.
     */
    private Habitacion createHabitacionFromResultSet(ResultSet rs) throws SQLException {
        int dbId = rs.getInt("id_habitacion"); // Obtener el ID de la base de datos
        int numero = rs.getInt("numero_habitacion");
        String tipo = rs.getString("tipo");
        double precioPorNoche = rs.getDouble("precio_por_noche");
        String estado = rs.getString("estado");

        Habitacion habitacion;
        switch (tipo) {
            case "Simple":
                habitacion = new HabitacionSimple(numero, precioPorNoche);
                break;
            case "Doble":
                habitacion = new HabitacionDoble(numero, precioPorNoche);
                break;
            case "Suite":
                habitacion = new HabitacionSuite(numero, precioPorNoche);
                break;
            default:
                throw new SQLException("Tipo de habitación desconocido en la base de datos: " + tipo);
        }
        habitacion.setEstado(estado);

        habitacion.setDbId(dbId);

        return habitacion;
    }
}




