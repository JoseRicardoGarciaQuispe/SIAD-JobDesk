package gob.presidencia.siad.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import gob.presidencia.siad.exception.ValidacionPersonalizadaException;
import gob.presidencia.siad.model.Reporte;

@Component
public class ExportarUtil {

	private static final Logger logger = LoggerFactory.getLogger(ExportarUtil.class);
	private static SimpleDateFormat simpleDateFormatOUT = new SimpleDateFormat("dd/MM/yyyy");
	private static SimpleDateFormat simpleDateFormatIN = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public byte[] exportarExcel(List<Map<String, Object>> list, Reporte reporte) throws Exception {
		try {
			if(list==null || list.isEmpty()) throw new ValidacionPersonalizadaException("No se encontraron registros para exportar");
			Map<Integer,String> headers = getHeaders(list.get(0));
			if(headers==null || headers.isEmpty()) throw new ValidacionPersonalizadaException("No se encontraron cabeceras en el reporte");
			
			XSSFWorkbook workbook = new XSSFWorkbook();
	        CreationHelper createHelper = workbook.getCreationHelper();
	        Sheet sheet = workbook.createSheet("Reporte");
	        
	        setTitle(sheet, workbook);
	        int rowIndex = setDetails(sheet, workbook, reporte);
	        
	        Row rowHeader = sheet.createRow(rowIndex);
	        
	        for(int cellHeader=0;cellHeader<headers.size();cellHeader++) {
	        	Cell cell = rowHeader.createCell(cellHeader);
	         	String header = headers.get(cellHeader);
	            if(header != null) cell.setCellValue(createHelper.createRichTextString(header));
	            else cell.setCellValue("");
	            cell.setCellStyle(getStyleHeader(workbook));
	        }

	        rowIndex += 1;
			int numeroRenglon = (rowIndex);
	        for(Map<String, Object> object : list) {
	        	Row row = sheet.createRow(numeroRenglon++);
	        	for(int numeroColumna=0;numeroColumna<headers.size();numeroColumna++) {
	        		Cell cell = row.createCell(numeroColumna);
	            	String header = headers.get(numeroColumna);
	                if(object != null) {
	                	Object cellValue = object.get(header);
	                	if(cellValue==null) {
	                		cell.setCellValue("");
	                	}else {
	                		if(cellValue instanceof String) {
	                			cell.setCellValue(createHelper.createRichTextString(String.valueOf(cellValue)));
	                		}else if(cellValue instanceof BigDecimal) {
	                			BigDecimal bigDecimal = new BigDecimal(String.valueOf(cellValue));
	                	        BigDecimal resultado = bigDecimal.setScale(2, RoundingMode.HALF_UP);
	                			cell.setCellValue(createHelper.createRichTextString(resultado.toString()));
	                		}else if(cellValue instanceof Date) {
	                			Date fechainput = simpleDateFormatIN.parse(String.valueOf(cellValue));
	                			String fechaFormateada = simpleDateFormatOUT.format(fechainput);
	                			cell.setCellValue(createHelper.createRichTextString(fechaFormateada));
	                		}
	                	}
	                	
	                }else {
	                	cell.setCellValue("");
	                }
	        	}
			}
	        
	        for(int numeroColumna=0;numeroColumna<headers.size();numeroColumna++) {
	        	String header = headers.get(numeroColumna);
	        	Map<String, Object> object = list.get(0);
		        if(object != null) {
	            	Object cellValue = object.get(header);
	            	if(cellValue==null) {
	            		sheet.autoSizeColumn(numeroColumna);
	            	}else {
	            		if(cellValue instanceof String) {
	            			String value = String.valueOf(cellValue);
	            			if(value.length()>20) {
	            				sheet.setColumnWidth(numeroColumna, 15000);
	            			}else {
	            				sheet.autoSizeColumn(numeroColumna);
	            			}
	            		}else if(cellValue instanceof BigDecimal) {
	            			sheet.autoSizeColumn(numeroColumna);
	            		}else if(cellValue instanceof Date) {
	            			sheet.autoSizeColumn(numeroColumna);
	            		}
	            	}
	            	
	            }else {
	            	sheet.autoSizeColumn(numeroColumna);
	            }
	        }
	        
	        sheet.autoSizeColumn(0);
	        
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        workbook.write(bos);
	        byte[] bytes = bos.toByteArray();
	        
	        bos.close();
	        workbook.close();
			return bytes;
		} catch(ValidacionPersonalizadaException e) {
			throw e;
		} catch(Exception e) {
			logger.error("Error en exportar: ", e);
			throw new Exception("Error al exportar el reporte");
		}
	}
	
	private Map<Integer,String> getHeaders(Map<String, Object> object){
		Map<Integer,String> headers = new HashMap<>();
		int indice = 0;
		for (Map.Entry<String, Object> entry : object.entrySet()) {
		    String key = entry.getKey();
		    headers.put(indice, key);
		    indice++;
		}
		return headers;
	}
	
	private void setTitle(Sheet sheet, XSSFWorkbook workbook) throws IOException {
		
		Row headerRow = sheet.createRow(0);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
		Cell titleCell = headerRow.createCell(0);
        titleCell.setCellValue("REPORTE");
        
        XSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = workbook.createFont();
 		font.setFontHeightInPoints((short)26);
 		font.setFontName("Calibri");
 		font.setBold(true);
 		font.setColor((short)IndexedColors.WHITE.getIndex());

        titleStyle.setFont(font);
        titleStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(191, 9, 9)));
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Aplicar el estilo al título
        titleCell.setCellStyle(titleStyle);
        
        double altoFila = 48;
        short altoEnPuntos = (short) (altoFila * 20); // 1 punto = 1/20 de un punto
        headerRow.setHeight(altoEnPuntos);
		
		InputStream inputStream = ExportarUtil.class.getResourceAsStream("/static/images/logo_siad.PNG");
        byte[] bytesIMG = IOUtils.toByteArray(inputStream);
        
        int pictureIdx = workbook.addPicture(bytesIMG, Workbook.PICTURE_TYPE_PNG);
        inputStream.close();
        
        Drawing<?> drawing = sheet.createDrawingPatriarch();

        // Crear un ancla para la imagen
        ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();

        // Establecer la posición de la imagen
        anchor.setAnchorType(AnchorType.MOVE_DONT_RESIZE);
        anchor.setCol1(0); // Columna
        anchor.setRow1(0); // Fila
        anchor.setCol2(5); // Columna
        anchor.setRow2(0); // Fila
        
        anchor.setDx1(500); // Margen izquierdo
        anchor.setDy1(500); // Margen superior
        anchor.setDx2(500); // Margen derecho
        anchor.setDy2(500); // Margen inferior

        // Crear una imagen y adjuntarla al objeto Drawing
        Picture pict = drawing.createPicture(anchor, pictureIdx);

        // Ajustar el tamaño de la imagen si es necesario
        pict.resize(0.35, 1.0); // O pict.resize(1.0); para tamaño original, ancho y alto
	}
	
	private int setDetails(Sheet sheet, XSSFWorkbook workbook, Reporte reporte) throws IOException {
		
		int rowIndex = 2;
		Row headerRow = sheet.createRow(rowIndex);
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 5));
		Cell titleCell = headerRow.createCell(0);
        titleCell.setCellValue("DETALLES DEL REPORTE");
        
        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = workbook.createFont();
 		font.setFontHeightInPoints((short)12);
 		font.setFontName("Calibri");
 		font.setBold(true);
 		font.setColor((short)IndexedColors.WHITE.getIndex());

 		style.setFont(font);
 		style.setFillForegroundColor(new XSSFColor(new java.awt.Color(191, 9, 9)));
 		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
 		style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        
     // Aplicar el estilo al título
        titleCell.setCellStyle(style);
        
 		for (int i = 1; i <= 5; i++) {
 			Cell cell = headerRow.createCell(i);
 			XSSFCellStyle styleCell = workbook.createCellStyle();
 			styleCell.setBorderBottom(BorderStyle.THIN);
 			styleCell.setBottomBorderColor(IndexedColors.BLACK.getIndex());
 			styleCell.setBorderLeft(BorderStyle.THIN);
 			styleCell.setLeftBorderColor(IndexedColors.BLACK.getIndex());
 			styleCell.setBorderRight(BorderStyle.THIN);
 			styleCell.setRightBorderColor(IndexedColors.BLACK.getIndex());
 			styleCell.setBorderTop(BorderStyle.THIN);
 			styleCell.setTopBorderColor(IndexedColors.BLACK.getIndex());
 			cell.setCellStyle(styleCell);
 		}
        
 		rowIndex += 1;
 		
 		rowIndex = setDetail(sheet, workbook, rowIndex, "FECHA:", simpleDateFormatOUT.format(new Date()));
 		rowIndex = setDetail(sheet, workbook, rowIndex, "DESCRIPCIÓN:", reporte.getDescripcion().toUpperCase());
 		rowIndex = setDetail(sheet, workbook, rowIndex, "SOLICITANTE:", reporte.getUsuario().toUpperCase());
 		
 		rowIndex += 1;
 		
 		return rowIndex;
	}
	
	private CellStyle getStyleHeader(XSSFWorkbook workbook) {
		XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = workbook.createFont();
 		font.setFontHeightInPoints((short)12);
 		font.setFontName("Calibri");
 		font.setBold(true);
 		font.setColor((short)IndexedColors.WHITE.getIndex());

 		style.setFont(font);
 		style.setFillForegroundColor(new XSSFColor(new java.awt.Color(191, 9, 9)));
 		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
 		style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        
        return style;
	}
	
	private int setDetail(Sheet sheet, XSSFWorkbook workbook, int rowIndex, String title, String value) throws IOException {
		
		Row headerRow = sheet.createRow(rowIndex);
		sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 1, 5));
		Cell titleCell = headerRow.createCell(0);
        titleCell.setCellValue(title);
        
        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = workbook.createFont();
 		font.setFontHeightInPoints((short)10);
 		font.setFontName("Calibri");
 		font.setBold(true);
 		font.setColor((short)IndexedColors.BLACK.getIndex());
 		style.setFont(font);
 		style.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 204, 204)));
 		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
 		style.setIndention((short)1);
        
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());


        // Aplicar el estilo al título
        titleCell.setCellStyle(style);
        
        Cell valueCell = headerRow.createCell(1);
        valueCell.setCellValue(value);
        
        XSSFCellStyle styleValue = workbook.createCellStyle();
        styleValue.setAlignment(HorizontalAlignment.LEFT);
        styleValue.setVerticalAlignment(VerticalAlignment.CENTER);

        Font fontValue = workbook.createFont();
        fontValue.setFontHeightInPoints((short)10);
        fontValue.setFontName("Calibri");
        fontValue.setBold(false);
        fontValue.setColor((short)IndexedColors.BLACK.getIndex());
 		styleValue.setFont(fontValue);
 		styleValue.setIndention((short)1);
        
 		styleValue.setBorderBottom(BorderStyle.THIN);
        styleValue.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        styleValue.setBorderLeft(BorderStyle.THIN);
        styleValue.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        styleValue.setBorderRight(BorderStyle.THIN);
        styleValue.setRightBorderColor(IndexedColors.BLACK.getIndex());
        styleValue.setBorderTop(BorderStyle.THIN);
        styleValue.setTopBorderColor(IndexedColors.BLACK.getIndex());

 	// Aplicar el estilo al título
        valueCell.setCellStyle(styleValue);
        
        for (int i = 2; i <= 5; i++) {
 			Cell cell = headerRow.createCell(i);
 			XSSFCellStyle styleCell = workbook.createCellStyle();
 			styleCell.setBorderBottom(BorderStyle.THIN);
 			styleCell.setBottomBorderColor(IndexedColors.BLACK.getIndex());
 			styleCell.setBorderLeft(BorderStyle.THIN);
 			styleCell.setLeftBorderColor(IndexedColors.BLACK.getIndex());
 			styleCell.setBorderRight(BorderStyle.THIN);
 			styleCell.setRightBorderColor(IndexedColors.BLACK.getIndex());
 			styleCell.setBorderTop(BorderStyle.THIN);
 			styleCell.setTopBorderColor(IndexedColors.BLACK.getIndex());
 			cell.setCellStyle(styleCell);
 		}
        
        rowIndex += 1;
    	return rowIndex;
	}

	
}
