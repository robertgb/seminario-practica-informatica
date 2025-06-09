package hotel.config.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase de utilidad para gestionar la conexión a la base de datos MySQL.
 * Configura los parámetros de conexión y proporciona un método para obtener una conexión.
 */
public class Conexion {
    // Parámetros de conexión a la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_nova_db";
    private static final String USER = "root"; // ¡Cambia esto a tu usuario de MySQL!
    private static final String PASSWORD = ""; // ¡Cambia esto a tu contraseña de MySQL!

    /**
     * Establece y retorna una conexión a la base de datos.
     * @return Un objeto Connection si la conexión es exitosa.
     * @throws SQLException Si ocurre un error al conectar con la base de datos.
     */
    public static Connection getConnection() throws SQLException {
        // Cargar el driver JDBC (no es estrictamente necesario en JDBC 4.0+ pero es buena práctica)
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se pudo cargar el controlador JDBC de MySQL.");
            throw new SQLException("No se encontró el controlador JDBC: " + e.getMessage());
        }
        // Retornar la conexión utilizando DriverManager
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Cierra una conexión a la base de datos, si no es nula.
     * @param connection La conexión a cerrar.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
