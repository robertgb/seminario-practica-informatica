package hotel.config.db.dao.impl;



import hotel.config.db.Conexion;
import hotel.config.db.dao.interfaces.HuespedDAO;
import hotel.gestion.HotelException;
import hotel.gestion.Huesped;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de la interfaz HuespedDAO para operaciones con la base de datos MySQL.
 */
public class HuespedDAOImpl implements HuespedDAO {

    /**
     * Guarda un nuevo huésped en la base de datos.
     * El ID del huésped será generado automáticamente por la DB.
     * @param huesped El objeto Huesped a guardar.
     * @throws HotelException Si ocurre un error de SQL.
     */
    @Override
    public void save(Huesped huesped) throws HotelException {
        String sql = "INSERT INTO HUESPEDES (nombre, apellido, dni, email, telefono) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, huesped.getNombre());
            pstmt.setString(2, huesped.getApellido());
            pstmt.setString(3, huesped.getDni());
            pstmt.setString(4, huesped.getEmail());
            pstmt.setString(5, huesped.getTelefono());
            pstmt.executeUpdate();

            // Obtener el ID generado por la base de datos
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // Para que el objeto Huesped en memoria tenga su ID de DB,
                    // necesito un setter o un constructor apropiado.
                    // Asumiendo que Huesped ahora tiene un setter setIdHuesped(int) o se maneja internamente.
                    // O se puede crear una interfaz HuespedConID.
                    // Para este prototipo, modificaremos Huesped para que tenga un setIdHuesped.
                    huesped.setIdHuespedInterno(generatedKeys.getInt(1)); // Asignar el ID autogenerado
                }
            }
            System.out.println("Huésped " + huesped.getNombre() + " " + huesped.getApellido() + " guardado en la base de datos con ID: " + huesped.getIdHuesped() + ".");

        } catch (SQLException e) {
            throw new HotelException("Error al guardar el huésped: " + e.getMessage());
        }
    }

    /**
     * Encuentra un huésped por su ID en la base de datos.
     * @param id El ID del huésped.
     * @return Un Optional que contiene el Huesped si se encuentra, o un Optional vacío.
     * @throws HotelException Si ocurre un error de SQL.
     */
    @Override
    public Optional<Huesped> findById(int id) throws HotelException {
        String sql = "SELECT id_huesped, nombre, apellido, dni, email, telefono FROM HUESPEDES WHERE id_huesped = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(createHuespedFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new HotelException("Error al buscar huésped por ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Encuentra un huésped por su DNI en la base de datos.
     * @param dni El DNI del huésped.
     * @return Un Optional que contiene el Huesped si se encuentra, o un Optional vacío.
     * @throws HotelException Si ocurre un error de SQL.
     */
    @Override
    public Optional<Huesped> findByDni(String dni) throws HotelException {
        String sql = "SELECT id_huesped, nombre, apellido, dni, email, telefono FROM HUESPEDES WHERE dni = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dni);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(createHuespedFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new HotelException("Error al buscar huésped por DNI: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Obtiene todos los huéspedes de la base de datos.
     * @return Una lista de objetos Huesped.
     * @throws HotelException Si ocurre un error de SQL.
     */
    @Override
    public List<Huesped> findAll() throws HotelException {
        List<Huesped> huespedes = new ArrayList<>();
        String sql = "SELECT id_huesped, nombre, apellido, dni, email, telefono FROM HUESPEDES";
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                huespedes.add(createHuespedFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new HotelException("Error al obtener todos los huéspedes: " + e.getMessage());
        }
        return huespedes;
    }

    /**
     * Actualiza un huésped existente en la base de datos.
     * @param huesped El objeto Huesped con los datos actualizados (se usa su ID de DB).
     * @throws HotelException Si ocurre un error de SQL.
     */
    @Override
    public void update(Huesped huesped) throws HotelException {
        String sql = "UPDATE HUESPEDES SET nombre = ?, apellido = ?, dni = ?, email = ?, telefono = ? WHERE id_huesped = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, huesped.getNombre());
            pstmt.setString(2, huesped.getApellido());
            pstmt.setString(3, huesped.getDni());
            pstmt.setString(4, huesped.getEmail());
            pstmt.setString(5, huesped.getTelefono());
            pstmt.setInt(6, huesped.getIdHuespedInterno()); // Usar el ID de la DB

            pstmt.executeUpdate();
            System.out.println("Huésped con ID " + huesped.getIdHuesped() + " actualizado en la base de datos.");
        } catch (SQLException e) {
            throw new HotelException("Error al actualizar el huésped: " + e.getMessage());
        }
    }

    /**
     * Elimina un huésped de la base de datos por su ID.
     * @param id El ID del huésped a eliminar.
     * @throws HotelException Si ocurre un error de SQL.
     */
    @Override
    public void delete(int id) throws HotelException {
        String sql = "DELETE FROM HUESPEDES WHERE id_huesped = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Huésped con ID " + id + " eliminado de la base de datos.");
        } catch (SQLException e) {
            throw new HotelException("Error al eliminar el huésped: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para construir un objeto Huesped a partir de un ResultSet.
     */
    private Huesped createHuespedFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_huesped");
        String nombre = rs.getString("nombre");
        String apellido = rs.getString("apellido");
        String dni = rs.getString("dni");
        String email = rs.getString("email");
        String telefono = rs.getString("telefono");
        Huesped huesped = new Huesped(String.valueOf(id), nombre, apellido, dni, email, telefono);
        huesped.setIdHuespedInterno(id); // Establecer el ID interno de la DB
        return huesped;
    }
}
