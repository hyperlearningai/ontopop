package ai.hyperlearning.ontology.services.etl.parsers.implementations;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;

import ai.hyperlearning.ontology.services.etl.parsers.DataCatalogueParser;
import ai.hyperlearning.ontology.services.etl.utils.ExcelUtils;
import ai.hyperlearning.ontology.services.model.datamanagement.Dataset;

/**
 * Highways England Data Catalogue Parser
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class HighwaysEnglandDataCatalogueParser extends DataCatalogueParser {
	
	private static final int SHEET_INDEX = 0;
	private static final Set<String> EXCLUDE_COLUMNS = Stream.of(
			"etl_agent_location", 
			"data_condition_framework_compliant_", 
			"owner").collect(Collectors.toCollection(HashSet::new));
	
	/**
	 * Parse a string of entities into a set of entity strings
	 * @param entitiesString
	 * @return
	 */
	
	@SuppressWarnings("deprecation")
	private Set<String> parseEntitiesString(String entitiesString) {
		Set<String> entities = new LinkedHashSet<String>();
		String lines[] = entitiesString.split("[\\r?\\n\\.]");
		for (String line: lines) {
			if (!StringUtils.isEmpty(entitiesString)) {
				entities.add(line.trim());
			}
		}
		return entities;
	}
	
	/**
	 * Parse the Data Catalogue Excel workbook
	 * and return a set of dataset objects
	 * @return
	 */
	
	@Override
	public Set<Dataset> parseDataCatalogue(String filename) {
		
		// Instantiate an empty set of dataset objects
		Set<Dataset> datasets = new LinkedHashSet<Dataset>();
		
		try {
			
			// Parse the data catalogue Excel spreadsheet
			Workbook workbook = ExcelUtils.readWorkbook(filename);
			Sheet sheet = workbook.getSheetAt(SHEET_INDEX);
			List<String> columnNames = ExcelUtils.parseColumnNames(sheet);
			
			// Iterate over each row excluding the header row
			for (Row row : sheet) {
				if (row.getRowNum() > 0) {
					
					// Create and populate a new dataset object
					Dataset dataset = new Dataset();
					Map<String, Object> metadata = 
							new LinkedHashMap<String, Object>();
					for (Cell cell : row) {
						switch (cell.getColumnIndex()) {
							
							// Title <> dataset.name
							case 0:
								String name = ExcelUtils.getCellValue(cell)
										.toString();
								dataset.setName(name);
								metadata.put("name", name);
								break;
								
							// Entities <> dataset.entities
							case 1:
								Set<String> entities = parseEntitiesString(
										ExcelUtils.getCellValue(cell)
										.toString());
								dataset.setEntities(entities);
								metadata.put("entities", entities.toString());
								break;
								
							// Metadata <> dataset.metadata<Column Name, Cell Value>
							default:
								String columnName = columnNames.get(
										cell.getColumnIndex());
								if (!EXCLUDE_COLUMNS.contains(columnName))
									metadata.put(
											columnNames.get(
													cell.getColumnIndex()), 
											ExcelUtils.getCellValue(cell));
								break;
						
						}
						
					}
					
					// Add the newly created dataset object to the set
					dataset.setMetadata(metadata);
					datasets.add(dataset);
					
				}
			}
			
		} catch (Exception e) {
			
			
		}
		
		return datasets;
		
	}

}
