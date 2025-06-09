package hotel.config.db.dao.interfaces;

import hotel.gestion.HotelException;
import hotel.gestion.Huesped;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el Data Access Object (DAO) de la entidad Huesped.
 * Define las operaciones CRUD para los huéspedes.
 */
public interface HuespedDAO {
    // Método para guardar un nuevo huésped
    void save(Huesped huesped) throws HotelException;

    // Método para encontrar un huésped por su ID
    Optional<Huesped> findById(int id) throws HotelException;

    // Método para encontrar un huésped por su DNI
    Optional<Huesped> findByDni(String dni) throws HotelException;

    // Método para obtener todos los huéspedes
    List<Huesped> findAll() throws HotelException;

    // Método para actualizar un huésped existente
    void update(Huesped huesped) throws HotelException;

    // Método para eliminar un huésped por su ID
    void delete(int id) throws HotelException;
}
