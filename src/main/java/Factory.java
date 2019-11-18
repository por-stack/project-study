import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.SystemOutLogger;

public class Factory {

	final int LENGTH_OF_ROWS_IN_RASTER = 43;
	int numRows;

	private Import mport;
	private Zone zone;
	private Raster raster;
	private String[][] matrix;

	public Factory() throws InvalidFormatException, IOException {
		mport = new Import();
		this.matrix = mport.getMatrix();
	}

	public void createStructureFactory() {
		int[][] counter = new int[mport.getJ()][2];
		int maxNumberColumn = 0;

		for (int i = 1; i < mport.getI() - 1; i++) { // j=5
			String fullPosition = matrix[i][5];
			int row = Integer.parseInt(fullPosition.substring(0, 3));
			int column = Integer.parseInt(fullPosition.substring(4));

			boolean rowAlreadyIn = false;
			int j;
			for (j = 0; j < counter.length; j++) {
				if (counter[j][0] == 0) {
					break;
				} else {
					if (row == counter[j][0]) {
						rowAlreadyIn = true;
						break;
					}
				}
			}

			if (j != counter.length)
				if (rowAlreadyIn == false) {
					counter[j][0] = row;
				} else {
					if (counter[j][1] < column)
						counter[j][1] = column;
					if (counter[j][1] > maxNumberColumn)
						maxNumberColumn = counter[j][1];
				}
		}

		/*
		 * cancel 0-entries
		 */
		int u = 0;
		while (counter[u][0] != 0) {
			u++;
		}
		int laenge = u - 1;
		int[][] counterShort = new int[laenge + 1][2];
		System.arraycopy(counter, 0, counterShort, 0, laenge + 1);

		/*
		 * create factory structure
		 */
		int numberRows = counterShort.length;
		Raster[][] factoryStructure = new Raster[numberRows][maxNumberColumn - 11];

		/*
		 * put Raster into factoryStructure
		 */
		for (int i = 1; i < mport.getI() - 1; i++) { // j=5
			String fullPosition = matrix[i][5];
			int rowNumber = Integer.parseInt(fullPosition.substring(0, 3));
			int columnNumber = Integer.parseInt(fullPosition.substring(4));

			int j; 
			for (j = 0; j < counterShort.length; j++) {
				if (rowNumber == counterShort[j][0]) {
					break;
				}
			}

			factoryStructure[j][factoryStructure[0].length -1 + 12 - columnNumber] = new Raster(rowNumber, columnNumber);
			System.out.println(factoryStructure[j][factoryStructure[0].length -1 + 12 - columnNumber]);
		}
		
	}
}
