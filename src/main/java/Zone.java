import java.util.ArrayList;

public class Zone {
	String name; // name

//	int lastOccupiedRaster; // for calculation. non utilizzato? 

	int[] locationInFactory = new int[2];

	Raster[][] raster = new Raster[2][43];

	/**
	 * @return the dimensionTrainStationRow1
	 */
	public int getDimensionTrainStationRow1() {
		return dimensionTrainStationRow1;
	}

	/**
	 * @param dimensionTrainStationRow1 the dimensionTrainStationRow1 to set
	 */
	public void setDimensionTrainStationRow1(int dimensionTrainStationRow1) {
		this.dimensionTrainStationRow1 = dimensionTrainStationRow1;
	}

	/**
	 * @return the dimensionTrainStationRow2
	 */
	public int getDimensionTrainStationRow2() {
		return dimensionTrainStationRow2;
	}

	/**
	 * @param dimensionTrainStationRow2 the dimensionTrainStationRow2 to set
	 */
	public void setDimensionTrainStationRow2(int dimensionTrainStationRow2) {
		this.dimensionTrainStationRow2 = dimensionTrainStationRow2;
	}

	private boolean isEmpty = false; 

	public boolean isEmpty() {
		return isEmpty;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	int amountRasterRow1;
	int amountRasterRow2;
	int dimensionTrainStationRow1;
	int dimensionTrainStationRow2;
	int totalNumberRaster;

	private ArrayList<LogisticEquipment> logisticEquipment = new ArrayList<LogisticEquipment>();

	public Information information = null; // questa ci serve in calculator per la funzione performalgorithm

	public Zone(String name, int row1, int row2, int i, int j) {
		this.name = name;
		amountRasterRow1 = row1; // non abbiamo questa informazione quando inizializziamo la zona.
		amountRasterRow2 = row2; // --->>---
		locationInFactory[0] = i;
		locationInFactory[1] = j;
//		dimensionTrainStationRow1 = i; 
//		dimensionTrainStationRow2 = j; 

		logisticEquipment.add(new LogisticEquipment("2er XLT", 0, 2));
		logisticEquipment.add(new LogisticEquipment("3 Ebenen Regal", 0, 1));
		logisticEquipment.add(new LogisticEquipment("3 Ebenen Spez. Regal", 0, 1));
		logisticEquipment.add(new LogisticEquipment("3er XLT", 0, 3));
//		logisticEquipment.add(new LogisticEquipment("Bahnhof", 0));
		logisticEquipment.add(new LogisticEquipment("DLR", 0, 1));
		logisticEquipment.add(new LogisticEquipment("FREI", 0, 1));
		logisticEquipment.add(new LogisticEquipment("GI", 0, 1));
		logisticEquipment.add(new LogisticEquipment("GI_Scheiben", 0, 1));
		logisticEquipment.add(new LogisticEquipment("GU", 0, 2));
		logisticEquipment.add(new LogisticEquipment("GW", 0, 3));
		logisticEquipment.add(new LogisticEquipment("Leergut", 0, 1));
		logisticEquipment.add(new LogisticEquipment("Lift-Scheibe", 0, 1));
		logisticEquipment.add(new LogisticEquipment("SÄULE", 0, 1));
		logisticEquipment.add(new LogisticEquipment("XU_groß", 0, 3));
		logisticEquipment.add(new LogisticEquipment("XW_groß", 0, 4));
		logisticEquipment.add(new LogisticEquipment("XW", 0, 4));
		logisticEquipment.add(new LogisticEquipment("4er XLT", 0, 4));
		
		calculateAmounts();
	}

	/**
	 * Calculates / updates the count of rasters contained in this zone
	 */
	public void calculateAmounts() {
		totalNumberRaster = amountRasterRow1 + amountRasterRow2 + dimensionTrainStationRow1 + dimensionTrainStationRow2;
	}

	/**
	 * @return the logisticEquipment
	 */
	public ArrayList<LogisticEquipment> getLogisticEquipment() {
		return logisticEquipment;
	}

	/**
	 * @param logisticEquipment the logisticEquipment to set
	 */
	public void setLogisticEquipment(ArrayList<LogisticEquipment> logisticEquipment) {
		this.logisticEquipment = logisticEquipment;
	}

	/**
	 * Returns the dimension of the given logistic equipment
	 * 
	 * @param string
	 * @return
	 * @throws Exception
	 */
	public int getLogEquipDim(String string) throws Exception {
		for (int i = 0; i < logisticEquipment.size(); i++) {
			if (logisticEquipment.get(i).getName().equals(string))
				return logisticEquipment.get(i).getDimension();
		}

		throw new Exception("logEquipNotFoundForDimension");
	}

	/**
	 * Increases the size of amountRasterRow1 or amountRasterRow2 by the dimension
	 * of the given logistic equipment
	 * 
	 * @param row
	 * @param name
	 * @throws Exception
	 */
	public void increaseAmountRasterRow(int row, String name) throws Exception {
		if (row == 0)
			amountRasterRow1 += getLogEquipDim(name);
		else
			amountRasterRow2 += getLogEquipDim(name);
	}

	/**
	 * Increases the given logistic equipment in the logistic-equipment-balance-list
	 * of this zone by 1
	 * 
	 * @param name
	 * @throws Exception
	 */
	public void increaseLogEquip(String name) throws Exception {
		// here, we do not consider bahnhof
		if (name.equals("Bahnhof"))
			return;
		int i;
		for (i = 0; i < logisticEquipment.size(); i++) {
			if (logisticEquipment.get(i).getName().equals(name))
				break;
		}

		if (i == logisticEquipment.size()) {
			System.out.println(this.name + " " + name);
			throw new Exception("Logisitc Equipment not contained in list");
		}

		logisticEquipment.get(i).anzahlSteigern();
	}

	/**
	 * Increases the size of dimensionTrainStationRow1 or dimensionTrainStationRow2
	 * depending on dim
	 * 
	 * @param row
	 * @param dim
	 */
	public void increaseDimensionTrainStatRow(int row, int dim) {
		if (row == 0) {
			dimensionTrainStationRow1 += dim;
		} else
			dimensionTrainStationRow2 += dim;
	}

	@Override
	public String toString() {
		return name + ":    raster1: " + amountRasterRow1 + ", raster2: " + amountRasterRow2 + ", dimTrainStat1: "
				+ dimensionTrainStationRow1 + ", dimTrainStat2: " + dimensionTrainStationRow2 + ", tot: "
				+ totalNumberRaster + " isEmpty: " + isEmpty +  "\n" + "LogEquip: " + logisticEquipment;
	}
}
