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

// Imports para Word
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import java.io.FileInputStream;

/**
 * Controlador para generar exámenes en formato PDF y Word
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
    public String[] generarExamenesPDF(int cantidadTemas) {
        String[] rutasArchivos = new String[cantidadTemas];
        
        // Crea la carpeta Exámenes si no existe
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
     * Genera múltiples versiones de exámenes en formato Word
     * @param cantidadTemas Número de temas diferentes a generar
     * @return Array con las rutas de los archivos Word generados
     */
    public String[] generarExamenesWord(int cantidadTemas) {
        String[] rutasArchivos = new String[cantidadTemas];
        
        // Crea la carpeta Exámenes si no existe
        java.io.File examDirectory = new java.io.File("Exámenes");
        if (!examDirectory.exists()) {
            examDirectory.mkdir();
        }
        
        for (int i = 0; i < cantidadTemas; i++) {
            // Generar letra del tema (A, B, C, ...)
            char letraTema = (char) ('A' + i);
            String nombreArchivo = "Exámenes" + java.io.File.separator + "Examen_Tema_" + letraTema + ".docx";
            
            // Generar examen aleatorio
            List<Pregunta> preguntasExamen = preguntaDAO.generarExamenAleatorio();
            
            // Crear Word
            if (generarWord(preguntasExamen, nombreArchivo, "Tema " + letraTema)) {
                rutasArchivos[i] = nombreArchivo;
            }
        }
        
        return rutasArchivos;
    }
    
    /**
     * Genera múltiples versiones de exámenes en el formato especificado
     * @param cantidadTemas Número de temas diferentes a generar
     * @param formatoPDF true para generar en PDF, false para generar en Word
     * @return Array con las rutas de los archivos generados
     */
    public String[] generarExamenes(int cantidadTemas, boolean formatoPDF) {
        if (formatoPDF) {
            return generarExamenesPDF(cantidadTemas);
        } else {
            return generarExamenesWord(cantidadTemas);
        }
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
    
    /**
     * Genera un archivo Word con las preguntas del examen
     * @param preguntas Lista de preguntas para el examen
     * @param rutaArchivo Ruta donde se guardará el archivo Word
     * @param tituloExamen Título del examen (Tema A, Tema B, etc.)
     * @return true si el Word se generó correctamente, false en caso contrario
     */
    private boolean generarWord(List<Pregunta> preguntas, String rutaArchivo, String tituloExamen) {
        XWPFDocument document = new XWPFDocument();
        
        try {
            // Agregar portada al documento Word
            agregarPortadaWord(document, tituloExamen.charAt(tituloExamen.length() - 1));
            
            // Agregar título
            XWPFParagraph titulo = document.createParagraph();
            titulo.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun tituloRun = titulo.createRun();
            tituloRun.setText("EXAMEN DE ADMISIÓN - " + tituloExamen);
            tituloRun.setBold(true);
            tituloRun.setFontSize(16);
            tituloRun.addBreak();
            
            // Agregar instrucciones
            XWPFParagraph instrucciones = document.createParagraph();
            XWPFRun instruccionesRun = instrucciones.createRun();
            instruccionesRun.setText("Instrucciones: Marque la alternativa correcta para cada pregunta.");
            instruccionesRun.setItalic(true);
            instruccionesRun.setFontSize(10);
            instruccionesRun.addBreak();
            instruccionesRun.addBreak();
            
            // Agregar preguntas
            for (int i = 0; i < preguntas.size(); i++) {
                Pregunta pregunta = preguntas.get(i);
                
                // Número y enunciado de la pregunta
                XWPFParagraph parrafoPregunta = document.createParagraph();
                XWPFRun preguntaRun = parrafoPregunta.createRun();
                preguntaRun.setText((i + 1) + ". " + pregunta.getEnunciado());
                preguntaRun.setFontSize(11);
                preguntaRun.addBreak();
                
                // Alternativas
                XWPFParagraph alternativaA = document.createParagraph();
                XWPFRun alternativaARun = alternativaA.createRun();
                alternativaARun.setText("a) " + pregunta.getAlternativaA());
                alternativaARun.setFontSize(10);
                
                XWPFParagraph alternativaB = document.createParagraph();
                XWPFRun alternativaBRun = alternativaB.createRun();
                alternativaBRun.setText("b) " + pregunta.getAlternativaB());
                alternativaBRun.setFontSize(10);
                
                XWPFParagraph alternativaC = document.createParagraph();
                XWPFRun alternativaCRun = alternativaC.createRun();
                alternativaCRun.setText("c) " + pregunta.getAlternativaC());
                alternativaCRun.setFontSize(10);
                
                XWPFParagraph alternativaD = document.createParagraph();
                XWPFRun alternativaDRun = alternativaD.createRun();
                alternativaDRun.setText("d) " + pregunta.getAlternativaD());
                alternativaDRun.setFontSize(10);
                
                XWPFParagraph alternativaE = document.createParagraph();
                XWPFRun alternativaERun = alternativaE.createRun();
                alternativaERun.setText("e) " + pregunta.getAlternativaE());
                alternativaERun.setFontSize(10);
                alternativaERun.addBreak();
            }
            
            // Guardar el documento
            FileOutputStream out = new FileOutputStream(rutaArchivo);
            document.write(out);
            out.close();
            document.close();
            return true;
            
        } catch (Exception e) {
            System.err.println("Error al generar el Word: " + e.getMessage());
            try {
                document.close();
            } catch (IOException ex) {
                // Ignorar
            }
            return false;
        }
    }
    
    /**
     * Agrega una portada al documento Word
     * @param document Documento Word
     * @param letraTema Letra del tema (A, B, C, ...)
     * @throws Exception Si hay un error al agregar elementos al documento
     */
    private void agregarPortadaWord(XWPFDocument document, char letraTema) throws Exception {
        // Obtener el año actual
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        
        // Agregar nombre de la universidad
        XWPFParagraph universidad = document.createParagraph();
        universidad.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun universidadRun = universidad.createRun();
        universidadRun.setText("UNIVERSIDAD NACIONAL \"SAN LUIS GONZAGA\"");
        universidadRun.setBold(true);
        universidadRun.setFontSize(24);
        
        // Agregar título del examen
        XWPFParagraph examen = document.createParagraph();
        examen.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun examenRun = examen.createRun();
        examenRun.setText("EXAMEN DE ADMISIÓN");
        examenRun.setBold(true);
        examenRun.setFontSize(24);
        
        // Agregar año (dinámico)
        XWPFParagraph anio = document.createParagraph();
        anio.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun anioRun = anio.createRun();
        anioRun.setText(String.valueOf(anioActual));
        anioRun.setBold(true);
        anioRun.setFontSize(24);
        
        // Agregar logo de la universidad
        XWPFParagraph logoParagraph = document.createParagraph();
        logoParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun logoRun = logoParagraph.createRun();
        
        // Cargar el logo directamente como un stream desde los recursos
        java.io.InputStream logoStream = getClass().getClassLoader().getResourceAsStream("images/logo.png");
        if (logoStream != null) {
            logoRun.addPicture(logoStream, XWPFDocument.PICTURE_TYPE_PNG, "logo.png", Units.toEMU(300), Units.toEMU(300));
            logoStream.close();
        } else {
            System.err.println("No se pudo cargar el logo desde los recursos");
        }
        
        // Agregar modalidad
        XWPFParagraph modalidad = document.createParagraph();
        modalidad.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun modalidadRun = modalidad.createRun();
        modalidadRun.setText("MODALIDAD");
        modalidadRun.setBold(true);
        modalidadRun.setFontSize(18);
        
        XWPFParagraph ordinario = document.createParagraph();
        ordinario.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun ordinarioRun = ordinario.createRun();
        ordinarioRun.setText("ORDINARIO");
        ordinarioRun.setBold(true);
        ordinarioRun.setFontSize(18);
        
        // Agregar tema
        XWPFParagraph tema = document.createParagraph();
        tema.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun temaRun = tema.createRun();
        temaRun.setText("TEMA:");
        temaRun.setBold(true);
        temaRun.setFontSize(18);
        
        // Crear círculo con letra del tema
        XWPFParagraph circuloTema = document.createParagraph();
        circuloTema.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun circuloTemaRun = circuloTema.createRun();
        circuloTemaRun.setText("(" + letraTema + ")");
        circuloTemaRun.setBold(true);
        circuloTemaRun.setFontSize(24);
        
        // Agregar pie de página
        XWPFParagraph pie = document.createParagraph();
        pie.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun pieRun = pie.createRun();
        pieRun.setText("UNIVERSIDAD LICENCIADA POR SUNEDU");
        pieRun.setFontSize(14);
        
        // Agregar nueva página para el contenido del examen
        document.createParagraph().createRun().addBreak(org.apache.poi.xwpf.usermodel.BreakType.PAGE);
    }
}