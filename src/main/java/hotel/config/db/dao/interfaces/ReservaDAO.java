package hotel.config.db.dao.interfaces;


import hotel.gestion.HotelException;
import hotel.gestion.Reserva;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el Data Access Object (DAO) de la entidad Reserva.
 * Define las operaciones CRUD para las reservas.
 */
public interface ReservaDAO {
    // Método para guardar una nueva reserva
    void save(Reserva reserva) throws HotelException;

    // Método para encontrar una reserva por su ID
    Optional<Reserva> findById(int id) throws HotelException;

    // Método para obtener todas las reservas
    List<Reserva> findAll() throws HotelException;

    // Método para actualizar una reserva existente
    void update(Reserva reserva) throws HotelException;

    // Método para eliminar una reserva por su ID
    void delete(int id) throws HotelException;
}
