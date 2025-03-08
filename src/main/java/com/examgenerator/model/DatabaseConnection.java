package com.examgenerator.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria para manejar la conexión a la base de datos MySQL
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/examen_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static Connection connection;
    
    /**
     * Obtiene una conexión a la base de datos
     * @return Objeto Connection
     * @throws SQLException si ocurre un error al conectar
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Cargar el driver de MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Establecer la conexión
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("No se encontró el driver de MySQL", e);
            }
        }
        return connection;
    }
    
    /**
     * Cierra la conexión a la base de datos
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}