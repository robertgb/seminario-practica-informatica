package hotel.gestion;


/**
 * Clase que representa a un huésped en el Hotel Nova.
 * Contiene información personal del huésped.
 */
public class Huesped {
    private int idHuespedInterno; // Nuevo: ID de la base de datos (AUTO_INCREMENT)
    private String idHuesped; // Antiguo: ID externo como String, ahora puede ser usado como referencia
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String telefono;

    /**
     * Constructor de la clase Huesped.
     * Ahora 'idHuesped' puede ser un ID temporal o un marcador hasta que la DB lo genere.
     * @param idHuesped ID externo/temporal del huésped.
     * @param nombre Nombre del huésped.
     * @param apellido Apellido del huésped.
     * @param dni DNI del huésped.
     * @param email Correo electrónico del huésped.
     * @param telefono Número de teléfono del huésped.
     */
    public Huesped(String idHuesped, String nombre, String apellido, String dni, String email, String telefono) {
        this.idHuesped = idHuesped;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
        this.telefono = telefono;
        this.idHuespedInterno = 0; // ID inicial para objetos no persistidos
    }

    // Nuevo: Getter para el ID de la base de datos
    public int getIdHuespedInterno() {
        return idHuespedInterno;
    }

    // Nuevo: Setter para el ID de la base de datos (usado por DAO al guardar)
    public void setIdHuespedInterno(int idHuespedInterno) {
        this.idHuespedInterno = idHuespedInterno;
        // Podríamos también actualizar idHuesped = String.valueOf(idHuespedInterno);
    }

    public String getIdHuesped() {
        return idHuesped;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDni() {
        return dni;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    @Override
    public String toString() {
        return "Huésped [ID_DB: " + idHuespedInterno + ", ID_Ext: " + idHuesped + ", Nombre: " + nombre + " " + apellido +
                ", DNI: " + dni + ", Email: " + email + ", Teléfono: " + telefono + "]";
    }
}