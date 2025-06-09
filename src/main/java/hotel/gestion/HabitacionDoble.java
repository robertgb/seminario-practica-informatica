package hotel.gestion;


/**
 * Clase que representa una habitación doble en el Hotel Nova.
 * Hereda de la clase abstracta Habitacion e implementa el método calcularCostoNoche.
 * Puede tener una tarifa ligeramente ajustada o características adicionales.
 */
public class HabitacionDoble extends Habitacion {
    /**
     * Constructor para HabitacionDoble.
     * Llama al constructor de la superclase (Habitacion).
     * @param numero El número único de la habitación.
     * @param precioPorNoche El precio base por noche de la habitación doble.
     */
    public HabitacionDoble(int numero, double precioPorNoche) {
        super(numero, "Doble", precioPorNoche);
    }

    /**
     * Implementación polimórfica del método calcularCostoNoche().
     * Para una habitación doble, podría ser el precio base, o un pequeño recargo.
     * En este caso, es el precio base.
     * @return El costo por una noche en esta habitación doble.
     */
    @Override
    public double calcularCostoNoche() {
        return getPrecioPorNoche();
    }
}
