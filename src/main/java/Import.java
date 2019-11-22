
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class Import {

	public static final String path = "C:\\Users\\Utente\\eclipse-workspace\\Porsche\\src\\main\\java\\Java.xlsx"; //PROVA

	private String matrix [][];
	private Workbook wb;
	private Sheet sh;
	private DataFormatter dtfrm;
	
	private int i = 0;
	private int j = 0; 
	
	/*
	 * Constructor
	 */
	public Import() throws InvalidFormatException, IOException {
		this.init();
		this.create();
//		wb.close();			// Closing the window
	}
	
	/*
	 *  Workbook, Sheet, Dataformatter
	 */
	public void init() throws IOException, InvalidFormatException{
		wb = WorkbookFactory.create(new File("C:\\Users\\aless\\eclipse-workspace\\Porsche\\src\\main\\java\\Java.xlsx"));
		sh = wb.getSheetAt(0);
		dtfrm = new DataFormatter();
	}
	
	/*
	 * Count size of Excel-Table
	 */
	public void create() {
		for (Row row : sh) {
			j = 0; 
			for (Cell cell : row) {
				String cellValue = dtfrm.formatCellValue(cell);
				j++;
			}
			i++; 
		}
//		System.out.println("rows: " + i + ", columns: " + j);
		
		/*
		 * Create Matrix based on the size of the excel Table
		 */
		matrix = new String [i][j];
		i = 0; 
		j= 0; 
		
		/*
		 * Create a to the Excel table equivalent matrix in java
		 */
		for (Row row : sh) {
			j = 0;
			for (Cell cell : row) {
				String cellValue = dtfrm.formatCellValue(cell);
				matrix[i][j] = cellValue;
				j++;
			}
			i++; 
		}
	}
	
	public void demo() {
		/*
		 * printout matrix
		 */
		for(int k = 0; k < i-1; k++) {
			for (int l = 0; l < j; l++) {
				String cellValue = matrix[k][l];
				System.out.print(cellValue + "| ");
			}
			System.out.println("\n");
		}
	}

	public String[][] getMatrix() {
		return matrix;
	}

	public int getI() {
		return i;
	}

	public int getJ() {
		return j;
	}
}
