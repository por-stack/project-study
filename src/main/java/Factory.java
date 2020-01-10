import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.SystemOutLogger;

public class Factory {

	private Import mport;
	private String[][] matrix;

	private int[][] counter; // for canel0Entries
	private boolean isTrainStat = false;
	private int maxNumberColumn = 0; // for createFactoryStrucure

	private Zone[][] factoryStructure; // for createFactoryStructure
	private ArrayList<EmptyZone> emptyZones;
	private ArrayList<Zone> zonesToAllocate;

	int[][] empty = { { 0, 0, 0, 0, 0, 1, 1 }, { 0, 0, 1, 0, 0, 0, 1 }, { 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0 } };

	public Factory() throws Exception {
		this.mport = new Import();
		this.matrix = mport.getMatrix();
		this.initializeFactory();
	}

	public Factory(boolean toCopy) {

	}

	public void initializeFactory() throws Exception {
		// count how many rows and how many columns each zone has
		countRowsColumns();
		// create factory structure
		createFactoryStructure();
		// put raster into zones
		rasterIntoZones();

		// RIMETTERE ATTENZIONE!!
		zonesToAllocate = createZonesToAllocate(this.factoryStructure);
		readEmptyImput();
		emptyZones = createEmptyZones(this.factoryStructure);

		System.out.println("");
	}

	/*
	 * from the matrix that contains the same information as the excel: take each
	 * single entry in the column "Materialfläche". this will be our first step do
	 * count how many rows and columns per row there are. Additionally we will also
	 * save the "name" of each row for future purposes example of how
	 */
	public void countRowsColumns() {
		counter = new int[mport.getJ() + 100][2];
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

	public void rasterIntoZones() throws Exception {
		// take every single entry in the column "Materialfläche"
		for (int i = 1; i < mport.getI() - 1; i++) { // j=5 // breakpoint
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
				/**
				 * The for loop in code-line ~113 elaborates for each iteration one line of the
				 * excel input file. Let's define it as "line". Each line represents one
				 * logistic equipment of a zone or a train station. Some train stations belong
				 * only to ONE zone, whereas some others are being shared from 2 zones. Let's
				 * define these as "double stations". We distinguish 2 particular cases, each of
				 * them with subcases:
				 * 
				 * Case 1: The zone of the given line is not yet contained in the structure.
				 * Case 1.1: The line is a logistic equipment or a normal train station______
				 * Case 1.2: The line is a double station
				 * 
				 * Case 2: The zone of the given line is already contained in the structure.
				 * Case 2.1: The line is a logistic equipment or a normal train station________
				 * Case 2.2: The line is a double station
				 */

				// CASE 1
				if (alreadyIn == false) {
					// CASE 1.1
					// The new zone is created and added to the factoryStructure. Secondly, the
					// logistic equipment or normal train station is added to the "raster"-matrix of
					// the zone, in the correct place. The logistic-equipment-balance-list of the
					// zone is updated with the new logistic equipment. The zone's counting
					// variables for rasters and train stations are updated as well.
					if (!matrix[i][1].contains("/")) {
						factoryStructure[rowInFactoryStructure][6 - k] = new Zone(zoneName, 0, 0, rowInFactoryStructure,
								6 - k);

						factoryStructure[rowInFactoryStructure][6 - k].raster = new Raster[2][43];
						factoryStructure[rowInFactoryStructure][6 - k].raster[firstOrSecondRow][42
								- (columnNumber - 12)] = new Raster(rowNumber, columnNumber, isTrainStat);

						factoryStructure[rowInFactoryStructure][6 - k].increaseLogEquip(matrix[i][6]);
						if (!isTrainStat)
							factoryStructure[rowInFactoryStructure][6 - k].increaseAmountRasterRow(firstOrSecondRow,
									matrix[i][6]);
						else {
							int dim = (int) (Double.parseDouble(matrix[i][17].replace(',', '.')));
							factoryStructure[rowInFactoryStructure][6 - k]
									.increaseDimensionTrainStatRow(firstOrSecondRow, dim);
						}

						factoryStructure[rowInFactoryStructure][6 - k].calculateAmounts();

					}

					// CASE 1.2
					// This case represents the scenario of a double station, e.g. "51S/41S", whose
					// right-end zone, here "41S", is yet not contained in the factoryStructure.
					// This particular scenario will probably never concretize.
					else {
						String string = "";
						if ((Integer
								.parseInt((zoneName.substring(zoneName.indexOf("/") + 1)).substring(0, 2)) >= (Integer
										.parseInt((zoneName.substring(0, zoneName.indexOf("/"))).substring(0, 2))))) {
							string = (zoneName.substring(zoneName.indexOf("/") + 1));
						} else
							string = (zoneName.substring(0, zoneName.indexOf("/")));

						int dimTrSt = (int) (Double.parseDouble(matrix[i][17].replace(',', '.')));
						int remaining = dimTrSt - dimTrSt / 2; // non dovrebbe servire

						boolean dimEven = false;
						if (dimTrSt % 2 == 0)
							dimEven = true;

						// divide rasters
						factoryStructure[rowInFactoryStructure][6 - k + 1].raster[firstOrSecondRow][(42
								- (columnNumber - 12)) - dimTrSt / 2] = new Raster(rowNumber,
										columnNumber - (dimTrSt / 2), isTrainStat);
						factoryStructure[rowInFactoryStructure][6 - k + 1].increaseLogEquip(matrix[i][6]);
						if (!isTrainStat)
							factoryStructure[rowInFactoryStructure][6 - k + 1].increaseAmountRasterRow(firstOrSecondRow,
									matrix[i][6]);
						else {
							if (dimEven)
								factoryStructure[rowInFactoryStructure][6 - k + 1]
										.increaseDimensionTrainStatRow(firstOrSecondRow, dimTrSt / 2);
							else
								factoryStructure[rowInFactoryStructure][6 - k + 1]
										.increaseDimensionTrainStatRow(firstOrSecondRow, dimTrSt / 2 + 1);
						}

						factoryStructure[rowInFactoryStructure][6 - k + 1].calculateAmounts();

						factoryStructure[rowInFactoryStructure][6 - k] = new Zone(string, 0, 0, rowInFactoryStructure,
								6 - k);
						factoryStructure[rowInFactoryStructure][6 - k].raster = new Raster[2][43];
						factoryStructure[rowInFactoryStructure][6 - k].raster[firstOrSecondRow][42
								- (columnNumber - 12)] = new Raster(rowNumber, columnNumber, isTrainStat);
						factoryStructure[rowInFactoryStructure][6 - k].increaseLogEquip(matrix[i][6]);
						if (!isTrainStat)
							factoryStructure[rowInFactoryStructure][6 - k].increaseAmountRasterRow(firstOrSecondRow,
									matrix[i][6]);

						else {
							if (dimEven)
								factoryStructure[rowInFactoryStructure][6 - k]
										.increaseDimensionTrainStatRow(firstOrSecondRow, remaining);
							else
								factoryStructure[rowInFactoryStructure][6 - k]
										.increaseDimensionTrainStatRow(firstOrSecondRow, remaining - 1);
						}

						factoryStructure[rowInFactoryStructure][6 - k].calculateAmounts();
					}
				}

				// CASE 2
				else {
					// CASE 2.1
					// The logistic equipment or the normal train station are added to the already
					// existing zone. The logistic-equipment-balance-list of the
					// zone is updated with the new logistic equipment. The zone's counting
					// variables for rasters and train stations are updated as well.
					if (!matrix[i][1].contains("/")) {
						factoryStructure[rowInFactoryStructure][6 - k].raster[firstOrSecondRow][42
								- (columnNumber - 12)] = new Raster(rowNumber, columnNumber, isTrainStat);
						factoryStructure[rowInFactoryStructure][6 - k].increaseLogEquip(matrix[i][6]);
						if (!isTrainStat)
							factoryStructure[rowInFactoryStructure][6 - k].increaseAmountRasterRow(firstOrSecondRow,
									matrix[i][6]);
						else {
							int dim = (int) (Double.parseDouble(matrix[i][17].replace(',', '.')));
							factoryStructure[rowInFactoryStructure][6 - k]
									.increaseDimensionTrainStatRow(firstOrSecondRow, dim);
						}
						factoryStructure[rowInFactoryStructure][6 - k].calculateAmounts();

					}
					// CASE 2.2
					// This case represents the scenario of a double station, which needs to be
					// shared by two adjacent zones. The double station is splitted in two parts.
					// Here, we distinguish two cases again: the station has an even length, or an
					// odd length. In the first case, two rasters are assigned to the right zone and
					// the remaining two to the left zone. In case of an odd zone, one raster more
					// will be assigned to the right zone.
					// The left zone does sometimes not even exist at this point. In this case the
					// left zone is created here and the train station is assigned.
					// For each of the two zones, the counting
					// variables for rasters and train stations are updated.
					else {
						int dimTrSt = (int) (Double.parseDouble(matrix[i][17].replace(',', '.')));
						int remaining = dimTrSt - dimTrSt / 2;

						boolean dimEven = false;
						if (dimTrSt % 2 == 0)
							dimEven = true;

						factoryStructure[rowInFactoryStructure][6 - k].raster[firstOrSecondRow][(42
								- (columnNumber - 12)) - dimTrSt / 2] = new Raster(rowNumber,
										columnNumber - (dimTrSt / 2), isTrainStat);
						factoryStructure[rowInFactoryStructure][6 - k].increaseLogEquip(matrix[i][6]);
						if (!isTrainStat)
							factoryStructure[rowInFactoryStructure][6 - k].increaseAmountRasterRow(firstOrSecondRow,
									matrix[i][6]);
						else {
							if (dimEven)
								factoryStructure[rowInFactoryStructure][6 - k]
										.increaseDimensionTrainStatRow(firstOrSecondRow, dimTrSt / 2);
							else
								factoryStructure[rowInFactoryStructure][6 - k]
										.increaseDimensionTrainStatRow(firstOrSecondRow, dimTrSt / 2 + 1);
						}

						factoryStructure[rowInFactoryStructure][6 - k].calculateAmounts();

						if (factoryStructure[rowInFactoryStructure][6 - k - 1] == null) {
							factoryStructure[rowInFactoryStructure][6 - k - 1] = new Zone(zoneName.substring(0, 3), 0,
									0, rowInFactoryStructure, 6 - k - 1);
						}
						factoryStructure[rowInFactoryStructure][6 - k - 1].raster[firstOrSecondRow][42
								- (columnNumber - 12)] = new Raster(rowNumber, columnNumber, isTrainStat);
						factoryStructure[rowInFactoryStructure][6 - k - 1].increaseLogEquip(matrix[i][6]);
						if (!isTrainStat)
							factoryStructure[rowInFactoryStructure][6 - k - 1].increaseAmountRasterRow(firstOrSecondRow,
									matrix[i][6]);
						else {
							if (dimEven)
								factoryStructure[rowInFactoryStructure][6 - k - 1]
										.increaseDimensionTrainStatRow(firstOrSecondRow, remaining);
							else
								factoryStructure[rowInFactoryStructure][6 - k - 1]
										.increaseDimensionTrainStatRow(firstOrSecondRow, remaining - 1);
						}

						factoryStructure[rowInFactoryStructure][6 - k - 1].calculateAmounts();
					}
				}
			}
		}
	}

	public ArrayList<EmptyZone> createEmptyZones(Zone[][] factoryStructure) throws Exception {
		ArrayList<EmptyZone> emptyZones = new ArrayList<EmptyZone>();

		for (int i = 0; i < factoryStructure.length; i++) {
			for (int j = 0; j < factoryStructure[0].length; j++) {
				Zone zone = factoryStructure[i][factoryStructure[0].length - j - 1];
				if (zone != null) {
					if (zone.isEmpty()) {
						EmptyZone emptyZone = new EmptyZone(zone.name, zone.amountRasterRow1, zone.amountRasterRow2, i,
								factoryStructure[0].length - j - 1);
						emptyZone.setDimensionTrainStationRow1(zone.dimensionTrainStationRow1);
						emptyZone.setDimensionTrainStationRow2(zone.dimensionTrainStationRow2);
						emptyZone.setEmpty(true);
						emptyZone.setLogisticEquipment(zone.getLogisticEquipment());
						emptyZone.calculateAmounts(); // random
						emptyZones.add(emptyZone);
					}
				}
			}
		}
		return emptyZones;
	}

	public void readEmptyImput() throws Exception {
		for (int i = 0; i < factoryStructure.length; i++) {
			for (int j = 0; j < factoryStructure[0].length; j++) {
				if (empty[i][empty[0].length - j - 1] == 1) {
					if (factoryStructure[i][factoryStructure[0].length - j - 1] != null) {
						factoryStructure[i][factoryStructure[0].length - j - 1].setEmpty(true);
					} else {
						throw new Exception("Empty zone does not exist in FactoryStructure");
					}
				}
			}
		}
	}

	/*
	 * to implement
	 */
	public ArrayList<Zone> createZonesToAllocate(Zone[][] factoryStructure) {
		ArrayList<Zone> zonesToAllocate = new ArrayList<Zone>();

		int lastRow = factoryStructure.length;
		Zone[] array = factoryStructure[lastRow - 1];

		ArrayList<Zone> temp = new ArrayList<Zone>();
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null) {
				temp.add(array[i]);
			}
		}

		int numberCycles = temp.size();
		for (int y = 0; y < numberCycles; y++) {
			int size = temp.get(0).totalNumberRaster;
			int location = 0;
			for (int i = 1; i < temp.size(); i++) {
				if (temp.get(i).totalNumberRaster > size) {
					size = temp.get(i).totalNumberRaster;
					location = i;
				}
			}
			zonesToAllocate.add(temp.get(location));
			temp.remove(location);
		}

		Zone[][] matr = new Zone[factoryStructure.length][factoryStructure[0].length];

		for (int i = 0; i < factoryStructure.length - 1; i++) {
			for (int j = 0; j < factoryStructure[0].length; j++) {
				matr[i][j] = factoryStructure[i][j];
			}
		}
		this.factoryStructure = matr;
		return zonesToAllocate;
	}

	public void totalNumberLogisticEquipment(Factory initial) {
		Zone[][] structure = initial.getFactoryStructure();
		int movements = 0;

		for (int i = 0; i < structure.length; i++) {
			for (int j = 0; j < structure[0].length; j++) {
				if (structure[i][j] != null) {
					for (int z = 0; z < structure[i][j].getLogisticEquipment().size(); z++) {
						if (structure[i][j].getLogisticEquipment().get(z).anzahl > 0)
							movements ++;
					}
				}
			}
		}
		System.out.println(movements);
	}

	/**
	 * returns the given list in reverse order
	 * 
	 * @param list
	 * @return
	 */
	public ArrayList<Zone> reverseList(ArrayList<Zone> list) {
		ArrayList<Zone> newList = new ArrayList<Zone>();
		for (int i = list.size() - 1; i >= 0; i--) {
			newList.add(list.get(i));
		}
		return newList;
	}

	public ArrayList<Zone> changeLastTwoZonesInList(ArrayList<Zone> list) {

		Zone temp = list.get(list.size() - 1);
		list.set(list.size() - 1, list.get(list.size() - 2));
		list.set(list.size() - 2, temp);
		return list;
	}

	public ArrayList<Zone> createZonesToAllocateAscending(Zone[][] factoryStructure) {
		return reverseList(createZonesToAllocate(factoryStructure));
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

	/**
	 * @return the mport
	 */
	public Import getMport() {
		return mport;
	}

	/**
	 * @param mport the mport to set
	 */
	public void setMport(Import mport) {
		this.mport = mport;
	}

	/**
	 * @return the counter
	 */
	public int[][] getCounter() {
		return counter;
	}

	/**
	 * @param counter the counter to set
	 */
	public void setCounter(int[][] counter) {
		this.counter = counter;
	}

	/**
	 * @return the isTrainStat
	 */
	public boolean isTrainStat() {
		return isTrainStat;
	}

	/**
	 * @param isTrainStat the isTrainStat to set
	 */
	public void setTrainStat(boolean isTrainStat) {
		this.isTrainStat = isTrainStat;
	}

	public boolean getTrainStat() {
		return this.isTrainStat;
	}

	/**
	 * @return the maxNumberColumn
	 */
	public int getMaxNumberColumn() {
		return maxNumberColumn;
	}

	/**
	 * @param maxNumberColumn the maxNumberColumn to set
	 */
	public void setMaxNumberColumn(int maxNumberColumn) {
		this.maxNumberColumn = maxNumberColumn;
	}

	/**
	 * @return the empty
	 */
	public int[][] getEmpty() {
		return empty;
	}

	/**
	 * @param empty the empty to set
	 */
	public void setEmpty(int[][] empty) {
		this.empty = empty;
	}

	/**
	 * @param matrix the matrix to set
	 */
	public void setMatrix(String[][] matrix) {
		this.matrix = matrix;
	}
}
