import java.util.ArrayList;


public class Zone {
	String name; // name

//	int lastOccupiedRaster; // for calculation. non utilizzato? 
	
	int[] locationInFactory = new int[2];  
	
	Raster[][] raster = new Raster[2][43];

	boolean isEmpty; // in case of empty zone

	int amountRasterRow1;
	int amountRasterRow2;
	int dimensionTrainStationRow1;
	int dimensionTrainStationRow2;
	int totalNumberRaster = amountRasterRow1 + amountRasterRow2 + dimensionTrainStationRow1 + dimensionTrainStationRow2;

	private ArrayList<LogisticEquipment> logisticEquipment = new ArrayList<LogisticEquipment>();
	
	public Information information = null; //questa ci serve in calculator per la funzione performalgorithm 

	public Zone(String name, int row1, int row2, int i, int j) {
		this.name = name;
		amountRasterRow1 = row1;
		amountRasterRow2 = row2;
		locationInFactory[0] = i; 
		locationInFactory[1] = j; 
		
		logisticEquipment.add(new LogisticEquipment("2er XLT", 0));
		logisticEquipment.add(new LogisticEquipment("3 Ebenen Regal", 0));
		logisticEquipment.add(new LogisticEquipment("3 Ebenen Spez. Regal", 0));
		logisticEquipment.add(new LogisticEquipment("3er XLT", 0));
		logisticEquipment.add(new LogisticEquipment("Bahnhof", 0));
		logisticEquipment.add(new LogisticEquipment("DLR", 0));
		logisticEquipment.add(new LogisticEquipment("FREI", 0));
		logisticEquipment.add(new LogisticEquipment("GI", 0));
		logisticEquipment.add(new LogisticEquipment("GI_Scheiben", 0));
		logisticEquipment.add(new LogisticEquipment("GU", 0));
		logisticEquipment.add(new LogisticEquipment("GW", 0));
		logisticEquipment.add(new LogisticEquipment("Leergut", 0));
		logisticEquipment.add(new LogisticEquipment("Lift-Scheibe", 0));
		logisticEquipment.add(new LogisticEquipment("SÄULE", 0));
		logisticEquipment.add(new LogisticEquipment("XU_groß", 0));
		logisticEquipment.add(new LogisticEquipment("XW_groß", 0));
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

}
