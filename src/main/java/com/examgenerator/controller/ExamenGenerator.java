package com.examgenerator.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import com.examgenerator.model.Pregunta;
import com.examgenerator.model.PreguntaDAO;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Controlador para generar exámenes en formato PDF
 */
public class ExamenGenerator {
    private PreguntaDAO preguntaDAO;
    
    public ExamenGenerator() {
        this.preguntaDAO = new PreguntaDAO();
    }
    
    /**
     * Genera múltiples versiones de exámenes en formato PDF
     * @param cantidadTemas Número de temas diferentes a generar
     * @return Array con las rutas de los archivos PDF generados
     */
    public String[] generarExamenes(int cantidadTemas) {
        String[] rutasArchivos = new String[cantidadTemas];
        
        // Create Exámenes directory if it doesn't exist
java.io.File examDirectory = new java.io.File("Exámenes");
        if (!examDirectory.exists()) {
            examDirectory.mkdir();
        }
        
        for (int i = 0; i < cantidadTemas; i++) {
            // Generar letra del tema (A, B, C, ...)
            char letraTema = (char) ('A' + i);
            String nombreArchivo = "Exámenes" + java.io.File.separator + "Examen_Tema_" + letraTema + ".pdf";
            
            // Generar examen aleatorio
            List<Pregunta> preguntasExamen = preguntaDAO.generarExamenAleatorio();
            
            // Crear PDF
            if (generarPDF(preguntasExamen, nombreArchivo, "Tema " + letraTema)) {
                rutasArchivos[i] = nombreArchivo;
            }
        }
        
        return rutasArchivos;
    }
    
    /**
     * Genera un archivo PDF con las preguntas del examen
     * @param preguntas Lista de preguntas para el examen
     * @param rutaArchivo Ruta donde se guardará el archivo PDF
     * @param tituloExamen Título del examen (Tema A, Tema B, etc.)
     * @return true si el PDF se generó correctamente, false en caso contrario
     */
    private boolean generarPDF(List<Pregunta> preguntas, String rutaArchivo, String tituloExamen) {
        Document document = new Document();
        
        try {
            PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo));
            document.open();
            
            // Agregar portada
            agregarPortada(document, tituloExamen.charAt(tituloExamen.length() - 1));
            
            // Agregar título
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph titulo = new Paragraph("EXAMEN DE ADMISIÓN - " + tituloExamen, fontTitulo);
            titulo.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(titulo);
            document.add(new Paragraph("\n"));
            
            // Agregar instrucciones
            Font fontInstrucciones = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC);
            Paragraph instrucciones = new Paragraph("Instrucciones: Marque la alternativa correcta para cada pregunta.", fontInstrucciones);
            document.add(instrucciones);
            document.add(new Paragraph("\n"));
            
            // Agregar preguntas
            Font fontPregunta = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font fontAlternativa = FontFactory.getFont(FontFactory.HELVETICA, 10);
            
            for (int i = 0; i < preguntas.size(); i++) {
                Pregunta pregunta = preguntas.get(i);
                
                // Número y enunciado de la pregunta
                Paragraph parrafoPregunta = new Paragraph((i + 1) + ". " + pregunta.getEnunciado(), fontPregunta);
                document.add(parrafoPregunta);
                
                // Alternativas
                document.add(new Paragraph("a) " + pregunta.getAlternativaA(), fontAlternativa));
                document.add(new Paragraph("b) " + pregunta.getAlternativaB(), fontAlternativa));
                document.add(new Paragraph("c) " + pregunta.getAlternativaC(), fontAlternativa));
                document.add(new Paragraph("d) " + pregunta.getAlternativaD(), fontAlternativa));
                document.add(new Paragraph("e) " + pregunta.getAlternativaE(), fontAlternativa));
                
                document.add(new Paragraph("\n"));
            }
            
            document.close();
            return true;
            
        } catch (DocumentException | IOException e) {
            System.err.println("Error al generar el PDF: " + e.getMessage());
            if (document.isOpen()) {
                document.close();
            }
            return false;
        }
    }
    
    /**
     * Agrega una portada al documento PDF
     * @param document Documento PDF
     * @param letraTema Letra del tema (A, B, C, ...)
     * @throws DocumentException Si hay un error al agregar elementos al documento
     * @throws IOException Si hay un error al cargar imágenes
     */
    private void agregarPortada(Document document, char letraTema) throws DocumentException, IOException {
        // Obtener el año actual
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        
        // Configurar fuentes
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);
        Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 14);
        
        // Agregar nombre de la universidad
        Paragraph universidad = new Paragraph("UNIVERSIDAD NACIONAL \"SAN LUIS GONZAGA\"", fontTitulo);
        universidad.setAlignment(Element.ALIGN_CENTER);
        document.add(universidad);
        
        // Agregar título del examen
        Paragraph examen = new Paragraph("EXAMEN DE ADMISIÓN", fontTitulo);
        examen.setAlignment(Element.ALIGN_CENTER);
        examen.setSpacingBefore(10);
        document.add(examen);
        
        // Agregar año (dinámico)
        Paragraph anio = new Paragraph(String.valueOf(anioActual), fontTitulo);
        anio.setAlignment(Element.ALIGN_CENTER);
        anio.setSpacingBefore(10);
        document.add(anio);
        
        // Agregar logo de la universidad debajo del título
        Image logo = Image.getInstance(getClass().getClassLoader().getResource("images/logo.png"));
        logo.scaleToFit(300, 300);
        logo.setAlignment(Element.ALIGN_CENTER);
        logo.setSpacingBefore(20);
        document.add(logo);
        
        
        // Agregar modalidad
        Paragraph modalidad = new Paragraph("MODALIDAD", fontSubtitulo);
        modalidad.setAlignment(Element.ALIGN_CENTER);
        modalidad.setSpacingBefore(40);
        document.add(modalidad);
        
        Paragraph ordinario = new Paragraph("ORDINARIO", fontSubtitulo);
        ordinario.setAlignment(Element.ALIGN_CENTER);
        document.add(ordinario);
        
        // Agregar tema
        Paragraph tema = new Paragraph("TEMA:", fontSubtitulo);
        tema.setAlignment(Element.ALIGN_CENTER);
        tema.setSpacingBefore(40);
        document.add(tema);
        
        // Crear círculo con letra del tema
        Paragraph circuloTema = new Paragraph("(" + letraTema + ")", fontTitulo);
        circuloTema.setAlignment(Element.ALIGN_CENTER);
        document.add(circuloTema);
        
        // Agregar pie de página
        Paragraph pie = new Paragraph("UNIVERSIDAD LICENCIADA POR SUNEDU", fontNormal);
        pie.setAlignment(Element.ALIGN_CENTER);
        pie.setSpacingBefore(60);
        document.add(pie);
        
        // Agregar nueva página para el contenido del examen
        document.newPage();
    }
}