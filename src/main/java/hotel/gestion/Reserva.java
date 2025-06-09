package hotel.gestion;


import java.time.LocalDate; // Para manejar fechas de forma moderna

/**
 * Clase que representa una reserva en el Hotel Nova.
 * Almacena detalles sobre el huésped, la habitación, fechas y estado de la reserva.
 */
public class Reserva {
    private int idReservaInterno; // Nuevo: ID de la base de datos (AUTO_INCREMENT)
    private String idReserva; // Antiguo: ID externo como String, ahora puede ser usado como referencia
    private Huesped huesped;
    private Habitacion habitacion;
    private LocalDate fechaCheckin;
    private LocalDate fechaCheckout;
    private int cantidadHuespedes;
    private String estado;

    /**
     * Constructor de la clase Reserva.
     * Ahora 'idReserva' puede ser un ID temporal o un marcador hasta que la DB lo genere.
     * @param idReserva ID externo/temporal de la reserva.
     * @param huesped El huésped que realiza la reserva.
     * @param habitacion La habitación reservada.
     * @param fechaCheckin La fecha de entrada.
     * @param fechaCheckout La fecha de salida.
     * @param cantidadHuespedes El número de huéspedes.
     * @throws HotelException Si las fechas de check-in son posteriores o iguales a las de check-out.
     */
    public Reserva(String idReserva, Huesped huesped, Habitacion habitacion,
                   LocalDate fechaCheckin, LocalDate fechaCheckout, int cantidadHuespedes) throws HotelException {
        if (fechaCheckin.isAfter(fechaCheckout) || fechaCheckin.isEqual(fechaCheckout)) {
            throw new HotelException("Error de reserva: La fecha de check-out debe ser posterior a la fecha de check-in.");
        }

        this.idReserva = idReserva;
        this.huesped = huesped;
        this.habitacion = habitacion;
        this.fechaCheckin = fechaCheckin;
        this.fechaCheckout = fechaCheckout;
        this.cantidadHuespedes = cantidadHuespedes;
        this.estado = "Confirmada";
        this.idReservaInterno = 0; // ID inicial para objetos no persistidos
    }

    // Nuevo: Getter para el ID de la base de datos
    public int getIdReservaInterno() {
        return idReservaInterno;
    }

    // Nuevo: Setter para el ID de la base de datos (usado por DAO al guardar)
    public void setIdReservaInterno(int idReservaInterno) {
        this.idReservaInterno = idReservaInterno;
        // Podríamos también actualizar idReserva = String.valueOf(idReservaInterno);
    }

    public String getIdReserva() {
        return idReserva;
    }

    public Huesped getHuesped() {
        return huesped;
    }

    public Habitacion getHabitacion() {
        return habitacion;
    }

    public LocalDate getFechaCheckin() {
        return fechaCheckin;
    }

    public LocalDate getFechaCheckout() {
        return fechaCheckout;
    }

    public int getCantidadHuespedes() {
        return cantidadHuespedes;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double calcularCostoTotal() {
        long diasEstancia = java.time.temporal.ChronoUnit.DAYS.between(fechaCheckin, fechaCheckout);
        return habitacion.calcularCostoNoche() * diasEstancia;
    }

    @Override
    public String toString() {
        return "Reserva [ID_DB: " + idReservaInterno + ", ID_Ext: " + idReserva +
                ", Huésped: " + huesped.getNombre() + " " + huesped.getApellido() +
                ", Habitación: " + habitacion.getNumero() + " (" + habitacion.getTipo() + ")" +
                ", Check-in: " + fechaCheckin + ", Check-out: " + fechaCheckout +
                ", Huéspedes: " + cantidadHuespedes + ", Estado: " + estado +
                ", Costo Total: $" + String.format("%.2f", calcularCostoTotal()) + "]";
    }
}
