package com.examgenerator.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clase de acceso a datos para las preguntas del examen
 */
public class PreguntaDAO {
    
    /**
     * Obtiene todas las preguntas de la base de datos
     * @return Lista de preguntas
     */
    public List<Pregunta> obtenerTodasLasPreguntas() {
        List<Pregunta> preguntas = new ArrayList<>();
        String sql = "SELECT id, enunciado, alternativa_a, alternativa_b, alternativa_c, alternativa_d, alternativa_e FROM preguntas";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Pregunta pregunta = new Pregunta(
                    rs.getInt("id"),
                    rs.getString("enunciado"),
                    rs.getString("alternativa_a"),
                    rs.getString("alternativa_b"),
                    rs.getString("alternativa_c"),
                    rs.getString("alternativa_d"),
                    rs.getString("alternativa_e")
                );
                preguntas.add(pregunta);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener preguntas: " + e.getMessage());
        }
        
        return preguntas;
    }
    
    /**
     * Obtiene una lista aleatoria de preguntas con sus alternativas en orden aleatorio
     * @return Lista de preguntas con orden aleatorio
     */
    public List<Pregunta> obtenerPreguntasAleatorias() {
        List<Pregunta> preguntas = obtenerTodasLasPreguntas();
        
        // Mezclar el orden de las preguntas
        Collections.shuffle(preguntas);
        
        return preguntas;
    }
    
    /**
     * Reorganiza aleatoriamente las alternativas de una pregunta
     * @param pregunta La pregunta cuyas alternativas se reorganizarán
     * @return Una nueva pregunta con las alternativas reorganizadas
     */
    public Pregunta reorganizarAlternativas(Pregunta pregunta) {
        // Crear una lista con las alternativas
        List<String> alternativas = new ArrayList<>();
        alternativas.add(pregunta.getAlternativaA());
        alternativas.add(pregunta.getAlternativaB());
        alternativas.add(pregunta.getAlternativaC());
        alternativas.add(pregunta.getAlternativaD());
        alternativas.add(pregunta.getAlternativaE());
        
        // Mezclar las alternativas
        Collections.shuffle(alternativas);
        
        // Crear una nueva pregunta con las alternativas reorganizadas
        return new Pregunta(
            pregunta.getId(),
            pregunta.getEnunciado(),
            alternativas.get(0),
            alternativas.get(1),
            alternativas.get(2),
            alternativas.get(3),
            alternativas.get(4)
        );
    }
    
    /**
     * Genera un conjunto de preguntas con alternativas reorganizadas para un tema específico
     * @return Lista de preguntas con alternativas reorganizadas
     */
    public List<Pregunta> generarExamenAleatorio() {
        List<Pregunta> preguntasOriginales = obtenerPreguntasAleatorias();
        List<Pregunta> preguntasReorganizadas = new ArrayList<>();
        
        for (Pregunta pregunta : preguntasOriginales) {
            preguntasReorganizadas.add(reorganizarAlternativas(pregunta));
        }
        
        return preguntasReorganizadas;
    }
}