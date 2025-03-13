package com.examgenerator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

/**
 * Pantalla de carga que se muestra al iniciar la aplicación
 */
public class SplashScreen extends JWindow {
    private JProgressBar progressBar;
    private int duration;
    
    /**
     * Constructor de la pantalla de carga
     * @param duration Duración en milisegundos de la animación
     */
    public SplashScreen(int duration) {
        this.duration = duration;
        createSplash();
    }
    
    /**
     * Crea los componentes de la pantalla de carga
     */
    private void createSplash() {
        // Panel principal con fondo blanco
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        
        // Panel central con logo y título
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        
        // Agregar logo
        try {
            URL logoUrl = getClass().getClassLoader().getResource("images/logo.png");
            if (logoUrl != null) {
                ImageIcon logoIcon = new ImageIcon(logoUrl);
                Image logoImage = logoIcon.getImage();
                Image scaledLogo = logoImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
                logoLabel.setHorizontalAlignment(JLabel.CENTER);
                centerPanel.add(logoLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Agregar título
        JLabel titleLabel = new JLabel("GENERADOR DE EXÁMENES DE ADMISIÓN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.RED);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        centerPanel.add(titleLabel, BorderLayout.SOUTH);
        
        // Agregar barra de progreso
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(Color.RED);
        progressBar.setString("Cargando...");
        
        // Agregar componentes al panel principal
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);
        
        // Configurar ventana
        getContentPane().add(panel);
        pack();
        setSize(400, 300);
        setLocationRelativeTo(null);
    }
    
    /**
     * Muestra la pantalla de carga y simula el progreso
     */
    public void showSplash() {
        setVisible(true);
        
        // Simular carga con incrementos de progreso
        int increment = 100 / (duration / 100);
        for (int i = 0; i <= 100; i += increment) {
            try {
                progressBar.setValue(i);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // Asegurar que la barra llegue al 100%
        progressBar.setValue(100);
    }
    
    /**
     * Cierra la pantalla de carga
     */
    public void closeSplash() {
        setVisible(false);
        dispose();
    }
}