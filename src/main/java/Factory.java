import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.SystemOutLogger;

public class Factory {

	private Import mport;
	private Zone zone;
	private Raster raster;
	private String[][] matrix;
	private Information[] informationPerRaw;
	int[][] counter; // for canel0Entries
	int maxNumberColumn = 0; // for createFactoryStrucure
	int[][] counterShort; // for cancel0Entries, createFactoryStructure
	Zone[][] factoryStructure; // for createFactoryStructure

	public Factory() throws InvalidFormatException, IOException {
		mport = new Import();
		this.matrix = mport.getMatrix();
	}

	public void createStructureFactory() {
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
			// cancel 0-entries
			cancel0Entries();
			// create factory structure
			createFactoryStructure();
			// put Raster into Zones
			rasterIntoZones(i);
		}
	}

	/*
	 * cancel 0-entries used in createStructureFactory
	 */
	public void cancel0Entries() {
		int u = 0;
		while (counter[u][0] != 0) {
			u++;
		}
		int laenge = u - 1;
		counterShort = new int[laenge + 1][2];
		System.arraycopy(counter, 0, counterShort, 0, laenge + 1);
	}

	/*
	 * create factory structure used in createStructureFactory
	 */
	public void createFactoryStructure() {
		int numberRows = counterShort.length;
		factoryStructure = new Zone[numberRows / 2][7]; // we have a maximum of 7 zones per row
	}

	/*
	 * put Raster into zones used
	 */
	public void rasterIntoZones(int rowInImport) {
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

			// find out in which row the zone has to go. Remember that each zones involves
			// to rows of Rasters
			int rowInLayout;
			boolean rowEven = false;
			if (rowNumber % 2 == 0) {
				rowEven = true;
				rowInLayout = rowNumber;
				rowInLayout = (rowInLayout + 2) / 2;
			} else {
				rowInLayout = rowNumber;
				rowInLayout = (rowInLayout + 1) / 2;
			}

			// get the zone for this specific raster
			String zoneName = matrix[rowInImport][1];

			// see if this zone is already in the factoryLayout. if not, add the zone and
			// add the raster into this zone.
			// if yes, get into zone and add the new raster in its interal layout.
			boolean alreadyIn = false;
			int k;
			for (k = 0; k < 7; k++) {
				if (factoryStructure[rowInLayout][6 - k] == null) {
					break;
				} else {
					if (factoryStructure[rowInLayout][6 - k].equals(zoneName)) {
						alreadyIn = true;
						break;
					}
				}

				if (k != 7) {
					if (alreadyIn == false) {
						factoryStructure[rowInLayout][6 - k] = new Zone(zoneName);
						factoryStructure[rowInLayout][6 - k].raster = new Raster[2][43];
						int sizeRaster = Integer.parseInt(matrix[rowInImport][17]);
						factoryStructure[rowInLayout][6 - k].raster = new Raster[2][43];
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
<<<<<<< HEAD
=======

>>>>>>> fd6035876a61cf12db00018063ad7faa08217bc3
					}
				}

//			factoryStructure[j][factoryStructure[0].length - 1 + 12 - columnNumber] = new Raster(rowNumber,
//					columnNumber);
//			System.out.println(factoryStructure[j][factoryStructure[0].length - 1 + 12 - columnNumber]);
			}
		}

	}

	public ArrayList<Zone> createStartingArray(Zone[][] factory) {
		ArrayList<Zone> emptyZones = new ArrayList<Zone>();
		for (int i = 0; i < factory.length; i++) {
			for (int j = 0; j < factory[0].length; j++) {
				if (factory[i][j].isEmpty) {
					emptyZones.add(factory[i][j]);
				}
			}
		}
		return emptyZones;
	}
	
	
}
