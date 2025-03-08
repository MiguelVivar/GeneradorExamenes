package com.examgenerator.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase utilitaria para manejar la conexión a la base de datos MySQL
 */
public class DatabaseConnection {
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
                
                // Obtener credenciales de la configuración
                Properties props = new Properties();
                try (java.io.InputStream input = DatabaseConnection.class.getClassLoader()
                        .getResourceAsStream("application.properties")) {
                    if (input == null) {
                        throw new IOException("No se pudo encontrar application.properties");
                    }
                    props.load(input);
                } catch (IOException e) {
                    throw new SQLException("Error al cargar la configuración de la base de datos: " + e.getMessage(), e);
                }
                
                String url = props.getProperty("db.url");
                String user = props.getProperty("db.username");
                String password = props.getProperty("db.password");
                
                if (url == null || user == null || password == null) {
                    throw new SQLException("Faltan credenciales de la base de datos en application.properties");
                }
                
                // Establecer la conexión
                connection = DriverManager.getConnection(url, user, password);
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