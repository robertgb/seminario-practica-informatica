package hotel.gestion;

/**
 * Clase que representa una habitación simple en el Hotel Nova.
 * Hereda de la clase abstracta Habitacion e implementa el método calcularCostoNoche.
 */
public class HabitacionSimple extends Habitacion {
    /**
     * Constructor para HabitacionSimple.
     * Llama al constructor de la superclase (Habitacion).
     * @param numero El número único de la habitación.
     * @param precioPorNoche El precio base por noche de la habitación simple.
     */
    public HabitacionSimple(int numero, double precioPorNoche) {
        super(numero, "Simple", precioPorNoche);
    }

    /**
     * Implementación polimórfica del método calcularCostoNoche().
     * Para una habitación simple, el costo es simplemente el precio base por noche.
     * @return El costo por una noche en esta habitación simple.
     */
    @Override
    public double calcularCostoNoche() {
        return getPrecioPorNoche();
    }
}
