package com.examgenerator.view;

import com.examgenerator.controller.ExamenGenerator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

/**
 * Interfaz gráfica para el generador de exámenes
 */
public class ExamenGeneratorGUI extends JFrame {
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JLabel cantidadTemasLabel;
    private JSpinner cantidadTemasSpinner;
    private JButton verExamenesButton;
    private JButton generarButton;
    private JTextArea resultadoTextArea;
    private JScrollPane scrollPane;
    
    private ExamenGenerator examenGenerator;
    
    public ExamenGeneratorGUI() {
        // Inicializar el controlador
        examenGenerator = new ExamenGenerator();
        
        // Configurar la ventana
        setTitle("Generador de Exámenes de Admisión");
        try {
            URL iconUrl = getClass().getClassLoader().getResource("images/logo.png");
            if (iconUrl != null) {
                setIconImage(new ImageIcon(iconUrl).getImage());
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se pudo cargar el ícono de la aplicación",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar el ícono: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
        setSize(1280, 720);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Inicializar componentes
        initComponents();
        
        // Ya no mostramos la ventana aquí, se mostrará después de cerrar el splash screen
        // setVisible(true);
    }
    
    private void initComponents() {
        // Panel principal con split layout
        mainPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Panel izquierdo
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        leftPanel.setBackground(new Color(252, 243, 234));
        
        // Logo banner en la parte superior
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoPanel.setBackground(new Color(252, 243, 234));
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        try {
            URL logoUrl = getClass().getClassLoader().getResource("images/banner.png");
            if (logoUrl != null) {
                ImageIcon logoIcon = new ImageIcon(logoUrl);
                Image logoImage = logoIcon.getImage();
                Image scaledLogo = logoImage.getScaledInstance(400, -1, Image.SCALE_SMOOTH);
                JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
                logoPanel.add(logoLabel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Generador titulo
        JLabel generadorLabel = new JLabel("GENERADOR DE TEMAS");
        generadorLabel.setFont(new Font("Arial", Font.BOLD, 36));
        generadorLabel.setForeground(Color.RED);
        generadorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel admisionLabel = new JLabel("PARA EXÁMEN DE ADMISIÓN");
        admisionLabel.setFont(new Font("Arial", Font.BOLD, 36));
        admisionLabel.setForeground(Color.RED);
        admisionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Panel de configuración
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
        configPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        configPanel.setBackground(Color.WHITE);
        
        cantidadTemasLabel = new JLabel("CANTIDAD DE TEMAS A GENERAR:");
        cantidadTemasLabel.setFont(new Font("Arial", Font.BOLD, 24));
        cantidadTemasLabel.setForeground(Color.RED);
        cantidadTemasLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cantidadTemasSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 100, 1));
        cantidadTemasSpinner.setFont(new Font("Arial", Font.BOLD, 24));
        cantidadTemasSpinner.setMaximumSize(new Dimension(70, 30));
        cantidadTemasSpinner.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Botones
        verExamenesButton = new JButton("VER EXÁMENES") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 102, 0));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        verExamenesButton.setForeground(Color.WHITE);
        verExamenesButton.setFont(new Font("Arial", Font.BOLD, 24));
        verExamenesButton.setMaximumSize(new Dimension(300, 40));
        verExamenesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        verExamenesButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        verExamenesButton.setContentAreaFilled(false);
        verExamenesButton.setBorderPainted(false);
        verExamenesButton.setFocusPainted(false);

        generarButton = new JButton("GENERAR EXÁMENES") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.RED);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        generarButton.setForeground(Color.WHITE);
        generarButton.setFont(new Font("Arial", Font.BOLD, 24));
        generarButton.setMaximumSize(new Dimension(300, 40));
        generarButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        generarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        generarButton.setContentAreaFilled(false);
        generarButton.setBorderPainted(false);
        generarButton.setFocusPainted(false);
        generarButton.putClientProperty("JButton.buttonType", "square");
        
        // Footer
        JLabel footerLabel = new JLabel("DESARROLLADO POR: III CICLO \"A\" 2024-I");
        footerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        footerLabel.setForeground(Color.RED);
        footerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Agregar componentes al panel izquierdo con espaciado
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(logoPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        leftPanel.add(generadorLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(admisionLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        leftPanel.add(cantidadTemasLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(cantidadTemasSpinner);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        leftPanel.add(verExamenesButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(generarButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        leftPanel.add(footerLabel);
        leftPanel.add(Box.createVerticalGlue());
        
        // Panel derecho con imagen
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        
        try {
            // Cargar y mostrar la imagen de la universidad
            URL imageUrl = getClass().getClassLoader().getResource("images/hero.jpg");
            if (imageUrl != null) {
                ImageIcon imageIcon = new ImageIcon(imageUrl);
                Image image = imageIcon.getImage();
                Dimension size = rightPanel.getSize();
                if (size.width == 0) size.width = getWidth() / 2;
                if (size.height == 0) size.height = getHeight();
                Image scaledImage = image.getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                rightPanel.add(imageLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Agregar paneles al panel principal
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        
        // Agregar panel principal a la ventana
        add(mainPanel);
        
        // Configurar eventos
        generarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarExamenes();
            }
        });
        
        verExamenesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File examDirectory = new File("Exámenes");
                if (!examDirectory.exists()) {
                    JOptionPane.showMessageDialog(ExamenGeneratorGUI.this,
                        "La carpeta de exámenes aún no existe. Genere algunos exámenes primero.",
                        "Carpeta no encontrada",
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                try {
                    Desktop.getDesktop().open(examDirectory);
                } catch (java.io.IOException ex) {
                    JOptionPane.showMessageDialog(ExamenGeneratorGUI.this,
                        "Error al abrir la carpeta: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    
    private void generarExamenes() {
        // Obtener la cantidad de temas a generar
        int cantidadTemas = (Integer) cantidadTemasSpinner.getValue();
        
        // Deshabilitar el botón mientras se generan los exámenes
        generarButton.setEnabled(false);
        
        // Crear un hilo para no bloquear la interfaz durante la generación
        SwingWorker<String[], Void> worker = new SwingWorker<String[], Void>() {
            @Override
            protected String[] doInBackground() throws Exception {
                // Generar los exámenes
                return examenGenerator.generarExamenes(cantidadTemas);
            }
            
            @Override
            protected void done() {
                try {
                    String[] rutasArchivos = get();
                    StringBuilder mensaje = new StringBuilder();
                    mensaje.append("Exámenes generados correctamente:\n\n");
                    
                    for (int i = 0; i < rutasArchivos.length; i++) {
                        if (rutasArchivos[i] != null) {
                            mensaje.append("Tema ").append((char) ('A' + i))
                                    .append(" generado.\n");
                        }
                    }
                    
                    JOptionPane.showMessageDialog(ExamenGeneratorGUI.this,
                        mensaje.toString(),
                        "Generación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ExamenGeneratorGUI.this,
                        "Error al generar los exámenes: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                } finally {
                    // Habilitar el botón nuevamente
                    generarButton.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Mostrar pantalla de carga
        final SplashScreen splash = new SplashScreen(3000);
        splash.showSplash();
        
        // Iniciar la aplicación en segundo plano
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Crear la ventana principal
                ExamenGeneratorGUI mainWindow = new ExamenGeneratorGUI();
                
                // Cerrar la pantalla de carga y mostrar la ventana principal
                splash.closeSplash();
                mainWindow.setVisible(true);
            }
        });
    }
}