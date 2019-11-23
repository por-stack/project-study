
public class Zone {
	String name; // name

	int lastOccupiedRaster; // for calculation

	int amountRasterRow1;
	int amountRasterRow2;
	int[] rows = new int[2];

	int posInFactoryLayout;

	boolean isEmpty; // in case of empty zone

	Raster[][] raster = new Raster[2][43];

	int dimensionTrainStationRow1;
	int dimensionTrainStationRow2;

	int totalNumberRaster = amountRasterRow1 + amountRasterRow2 + dimensionTrainStationRow1 + dimensionTrainStationRow2;

	Zone neighbRight;
	Zone neighbLeft;

	public Zone(String name) {
		this.name = name;
		amountRasterRow1 = 0;
		amountRasterRow2 = 0;
	}

}
