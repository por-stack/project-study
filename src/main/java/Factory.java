import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.SystemOutLogger;

public class Factory {

	private Import mport;
	private String[][] matrix;
	private int[][] counter; // for canel0Entries
	private int maxNumberColumn = 0; // for createFactoryStrucure
	private Zone[][] factoryStructure; // for createFactoryStructure

	private ArrayList<EmptyZone> emptyZones;
	private ArrayList<Zone> zonesToAllocate;
	

	public Factory() throws InvalidFormatException, IOException {
		this.mport = new Import();
		this.matrix = mport.getMatrix();
		this.initializeFactory();
		this.emptyZones = createEmptyZones(factoryStructure);
		this.zonesToAllocate = createZonesToAllocate(factoryStructure); // to implement
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

		}

		// make the matrix shorter, cancelling the superfluos 0-entries
		cancel0Entries();
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

	public void rasterIntoZones() {
		// take every single entry in the column "Materialfläche"
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

			// get the zone(name) for this specific raster
			String zoneName = matrix[i][1]; // matrix rowinimport 1

			/*
			 * see if this zone is already in the factoryLayout. if not, add the zone and
			 * add the raster into this zone. if yes, get into zone and add the new raster
			 * in its internal layout.
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
			}
			if (k != 7) { // avoiding out of bounce
				if (alreadyIn == false) {
					factoryStructure[rowInFactoryStructure][6 - k] = new Zone(zoneName, 0, 0);
					factoryStructure[rowInFactoryStructure][6 - k].raster = new Raster[2][43];
				}
				int firstOrSecondRow;
				if (rowEven == true) {
					firstOrSecondRow = 0;
				} else {
					firstOrSecondRow = 1;
				}
				factoryStructure[rowInFactoryStructure][6 - k].raster[firstOrSecondRow][42 - columnNumber
						- 12] = new Raster(rowNumber, columnNumber); // manca logisticequipment

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

	public ArrayList<EmptyZone> createEmptyZones(Zone[][] factory) {
		ArrayList<EmptyZone> emptyZones = new ArrayList<EmptyZone>();
		for (int i = 0; i < factory.length; i++) {
			for (int j = 0; j < factory[0].length; j++) {
				if (factory[i][j].isEmpty) {
					EmptyZone emptyZone = new EmptyZone(factory[i][j].name, factory[i][j].amountRasterRow1,
							factory[i][j].amountRasterRow2, i, j);
					emptyZones.add(emptyZone);
				}
			}
		}
		return emptyZones;
	}

	/*
	 * to implement
	 */
	public ArrayList<Zone> createZonesToAllocate(Zone[][] factory) {
		ArrayList<Zone> zonesToAllocate = new ArrayList<Zone>();
		//toDo 
		return zonesToAllocate;
	}

	public String[][] getMatrix() {
		return matrix;
	}

	public Zone[][] getFactoryStructure() {
		return factoryStructure;
	}
	
	public void setFactoryStructure(Zone[][] factoryStructure) {
		this.factoryStructure = factoryStructure; 
	}

	/**
	 * @return the emptyZones
	 */
	public ArrayList<EmptyZone> getEmptyZones() {
		return emptyZones;
	}

	/**
	 * @param emptyZones the emptyZones to set
	 */
	public void setEmptyZones(ArrayList<EmptyZone> emptyZones) {
		this.emptyZones = emptyZones;
	}

	/**
	 * @return the zonesToAllocate
	 */
	public ArrayList<Zone> getZonesToAllocate() {
		return zonesToAllocate;
	}

	/**
	 * @param zonesToAllocate the zonesToAllocate to set
	 */
	public void setZonesToAllocate(ArrayList<Zone> zonesToAllocate) {
		this.zonesToAllocate = zonesToAllocate;
	}
}
