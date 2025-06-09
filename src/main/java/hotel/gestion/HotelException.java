package hotel.gestion;

/**
 * Excepción personalizada para el sistema Hotel Nova.
 * Utilizada para manejar errores específicos de la lógica de negocio del hotel.
 */
public class HotelException extends Exception {
    /**
     * Constructor para la excepción HotelException.
     * @param message El mensaje de error asociado a la excepción.
     */
    public HotelException(String message) {
        super(message);
    }
}
