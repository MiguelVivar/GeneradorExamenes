package com.examgenerator.model;

import java.io.Serializable;

/**
 * Clase modelo que representa una pregunta del examen con sus alternativas
 */
public class Pregunta implements Serializable {
    private int id;
    private String enunciado;
    private String alternativaA;
    private String alternativaB;
    private String alternativaC;
    private String alternativaD;
    private String alternativaE;
    
    public Pregunta() {
    }
    
    public Pregunta(int id, String enunciado, String alternativaA, String alternativaB, String alternativaC, String alternativaD, String alternativaE) {
        this.id = id;
        this.enunciado = enunciado;
        this.alternativaA = alternativaA;
        this.alternativaB = alternativaB;
        this.alternativaC = alternativaC;
        this.alternativaD = alternativaD;
        this.alternativaE = alternativaE;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getEnunciado() {
        return enunciado;
    }
    
    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }
    
    public String getAlternativaA() {
        return alternativaA;
    }
    
    public void setAlternativaA(String alternativaA) {
        this.alternativaA = alternativaA;
    }
    
    public String getAlternativaB() {
        return alternativaB;
    }
    
    public void setAlternativaB(String alternativaB) {
        this.alternativaB = alternativaB;
    }
    
    public String getAlternativaC() {
        return alternativaC;
    }
    
    public void setAlternativaC(String alternativaC) {
        this.alternativaC = alternativaC;
    }
    
    public String getAlternativaD() {
        return alternativaD;
    }
    
    public void setAlternativaD(String alternativaD) {
        this.alternativaD = alternativaD;
    }
    
    public String getAlternativaE() {
        return alternativaE;
    }
    
    public void setAlternativaE(String alternativaE) {
        this.alternativaE = alternativaE;
    }
    
    @Override
    public String toString() {
        return "Pregunta{" + "id=" + id + ", enunciado=" + enunciado + "}";
    }
}