
public class Zone {
	String name; // name

	int lastOccupiedRaster; // for calculation
	int[] rows = new int[2];
	int posInFactoryLayout;
	Raster[][] raster = new Raster[2][43];

	boolean isEmpty; // in case of empty zone

	int amountRasterRow1;
	int amountRasterRow2;
	int dimensionTrainStationRow1;
	int dimensionTrainStationRow2;
	int totalNumberRaster = amountRasterRow1 + amountRasterRow2 + dimensionTrainStationRow1 + dimensionTrainStationRow2;



	public Zone(String name, int row1, int row2) {
		this.name = name;
		amountRasterRow1 = row1;
		amountRasterRow2 = row2;
	}

}
