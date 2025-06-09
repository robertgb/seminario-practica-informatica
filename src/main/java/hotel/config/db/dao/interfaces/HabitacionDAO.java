package hotel.config.db.dao.interfaces;

import hotel.gestion.Habitacion;
import hotel.gestion.HotelException;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el Data Access Object (DAO) de la entidad Habitacion.
 * Define las operaciones CRUD (Crear, Leer, Actualizar, Borrar) para las habitaciones.
 */
public interface HabitacionDAO {
    // Método para guardar una nueva habitación en la base de datos
    void save(Habitacion habitacion) throws HotelException;

    // Método para encontrar una habitación por su ID
    Optional<Habitacion> findById(int id) throws HotelException;

    // Método para encontrar una habitación por su número de habitación
    Optional<Habitacion> findByNumero(int numero) throws HotelException;

    // Método para obtener todas las habitaciones
    List<Habitacion> findAll() throws HotelException;

    // Método para actualizar una habitación existente
    void update(Habitacion habitacion) throws HotelException;

    // Método para eliminar una habitación por su ID
    void delete(int id) throws HotelException;
}
