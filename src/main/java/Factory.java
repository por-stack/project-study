import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.SystemOutLogger;

public class Factory {

	private Import mport;
	private String[][] matrix;
	private Information[] informationPerRaw;
	private int[][] counter; // for canel0Entries
	private int maxNumberColumn = 0; // for createFactoryStrucure
	private Zone[][] factoryStructure; // for createFactoryStructure
	private boolean isTrainStat = false;
	private int number;

	private ArrayList<Zone> emptyZones;
	private ArrayList<Zone> zonesToAllocate;

	public Factory() throws InvalidFormatException, IOException {
		this.mport = new Import();
		this.matrix = mport.getMatrix();
		this.initializeFactory();
//		this.emptyZones = createEmptyZones(factoryStructure);
//		this.zonesToAllocate = createZonesToAllocate(factoryStructure); // to implement
	}

	public void initializeFactory() {
		// count how many rows and how many columns each zone has
		countRowsColumns();
		// create factory structure
		createFactoryStructure();
		// put raster into zones
		rasterIntoZones();

		// prints the matrix 'counter'
		System.out.println(Arrays.deepToString(counter));
		System.out.println(matrix[19][1].equals("51S"));
	}

	/*
	 * from the matrix that contains the same information as the excel: take each
	 * single entry in the column "Materialfläche". this will be our first step do
	 * count how many rows and columns per row there are. Additionally we will also
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
		for (int i = 1; i < mport.getI() - 1; i++) { // j=5 // breakpoint

			// debug
			if (i == 127)
				System.out.println("");

			isTrainStat = false;
			String fullPosition = matrix[i][5];
			int rowNumber = Integer.parseInt(fullPosition.substring(0, 3));
			int columnNumber = Integer.parseInt(fullPosition.substring(4));

			/*
			 * next part: taking in consideration the information in counter, we can
			 * determine in which row the zone has to go
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

			// this raster is part of a train station
			if (matrix[i][2].equals("BHF")) {
				isTrainStat = true;
			}

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
					if (factoryStructure[rowInFactoryStructure][6 - k].name.equals(zoneName)) {
						alreadyIn = true;
						break;
					}
					if (zoneName.length() > 3) {
						if (factoryStructure[rowInFactoryStructure][6 - k].name.equals(zoneName.substring(4))) {
							alreadyIn = true;
							break;
						}
					}
				}
			}
			if (k != 7) { // avoiding out of bound
				int firstOrSecondRow;
				if (rowEven == true) {
					firstOrSecondRow = 0;
				} else {
					firstOrSecondRow = 1;
				}
				if (alreadyIn == false) {
					// If the train station belongs to 2 zones, no new zone must be created. The
					// rasters must be divided between the two zones.
					if (!matrix[i][1].contains("/")) {
						factoryStructure[rowInFactoryStructure][6 - k] = new Zone(zoneName, 0, 0, rowInFactoryStructure,
								6 - k); // calculated how
						// many raster per
						// zone
						factoryStructure[rowInFactoryStructure][6 - k].raster = new Raster[2][43];
						factoryStructure[rowInFactoryStructure][6 - k].raster[firstOrSecondRow][42
								- (columnNumber - 12)] = new Raster(rowNumber, columnNumber, isTrainStat); // manca
																											// logisticequipment
					} else {
						String string = "";
						if ((Integer
								.parseInt((zoneName.substring(zoneName.indexOf("/") + 1)).substring(0, 2)) >= (Integer
										.parseInt((zoneName.substring(0, zoneName.indexOf("/"))).substring(0, 2))))) {
							string = (zoneName.substring(zoneName.indexOf("/") + 1));
						} else
							string = (zoneName.substring(0, zoneName.indexOf("/")));

						int dimTrSt = (int) (Double.parseDouble(matrix[i][17].replace(',', '.')));
						int remaining = dimTrSt - dimTrSt / 2; // non dovrebbe servire
						// divide rasters
						factoryStructure[rowInFactoryStructure][6 - k + 1].raster[firstOrSecondRow][(42
								- (columnNumber - 12)) - dimTrSt / 2] = new Raster(rowNumber,
										columnNumber - (dimTrSt / 2), isTrainStat);
						factoryStructure[rowInFactoryStructure][6 - k] = new Zone(string, 0, 0, rowInFactoryStructure,
								6 - k); // calculate how many
						// raster per row
						factoryStructure[rowInFactoryStructure][6 - k].raster = new Raster[2][43];
						factoryStructure[rowInFactoryStructure][6 - k].raster[firstOrSecondRow][42
								- (columnNumber - 12)] = new Raster(rowNumber, columnNumber, isTrainStat);
					}
				} else {
					if (!matrix[i][1].contains("/")) {
						factoryStructure[rowInFactoryStructure][6 - k].raster[firstOrSecondRow][42
								- (columnNumber - 12)] = new Raster(rowNumber, columnNumber, isTrainStat); // manca
																											// logisticequipment
					}
					// bahnhof with "/"
					else {
						int dimTrSt = (int) (Double.parseDouble(matrix[i][17].replace(',', '.')));
						factoryStructure[rowInFactoryStructure][6 - k].raster[firstOrSecondRow][(42
								- (columnNumber - 12)) - dimTrSt / 2] = new Raster(rowNumber,
										columnNumber - (dimTrSt / 2), isTrainStat);
						if (factoryStructure[rowInFactoryStructure][6 - k - 1] == null) {
							factoryStructure[rowInFactoryStructure][6 - k - 1] = new Zone(zoneName.substring(0, 3), 0,
									0, rowInFactoryStructure, 6 - k - 1);
						}
						factoryStructure[rowInFactoryStructure][6 - k - 1].raster[firstOrSecondRow][42
								- (columnNumber - 12)] = new Raster(rowNumber, columnNumber, isTrainStat);
					}
				}
			}
		}
	}

	public ArrayList<Zone> createEmptyZones(Zone[][] factory) {
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

	/*
	 * to implement
	 */
	public ArrayList<Zone> createZonesToAllocate(Zone[][] factory) {
		ArrayList<Zone> zonesToAllocate = new ArrayList<Zone>();
		// INSERISCI NELL ORDINE GIUSTO
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
	public ArrayList<Zone> getEmptyZones() {
		return emptyZones;
	}

	/**
	 * @param emptyZones the emptyZones to set
	 */
	public void setEmptyZones(ArrayList<Zone> emptyZones) {
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

<<<<<<< HEAD
//	public static void main(String[] args) throws InvalidFormatException, IOException {
//		Factory initial = new Factory();
//
//		Zone[][] factoryStructure = initial.getFactoryStructure();
//		for (int i = 0; i < factoryStructure.length; i++) {
//			System.out.println("\n" + "NEW ROW" + "\n");
//			for (int j = 0; j < factoryStructure[0].length; j++) {
//				if (factoryStructure[i][6 - j] == null) {
//					System.out.println("null");
//				} else {
//					System.out.println(factoryStructure[i][6 - j].name);
//					System.out.println(Arrays.deepToString(factoryStructure[i][6 - j].raster));
//				}
//			}
//		}
//	}
=======
	public static void main(String[] args) throws InvalidFormatException, IOException {
		Factory initial = new Factory();

		Zone[][] factoryStructure = initial.getFactoryStructure();
		for (int i = 0; i < factoryStructure.length; i++) {
			System.out.println("\n" + "NEW ROW" + "\n");
			for (int j = 0; j < factoryStructure[0].length; j++) {
				if (factoryStructure[i][6 - j] == null) {
					System.out.println("null");
				} else {
					System.out.println(factoryStructure[i][6 - j].name);
					System.out.println(Arrays.deepToString(factoryStructure[i][6 - j].raster));
				}
			}
		}
		
		initial.mport.demo();
	}
>>>>>>> 7065457e125343d606ebe89bb5f554f0673c2a8a
}
