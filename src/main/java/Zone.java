
public class Zone {
	String name; //name
	
	int lastOccupiedRaster; //for calculation 
	
	int amountRasterRow1;
	int amountRasterRow2;
	int[] rows = new int[2];
	
	int posInFactoryLayout;
	
	boolean isEmpty; //in case of empty zone
	
	Raster[][] raster = new Raster[2][43];

	Zone neighbRight;
	Zone neighbLeft;
	
	public Zone (String name) {
		this.name = name;
		amountRasterRow1 = 0;
		amountRasterRow2 = 0; 
	}

	
}
