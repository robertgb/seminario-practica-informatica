package hotel.gestion;


/**
 * Clase que representa una habitación tipo Suite en el Hotel Nova.
 * Hereda de la clase abstracta Habitacion e implementa el método calcularCostoNoche.
 * Las suites pueden tener un recargo adicional por sus servicios o tamaño.
 */
public class HabitacionSuite extends Habitacion {
    private static final double RECARGO_SUITE = 0.20; // 20% de recargo por ser Suite

    /**
     * Constructor para HabitacionSuite.
     * Llama al constructor de la superclase (Habitacion).
     * @param numero El número único de la habitación.
     * @param precioPorNoche El precio base por noche de la suite.
     */
    public HabitacionSuite(int numero, double precioPorNoche) {
        super(numero, "Suite", precioPorNoche);
    }

    /**
     * Implementación polimórfica del método calcularCostoNoche().
     * Para una Suite, el costo es el precio base más un recargo fijo.
     * @return El costo por una noche en esta habitación suite.
     */
    @Override
    public double calcularCostoNoche() {
        return getPrecioPorNoche() * (1 + RECARGO_SUITE);
    }
}
