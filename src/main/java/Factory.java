import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.SystemOutLogger;

public class Factory {

	private Import mport;
	private String[][] matrix;
	private Information[] informationPerRaw;
<<<<<<< HEAD
	private int[][] counter; // for canel0Entries
	private int maxNumberColumn = 0; // for createFactoryStrucure
	private int[][] counterShort; // for cancel0Entries, createFactoryStructure
	private Zone[][] factoryStructure; // for createFactoryStructure
=======
	int[][] counter; // constructed in: countRowsColumns
	int maxNumberColumn = 0; // for createFactoryStrucure
	Zone[][] factoryStructure; // for createFactoryStructure
>>>>>>> 551e307b5f763f500361c491b1ee82f0b902ae14

	public Factory() throws InvalidFormatException, IOException {
		mport = new Import();
		this.matrix = mport.getMatrix();
		initializeFactory();
	}

	public void initializeFactory() {
		// count how many rows and how many columns each zone has
		countRowsColumns();
		// create factory structure
		createFactoryStructure();
		// put raster into zones
		rasterIntoZones();
		System.out.println(factoryStructure);
	}

	/*
	 * from the matrix that contains the same information as the excel: take each
	 * single entry in the column "Materialfläche". this will we our first step do
	 * count how many rows and columns per row there are. Additonally we will aldo
	 * save the "name" of each row for future purposes example of how
	 */
	public void countRowsColumns() {
		counter = new int[mport.getJ()][2];
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
<<<<<<< HEAD
		}
		cancel0Entries();
		createFactoryStructure();
		rasterIntoZones();

=======

		}

		// make the matrix shorter, cancelling the superfluos 0-entries
		cancel0Entries();
>>>>>>> 551e307b5f763f500361c491b1ee82f0b902ae14
	}

	/*
	 * cancel 0-entries used in createStructureFactory used in: countRowsColumns to
	 * remove all the 0-entries
	 */
	public void cancel0Entries() {
		int[][] counterShort; // for cancel0Entries, createFactoryStructure
		int u = 0;
		while (counter[u][0] != 0) {
			u++;
		}
		int laenge = u - 1;
		counterShort = new int[laenge + 1][2];
		System.arraycopy(counter, 0, counterShort, 0, laenge + 1);
		counter = counterShort;
	}

	/*
	 * create factory structure used in createStructureFactory
	 */
	public void createFactoryStructure() {
		int numberRows = counter.length;
		factoryStructure = new Zone[numberRows / 2][7]; // we have a maximum of 7 zones per row

	}

	/*
	 * put Raster into zones used
	 */
<<<<<<< HEAD
	public void rasterIntoZones() { // int rowInImport
=======
	public void rasterIntoZones() {
		// take every single entry in the column "Materialfläche"
>>>>>>> 551e307b5f763f500361c491b1ee82f0b902ae14
		for (int i = 1; i < mport.getI() - 1; i++) { // j=5
			String fullPosition = matrix[i][5];
			int rowNumber = Integer.parseInt(fullPosition.substring(0, 3));
			int columnNumber = Integer.parseInt(fullPosition.substring(4));

			/*
			 * next part: taking in consideration the information in counter, we can
			 * determine in wihich row the zone has to go
			 */
			int j;
			for (j = 0; j < counter.length; j++) {
				if (rowNumber == counter[j][0]) {
					break;
				}
			}
			int rowInFactoryStructure;
			boolean rowEven = false;
			if (j % 2 == 0) {
				rowEven = true;
				rowInFactoryStructure = j;
				rowInFactoryStructure = (rowInFactoryStructure + 2) / 2 - 1;
			} else {
				rowInFactoryStructure = j;
				rowInFactoryStructure = (rowInFactoryStructure + 1) / 2 - 1;
			}

<<<<<<< HEAD
			// get the zone for this specific raster
=======
			// get the zone(name) for this specific raster
>>>>>>> 551e307b5f763f500361c491b1ee82f0b902ae14
			String zoneName = matrix[i][1]; // matrix rowinimport 1

			/*
			 *  see if this zone is already in the factoryLayout. 
			 *  if not, add the zone and add the raster into this zone.
			 *  if yes, get into zone and add the new raster in its internal layout.
			 */
			boolean alreadyIn = false;
			int k;
			for (k = 0; k < 7; k++) {
				if (factoryStructure[rowInFactoryStructure][6 - k] == null) {
					break;
				} else {
					if (factoryStructure[rowInFactoryStructure][6 - k].equals(zoneName)) {
						alreadyIn = true;
						break;
					}
				}
<<<<<<< HEAD

				if (k != 7) {
					if (alreadyIn == false) {
						factoryStructure[rowInLayout][6 - k] = new Zone(zoneName);
						factoryStructure[rowInLayout][6 - k].raster = new Raster[2][43];
						int sizeRaster = Integer.parseInt(matrix[i][17]); // rowInImport
//						factoryStructure[rowInLayout][6 - k].raster = new Raster[2][43];
						int firstOrSecondRow;
						if (rowEven == true) {
							firstOrSecondRow = 0;
							factoryStructure[rowInLayout][6 - k].amountRasterRow1 += sizeRaster;
						} else {
							firstOrSecondRow = 1;
							factoryStructure[rowInLayout][6 - k].amountRasterRow2 += sizeRaster;
						}
						factoryStructure[rowInLayout][6 - k].raster[firstOrSecondRow][42
								- factoryStructure[rowInLayout][6 - k].amountRasterRow1] = new Raster(rowNumber,
										columnNumber);
					}
=======
			}
			if (k != 7) { //avoiding out of bounce
				if (alreadyIn == false) {
					factoryStructure[rowInFactoryStructure][6 - k] = new Zone(zoneName);
					factoryStructure[rowInFactoryStructure][6 - k].raster = new Raster[2][43];
>>>>>>> 551e307b5f763f500361c491b1ee82f0b902ae14
				}
				int firstOrSecondRow;
				if (rowEven == true) {
					firstOrSecondRow = 0;
				} else {
					firstOrSecondRow = 1;
				}
				factoryStructure[rowInFactoryStructure][6 - k].raster[firstOrSecondRow][42 - columnNumber
						- 12] = new Raster(rowNumber, columnNumber);

				/*
				 * vecchio. Non ancora da cancellare!
				 */
//				Double sizeRaster = Double.parseDouble(matrix[i][17].replace(".", "").replace(",", ".")); // rowInImport
//				int firstOrSecondRow;
//				if (rowEven == true) {
//					firstOrSecondRow = 0;
//					factoryStructure[rowInFactoryStructure][6 - k].amountRasterRow1 += sizeRaster;
//					factoryStructure[rowInFactoryStructure][6 - k].raster[firstOrSecondRow][42
//							- factoryStructure[rowInFactoryStructure][6 - k].amountRasterRow1] = new Raster(rowNumber,
//									columnNumber);
//				} else {
//					firstOrSecondRow = 1;
//					factoryStructure[rowInFactoryStructure][6 - k].amountRasterRow2 += sizeRaster;
//					factoryStructure[rowInFactoryStructure][6 - k].raster[firstOrSecondRow][42
//							- factoryStructure[rowInFactoryStructure][6 - k].amountRasterRow2] = new Raster(rowNumber,
//									columnNumber); //nel raster dobbiamo mettere la info che logistikequipment tiene 
			}
		}
	}

	public String[][] getMatrix() {
		return matrix;
	}

	public Zone[][] getFactoryStructure() {
		return factoryStructure;
	}
}
