package ai.hyperlearning.ontology.services.etl.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Microsoft Excel Utility Methods
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class ExcelUtils {
	
	/**
	 * Read the Data Catalogue Excel Spreadsheet 
	 * and return a workbook object
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	
	public static Workbook readWorkbook(String filename) throws IOException {
		InputStream inputStream = ExcelUtils.class
				.getResourceAsStream("/" + filename);
		return new XSSFWorkbook(inputStream);
	}
	
	/**
	 * Return a cell value given a Cell object
	 * @param cell
	 * @return
	 */
	
	public static Object getCellValue(Cell cell) {
		switch (cell.getCellType()) {
			case STRING:
				return cell.getRichStringCellValue().getString();
			case NUMERIC:
				if (DateUtil.isCellDateFormatted(cell))
					return cell.getDateCellValue();
				else
					return cell.getNumericCellValue();
			case BOOLEAN:
				return cell.getBooleanCellValue();
			default:
				return cell.getRichStringCellValue().getString();
		}
	}
	
	/**
	 * Clean column names by removing all non-alphanumeric characters
	 * @param columnName
	 * @return
	 */
	
	public static String cleanColumnName(String columnName) {
		return columnName
				.replaceAll("[^a-zA-Z0-9]", "_")
				.replaceAll("_+", "_")
				.trim()
				.toLowerCase();
	}
	
	/**
	 * Parse the header row of the Data Catalogue Excel workbook
	 * and return a list of cleansed column names
	 * @param sheet
	 * @return
	 */
	
	public static List<String> parseColumnNames(Sheet sheet) {
		List<String> columnNames = new ArrayList<String>();
		Row headerRow = sheet.getRow(0);
		for (Cell cell : headerRow) {
			columnNames.add(
					cleanColumnName(
							cell.getRichStringCellValue().getString()));
		}
		return columnNames;
	}

}
