package hotel.gestion;

public abstract class Habitacion {
    private int dbId; // Nuevo: ID de la base de datos (AUTO_INCREMENT)
    protected int numero; // Número de la habitación
    protected String tipo; // Tipo de habitación (ej: Simple, Doble, Suite)
    protected double precioPorNoche; // Precio base por noche
    protected String estado; // Estado de la habitación (ej: Disponible, Ocupada, En Limpieza, Mantenimiento)

    /**
     * Constructor de la clase Habitacion.
     * @param numero El número único de la habitación.
     * @param tipo El tipo de la habitación.
     * @param precioPorNoche El precio base por noche de la habitación.
     */
    public Habitacion(int numero, String tipo, double precioPorNoche) {
        this.numero = numero;
        this.tipo = tipo;
        this.precioPorNoche = precioPorNoche;
        this.estado = "Disponible"; // Estado inicial por defecto
        this.dbId = 0; // ID inicial para objetos no persistidos
    }

    // Nuevo: Getter para el ID de la base de datos
    public int getDbId() {
        return dbId;
    }

    // Nuevo: Setter para el ID de la base de datos (usado por DAO al guardar)
    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public int getNumero() {
        return numero;
    }

    public String getTipo() {
        return tipo;
    }

    public double getPrecioPorNoche() {
        return precioPorNoche;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public abstract double calcularCostoNoche();

    @Override
    public String toString() {
        return "Habitación [ID_DB: " + dbId + ", Número: " + numero + ", Tipo: " + tipo +
                ", Precio/Noche: $" + String.format("%.2f", precioPorNoche) +
                ", Estado: " + estado + "]";
    }
}
