package com.examgenerator.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import com.examgenerator.model.Pregunta;
import com.examgenerator.model.PreguntaDAO;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfWriter;

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
     * Genera un archivo PDF con las preguntas del examen en formato de dos columnas
     * @param preguntas Lista de preguntas para el examen
     * @param rutaArchivo Ruta donde se guardará el archivo PDF
     * @param tituloExamen Título del examen (Tema A, Tema B, etc.)
     * @return true si el PDF se generó correctamente, false en caso contrario
     */
    private boolean generarPDF(List<Pregunta> preguntas, String rutaArchivo, String tituloExamen) {
        Document document = new Document();
        
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo));
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
            
            // Fuentes para preguntas y alternativas
            Font fontPregunta = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font fontAlternativa = FontFactory.getFont(FontFactory.HELVETICA, 10);
            
            // Configuración para dos columnas
            float documentWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
            float columnWidth = (documentWidth - 20) / 2; // 20 es el espacio entre columnas
            float leftColumnX = document.leftMargin();
            float rightColumnX = leftColumnX + columnWidth + 20;
            float yStart = writer.getVerticalPosition(true);
            float yEnd = document.bottomMargin();
            
            // Índice de la pregunta actual
            int currentQuestionIndex = 0;
            int totalPreguntas = preguntas.size();
            
            // Procesar todas las páginas necesarias
            while (currentQuestionIndex < totalPreguntas) {
                // Crear ColumnText para ambas columnas en cada página
                ColumnText leftColumn = new ColumnText(writer.getDirectContent());
                ColumnText rightColumn = new ColumnText(writer.getDirectContent());
                
                // Actualizar posición vertical para la nueva página
                yStart = writer.getVerticalPosition(true);
                
                // Configurar las columnas
                leftColumn.setSimpleColumn(leftColumnX, yEnd, leftColumnX + columnWidth, yStart);
                rightColumn.setSimpleColumn(rightColumnX, yEnd, rightColumnX + columnWidth, yStart);
                
                // Preparar preguntas para la columna izquierda
                int leftColumnStartIndex = currentQuestionIndex;
                int questionsPerColumn = (totalPreguntas - currentQuestionIndex + 1) / 2;
                if (questionsPerColumn == 0) questionsPerColumn = 1;
                
                // Añadir preguntas a la columna izquierda
                boolean leftColumnFull = false;
                int leftColumnEndIndex = leftColumnStartIndex;
                
                for (int i = leftColumnStartIndex; i < leftColumnStartIndex + questionsPerColumn && i < totalPreguntas && !leftColumnFull; i++) {
                    Pregunta pregunta = preguntas.get(i);
                    
                    // Crear contenido de la pregunta
                    Paragraph contenidoPregunta = new Paragraph();
                    
                    // Número y enunciado de la pregunta
                    contenidoPregunta.add(new Paragraph((i + 1) + ". " + pregunta.getEnunciado(), fontPregunta));
                    
                    // Alternativas
                    contenidoPregunta.add(new Paragraph("a) " + pregunta.getAlternativaA(), fontAlternativa));
                    contenidoPregunta.add(new Paragraph("b) " + pregunta.getAlternativaB(), fontAlternativa));
                    contenidoPregunta.add(new Paragraph("c) " + pregunta.getAlternativaC(), fontAlternativa));
                    contenidoPregunta.add(new Paragraph("d) " + pregunta.getAlternativaD(), fontAlternativa));
                    contenidoPregunta.add(new Paragraph("e) " + pregunta.getAlternativaE(), fontAlternativa));
                    contenidoPregunta.add(new Paragraph("\n"));
                    
                    // Añadir la pregunta a la columna izquierda y verificar si cabe
                    leftColumn.addElement(contenidoPregunta);
                    int status = leftColumn.go(true); // Simulación para ver si cabe
                    
                    if (ColumnText.hasMoreText(status)) {
                        // No cabe más contenido, la columna está llena
                        leftColumnFull = true;
                    } else {
                        // La pregunta cabe, actualizamos el índice final
                        leftColumnEndIndex = i + 1;
                    }
                }
                
                // Revertir la simulación y añadir el contenido real
                leftColumn = new ColumnText(writer.getDirectContent());
                leftColumn.setSimpleColumn(leftColumnX, yEnd, leftColumnX + columnWidth, yStart);
                
                for (int i = leftColumnStartIndex; i < leftColumnEndIndex; i++) {
                    Pregunta pregunta = preguntas.get(i);
                    
                    Paragraph contenidoPregunta = new Paragraph();
                    contenidoPregunta.add(new Paragraph((i + 1) + ". " + pregunta.getEnunciado(), fontPregunta));
                    contenidoPregunta.add(new Paragraph("a) " + pregunta.getAlternativaA(), fontAlternativa));
                    contenidoPregunta.add(new Paragraph("b) " + pregunta.getAlternativaB(), fontAlternativa));
                    contenidoPregunta.add(new Paragraph("c) " + pregunta.getAlternativaC(), fontAlternativa));
                    contenidoPregunta.add(new Paragraph("d) " + pregunta.getAlternativaD(), fontAlternativa));
                    contenidoPregunta.add(new Paragraph("e) " + pregunta.getAlternativaE(), fontAlternativa));
                    contenidoPregunta.add(new Paragraph("\n"));
                    leftColumn.addElement(contenidoPregunta);
                }
                
                // Renderizar la columna izquierda
                leftColumn.go();
                
                // Actualizar el índice actual
                currentQuestionIndex = leftColumnEndIndex;
                
                // Añadir preguntas a la columna derecha si quedan preguntas
                if (currentQuestionIndex < totalPreguntas) {
                    boolean rightColumnFull = false;
                    int rightColumnEndIndex = currentQuestionIndex;
                    
                    for (int i = currentQuestionIndex; i < totalPreguntas && !rightColumnFull; i++) {
                        Pregunta pregunta = preguntas.get(i);
                        
                        // Crear contenido de la pregunta
                        Paragraph contenidoPregunta = new Paragraph();
                        
                        // Número y enunciado de la pregunta
                        contenidoPregunta.add(new Paragraph((i + 1) + ". " + pregunta.getEnunciado(), fontPregunta));
                        
                        // Alternativas
                        contenidoPregunta.add(new Paragraph("a) " + pregunta.getAlternativaA(), fontAlternativa));
                        contenidoPregunta.add(new Paragraph("b) " + pregunta.getAlternativaB(), fontAlternativa));
                        contenidoPregunta.add(new Paragraph("c) " + pregunta.getAlternativaC(), fontAlternativa));
                        contenidoPregunta.add(new Paragraph("d) " + pregunta.getAlternativaD(), fontAlternativa));
                        contenidoPregunta.add(new Paragraph("e) " + pregunta.getAlternativaE(), fontAlternativa));
                        contenidoPregunta.add(new Paragraph("\n"));
                        
                        // Añadir la pregunta a la columna derecha y verificar si cabe
                        rightColumn.addElement(contenidoPregunta);
                        int status = rightColumn.go(true); // Simulación para ver si cabe
                        
                        if (ColumnText.hasMoreText(status)) {
                            // No cabe más contenido, la columna está llena
                            rightColumnFull = true;
                        } else {
                            // La pregunta cabe, actualizamos el índice final
                            rightColumnEndIndex = i + 1;
                        }
                    }
                    
                    // Revertir la simulación y añadir el contenido real
                    rightColumn = new ColumnText(writer.getDirectContent());
                    rightColumn.setSimpleColumn(rightColumnX, yEnd, rightColumnX + columnWidth, yStart);
                    
                    for (int i = currentQuestionIndex; i < rightColumnEndIndex; i++) {
                        Pregunta pregunta = preguntas.get(i);
                        
                        Paragraph contenidoPregunta = new Paragraph();
                        contenidoPregunta.add(new Paragraph((i + 1) + ". " + pregunta.getEnunciado(), fontPregunta));
                        contenidoPregunta.add(new Paragraph("a) " + pregunta.getAlternativaA(), fontAlternativa));
                        contenidoPregunta.add(new Paragraph("b) " + pregunta.getAlternativaB(), fontAlternativa));
                        contenidoPregunta.add(new Paragraph("c) " + pregunta.getAlternativaC(), fontAlternativa));
                        contenidoPregunta.add(new Paragraph("d) " + pregunta.getAlternativaD(), fontAlternativa));
                        contenidoPregunta.add(new Paragraph("e) " + pregunta.getAlternativaE(), fontAlternativa));
                        contenidoPregunta.add(new Paragraph("\n"));
                        rightColumn.addElement(contenidoPregunta);
                    }
                    
                    // Renderizar la columna derecha
                    rightColumn.go();
                    
                    // Actualizar el índice actual
                    currentQuestionIndex = rightColumnEndIndex;
                }
                
                // Si aún quedan preguntas por procesar, crear una nueva página
                if (currentQuestionIndex < totalPreguntas) {
                    document.newPage();
                }
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
     * Genera un archivo Word con las preguntas del examen en formato de dos columnas
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
            instrucciones.setAlignment(ParagraphAlignment.LEFT);
            instrucciones.setSpacingAfter(0); // Eliminar espacio después de las instrucciones
            XWPFRun instruccionesRun = instrucciones.createRun();
            instruccionesRun.setText("Instrucciones: Marque la alternativa correcta para cada pregunta.");
            instruccionesRun.setItalic(true);
            instruccionesRun.setFontSize(10);
            
            // Índice de la pregunta actual
            int currentQuestionIndex = 0;
            int totalPreguntas = preguntas.size();
            
            // Procesar todas las páginas necesarias
            while (currentQuestionIndex < totalPreguntas) {
                // Crear tabla para dos columnas en cada página
                XWPFTable table = document.createTable(1, 2);
                table.getCTTbl().addNewTblPr().addNewTblW().setW(BigInteger.valueOf(9500));
                table.getCTTbl().getTblPr().getTblW().setType(STTblWidth.DXA);
                
                // Quitar bordes de la tabla
                table.getCTTbl().getTblPr().unsetTblBorders();
                
                // Obtener celdas para las columnas
                XWPFTableCell leftCell = table.getRow(0).getCell(0);
                XWPFTableCell rightCell = table.getRow(0).getCell(1);
                
                // Establecer ancho de columnas
                leftCell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(4750));
                rightCell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(4750));
                
                // Calcular cuántas preguntas van en cada columna para esta página
                int preguntasRestantes = totalPreguntas - currentQuestionIndex;
                int preguntasPorColumna = (preguntasRestantes + 1) / 2; // Redondeo hacia arriba
                
                // Estimación de cuántas preguntas caben en una columna (ajustado para mejor visualización)
                int maxPreguntasPorColumna = 4; // Ajustado para equilibrar con el PDF
                if (preguntasPorColumna > maxPreguntasPorColumna) {
                    preguntasPorColumna = maxPreguntasPorColumna;
                }
                
                // Primera columna (izquierda)
                int leftColumnEndIndex = Math.min(currentQuestionIndex + preguntasPorColumna, totalPreguntas);
                
                for (int i = currentQuestionIndex; i < leftColumnEndIndex; i++) {
                    Pregunta pregunta = preguntas.get(i);
                    
                    // Número y enunciado de la pregunta
                    XWPFParagraph parrafoPregunta = leftCell.addParagraph();
                    parrafoPregunta.setSpacingAfter(0);
                    XWPFRun preguntaRun = parrafoPregunta.createRun();
                    preguntaRun.setText((i + 1) + ". " + pregunta.getEnunciado());
                    preguntaRun.setBold(true);
                    preguntaRun.setFontSize(11);
                    
                    // Alternativas con mejor espaciado
                    XWPFParagraph alternativaA = leftCell.addParagraph();
                    alternativaA.setIndentationLeft(200);
                    alternativaA.setSpacingAfter(0);
                    XWPFRun alternativaARun = alternativaA.createRun();
                    alternativaARun.setText("a) " + pregunta.getAlternativaA());
                    alternativaARun.setFontSize(10);
                    
                    XWPFParagraph alternativaB = leftCell.addParagraph();
                    alternativaB.setIndentationLeft(200);
                    alternativaB.setSpacingAfter(0);
                    XWPFRun alternativaBRun = alternativaB.createRun();
                    alternativaBRun.setText("b) " + pregunta.getAlternativaB());
                    alternativaBRun.setFontSize(10);
                    
                    XWPFParagraph alternativaC = leftCell.addParagraph();
                    alternativaC.setIndentationLeft(200);
                    alternativaC.setSpacingAfter(0);
                    XWPFRun alternativaCRun = alternativaC.createRun();
                    alternativaCRun.setText("c) " + pregunta.getAlternativaC());
                    alternativaCRun.setFontSize(10);
                    
                    XWPFParagraph alternativaD = leftCell.addParagraph();
                    alternativaD.setIndentationLeft(200);
                    alternativaD.setSpacingAfter(0);
                    XWPFRun alternativaDRun = alternativaD.createRun();
                    alternativaDRun.setText("d) " + pregunta.getAlternativaD());
                    alternativaDRun.setFontSize(10);
                    
                    XWPFParagraph alternativaE = leftCell.addParagraph();
                    alternativaE.setIndentationLeft(200);
                    alternativaE.setSpacingAfter(0);
                    XWPFRun alternativaERun = alternativaE.createRun();
                    alternativaERun.setText("e) " + pregunta.getAlternativaE());
                    alternativaERun.setFontSize(10);
                }
                
                // Actualizar el índice de la pregunta actual
                currentQuestionIndex = leftColumnEndIndex;
                
                // Segunda columna (derecha) si quedan preguntas
                if (currentQuestionIndex < totalPreguntas) {
                    int rightColumnEndIndex = Math.min(currentQuestionIndex + preguntasPorColumna, totalPreguntas);
                    
                    for (int i = currentQuestionIndex; i < rightColumnEndIndex; i++) {
                        Pregunta pregunta = preguntas.get(i);
                        
                        // Número y enunciado de la pregunta
                        XWPFParagraph parrafoPregunta = rightCell.addParagraph();
                        parrafoPregunta.setSpacingAfter(0);
                        XWPFRun preguntaRun = parrafoPregunta.createRun();
                        preguntaRun.setText((i + 1) + ". " + pregunta.getEnunciado());
                        preguntaRun.setBold(true);
                        preguntaRun.setFontSize(11);
                        
                        // Alternativas con mejor espaciado
                        XWPFParagraph alternativaA = rightCell.addParagraph();
                        alternativaA.setIndentationLeft(200);
                        alternativaA.setSpacingAfter(0);
                        XWPFRun alternativaARun = alternativaA.createRun();
                        alternativaARun.setText("a) " + pregunta.getAlternativaA());
                        alternativaARun.setFontSize(10);
                        
                        XWPFParagraph alternativaB = rightCell.addParagraph();
                        alternativaB.setIndentationLeft(200);
                        alternativaB.setSpacingAfter(0);
                        XWPFRun alternativaBRun = alternativaB.createRun();
                        alternativaBRun.setText("b) " + pregunta.getAlternativaB());
                        alternativaBRun.setFontSize(10);
                        
                        XWPFParagraph alternativaC = rightCell.addParagraph();
                        alternativaC.setIndentationLeft(200);
                        alternativaC.setSpacingAfter(0);
                        XWPFRun alternativaCRun = alternativaC.createRun();
                        alternativaCRun.setText("c) " + pregunta.getAlternativaC());
                        alternativaCRun.setFontSize(10);
                        
                        XWPFParagraph alternativaD = rightCell.addParagraph();
                        alternativaD.setIndentationLeft(200);
                        alternativaD.setSpacingAfter(0);
                        XWPFRun alternativaDRun = alternativaD.createRun();
                        alternativaDRun.setText("d) " + pregunta.getAlternativaD());
                        alternativaDRun.setFontSize(10);
                        
                        XWPFParagraph alternativaE = rightCell.addParagraph();
                        alternativaE.setIndentationLeft(200);
                        alternativaE.setSpacingAfter(0);
                        XWPFRun alternativaERun = alternativaE.createRun();
                        alternativaERun.setText("e) " + pregunta.getAlternativaE());
                        alternativaERun.setFontSize(10);
                    }
                    
                    // Actualizar el índice de la pregunta actual
                    currentQuestionIndex = rightColumnEndIndex;
                }
                
                // Agregar salto de página solo si quedan más preguntas
                if (currentQuestionIndex < totalPreguntas) {
                    XWPFParagraph pageBreak = document.createParagraph();
                    pageBreak.createRun().addBreak(org.apache.poi.xwpf.usermodel.BreakType.PAGE);
                }
            }
            
            // Guardar el documento
            FileOutputStream out = new FileOutputStream(rutaArchivo);
            document.write(out);
            out.close();
            document.close();
            return true;
            
        } catch (Exception e) {
            System.err.println("Error al generar el Word: " + e.getMessage());
            e.printStackTrace(); // Añadir stack trace para mejor diagnóstico
            try {
                document.close();
            } catch (Exception ex) {
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