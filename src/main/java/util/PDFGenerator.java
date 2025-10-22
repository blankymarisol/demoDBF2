package util;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase utilitaria para generar reportes PDF de las tablas de JavaFX
 * Genera PDFs profesionales con encabezado formal y datos tabulares
 */
public class PDFGenerator {

    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(44, 62, 80); // #2c3e50
    private static final DeviceRgb ACCENT_COLOR = new DeviceRgb(52, 152, 219); // #3498db
    private static final DeviceRgb TABLE_HEADER_COLOR = new DeviceRgb(52, 73, 94); // #34495e

    /**
     * Genera un PDF a partir de un TableView de JavaFX
     *
     * @param tableView La tabla de la cual exportar datos
     * @param titulo Título del reporte
     * @param nombreArchivo Nombre del archivo PDF (sin extensión)
     * @return File El archivo PDF generado, o null si hubo error
     */
    public static File generarPDF(TableView<?> tableView, String titulo, String nombreArchivo) {
        try {
            // Crear el archivo PDF en el directorio de outputs
            String rutaCompleta = "/mnt/user-data/outputs/" + nombreArchivo + ".pdf";
            File archivoPdf = new File(rutaCompleta);
            archivoPdf.getParentFile().mkdirs(); // Crear directorio si no existe

            // Inicializar el documento PDF
            PdfWriter writer = new PdfWriter(rutaCompleta);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Agregar encabezado formal
            agregarEncabezado(document, titulo);

            // Agregar tabla con datos
            agregarTabla(document, tableView);

            // Agregar pie de página
            agregarPiePagina(document);

            // Cerrar documento
            document.close();

            System.out.println("✓ PDF generado exitosamente: " + rutaCompleta);
            return archivoPdf;

        } catch (Exception e) {
            System.err.println("Error al generar PDF: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Agrega un encabezado profesional al documento PDF
     */
    private static void agregarEncabezado(Document document, String titulo) {
        // Título principal de la empresa
        Paragraph empresa = new Paragraph("DevSolutionsF")
                .setFontSize(24)
                .setBold()
                .setFontColor(HEADER_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        document.add(empresa);

        // Subtítulo
        Paragraph subtitulo = new Paragraph("Sistema de Gestión de Inventario y Compras")
                .setFontSize(12)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(subtitulo);

        // Línea separadora
        document.add(crearLineaSeparadora());

        // Título del reporte
        Paragraph tituloReporte = new Paragraph(titulo)
                .setFontSize(18)
                .setBold()
                .setFontColor(ACCENT_COLOR)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginTop(10)
                .setMarginBottom(10);
        document.add(tituloReporte);

        // Información de fecha y hora
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        Paragraph fechaHora = new Paragraph("Fecha de generación: " + ahora.format(formatter))
                .setFontSize(10)
                .setFontColor(ColorConstants.DARK_GRAY)
                .setMarginBottom(20);
        document.add(fechaHora);
    }

    /**
     * Agrega la tabla con los datos del TableView al documento
     */
    private static void agregarTabla(Document document, TableView<?> tableView) {
        // Obtener columnas visibles
        ObservableList<TableColumn<?, ?>> columnas = tableView.getColumns();
        int numColumnas = columnas.size();

        // Crear tabla con el número de columnas
        Table tabla = new Table(UnitValue.createPercentArray(numColumnas));
        tabla.setWidth(UnitValue.createPercentValue(100));

        // Agregar encabezados de columna
        for (TableColumn<?, ?> columna : columnas) {
            Cell headerCell = new Cell()
                    .add(new Paragraph(columna.getText())
                            .setBold()
                            .setFontColor(ColorConstants.WHITE)
                            .setFontSize(10))
                    .setBackgroundColor(TABLE_HEADER_COLOR)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(8);
            tabla.addHeaderCell(headerCell);
        }

        // Agregar filas de datos
        ObservableList<?> items = tableView.getItems();
        boolean filaAlterna = false;

        for (Object item : items) {
            for (TableColumn<?, ?> columna : columnas) {
                Object cellData = columna.getCellData(item);
                String cellText = cellData != null ? cellData.toString() : "";

                Cell dataCell = new Cell()
                        .add(new Paragraph(cellText)
                                .setFontSize(9))
                        .setPadding(6)
                        .setTextAlignment(TextAlignment.LEFT);

                // Aplicar color alternado a las filas
                if (filaAlterna) {
                    dataCell.setBackgroundColor(new DeviceRgb(245, 245, 245));
                }

                tabla.addCell(dataCell);
            }
            filaAlterna = !filaAlterna;
        }

        document.add(tabla);

        // Agregar resumen
        Paragraph resumen = new Paragraph("Total de registros: " + items.size())
                .setFontSize(10)
                .setBold()
                .setMarginTop(15)
                .setFontColor(ACCENT_COLOR);
        document.add(resumen);
    }

    /**
     * Agrega un pie de página al documento
     */
    private static void agregarPiePagina(Document document) {
        document.add(crearLineaSeparadora());

        Paragraph pie = new Paragraph("Este reporte fue generado automáticamente por el Sistema de Gestión DevSolutionsF")
                .setFontSize(8)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10);
        document.add(pie);
    }

    /**
     * Crea una línea separadora horizontal
     */
    private static Table crearLineaSeparadora() {
        Table linea = new Table(1);
        linea.setWidth(UnitValue.createPercentValue(100));
        Cell cell = new Cell()
                .setBorder(null)
                .setBackgroundColor(ACCENT_COLOR)
                .setHeight(2)
                .setPadding(0);
        linea.addCell(cell);
        return linea;
    }

    /**
     * Genera un nombre de archivo único basado en el título y timestamp
     */
    public static String generarNombreArchivo(String titulo) {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = ahora.format(formatter);
        String tituloLimpio = titulo.replaceAll("[^a-zA-Z0-9]", "_");
        return tituloLimpio + "_" + timestamp;
    }
}